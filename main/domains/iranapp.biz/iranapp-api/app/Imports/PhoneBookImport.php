<?php

namespace App\Imports;

use App\Models\PhoneBookEntry;
use Illuminate\Support\Facades\DB;
use PhpOffice\PhpSpreadsheet\IOFactory;
use XMLReader;
use ZipArchive;

/**
 * Reads an Excel/CSV file of contacts into phone_book_entries, from every sheet of the file.
 *
 * Files may be up to 100 MB, which PhpSpreadsheet (and so maatwebsite/excel) cannot load: it keeps
 * every cell in memory. xlsx and csv are therefore streamed row by row; only xls, whose sheets are
 * limited to 65,536 rows, goes through PhpSpreadsheet.
 *
 * Columns are found from a header row when the sheet has one (e.g. «نام و نام خانوادگی»,
 * «شماره تلفن», «صنف», in any order); without a header the columns are taken as name, phone,
 * guild. Rows without a usable phone number are skipped, and so are numbers already in the phone
 * book (phone is unique).
 */
class PhoneBookImport
{
    const PHONE_HEADER = '/تلفن|شماره|موبایل|همراه|تماس|phone|mobile|tel/iu';
    const GUILD_HEADER = '/صنف|شغل|رسته|guild|job/iu';
    const NAME_HEADER = '/نام|name/iu';

    const BATCH_SIZE = 1000;

    /** Rows added to the phone book. */
    public int $imported = 0;

    /** Rows whose phone number was already in the phone book (or earlier in the file). */
    public int $duplicates = 0;

    /** Rows without a usable phone number. */
    public int $invalid = 0;

    /** @var array{name: int, phone: int, guild: int}|null Columns of the current sheet; null until its first row. */
    private ?array $columns = null;

    private array $batch = [];

    /** @throws \RuntimeException when the file cannot be read. */
    public function import(string $path, string $extension): void
    {
        switch (strtolower($extension)) {
            case 'xlsx':
                $this->readXlsx($path);
                break;
            case 'csv':
            case 'txt':
                $this->readCsv($path);
                break;
            default:
                $this->readWithPhpSpreadsheet($path);
        }
    }

    private function startSheet(): void
    {
        $this->flush();
        $this->columns = null;
    }

    private function addRow(array $row): void
    {
        if (!array_filter($row, fn ($cell) => $cell !== null && trim((string) $cell) !== '')) {
            return;
        }
        if ($this->columns === null) {
            $this->columns = $this->headerColumns($row);
            if ($this->columns) {
                return;
            }
            $this->columns = ['name' => 0, 'phone' => 1, 'guild' => 2];
        }

        $phone = PhoneBookEntry::normalizePhone($row[$this->columns['phone']] ?? '');
        if (strlen($phone) < 5 || strlen($phone) > 20) {
            $this->invalid++;

            return;
        }
        $now = now();
        $this->batch[] = [
            'full_name' => $this->text($row[$this->columns['name']] ?? null),
            'phone' => $phone,
            'guild' => $this->text($row[$this->columns['guild']] ?? null),
            'created_at' => $now,
            'updated_at' => $now,
        ];
        if (count($this->batch) >= self::BATCH_SIZE) {
            $this->flush();
        }
    }

    private function flush(): void
    {
        if ($this->batch === []) {
            return;
        }
        $added = DB::table('phone_book_entries')->insertOrIgnore($this->batch);
        $this->imported += $added;
        $this->duplicates += count($this->batch) - $added;
        $this->batch = [];
    }

    /**
     * Column indexes by field when $row is a header row, otherwise null. The phone and guild
     * headers are matched first, since «شماره تلفن» and «نام صنف» would also match the name.
     *
     * @return array{name: int, phone: int, guild: int}|null
     */
    private function headerColumns(array $row): ?array
    {
        $found = [];
        foreach (['phone' => self::PHONE_HEADER, 'guild' => self::GUILD_HEADER, 'name' => self::NAME_HEADER] as $field => $pattern) {
            foreach ($row as $index => $cell) {
                if (!in_array($index, $found, true) && is_string($cell) && preg_match($pattern, $cell)) {
                    $found[$field] = $index;
                    break;
                }
            }
        }
        if (!isset($found['phone'])) {
            return null;
        }

        // A header that names only some of the columns: the others keep their usual place.
        $rest = array_values(array_diff([0, 1, 2], $found));
        foreach (['name', 'guild'] as $field) {
            if (!isset($found[$field])) {
                $found[$field] = array_shift($rest) ?? -1;
            }
        }

        return $found;
    }

    private function text($value): ?string
    {
        if ($value === null) {
            return null;
        }
        $value = trim(preg_replace('/\s+/u', ' ', PhoneBookEntry::toPersianLetters((string) $value)));

        return $value === '' ? null : mb_substr($value, 0, 150);
    }

    // ---------------------------------------------------------------- xlsx

    private function readXlsx(string $path): void
    {
        $zip = new ZipArchive();
        if ($zip->open($path) !== true) {
            throw new \RuntimeException('Not an xlsx file.');
        }
        $sheets = [];
        for ($i = 0; $i < $zip->numFiles; $i++) {
            $name = $zip->getNameIndex($i);
            if (preg_match('#^xl/worksheets/sheet(\d+)\.xml$#', $name, $m)) {
                $sheets[(int) $m[1]] = $name;
            }
        }
        $hasSharedStrings = $zip->locateName('xl/sharedStrings.xml') !== false;
        $zip->close();
        if ($sheets === []) {
            throw new \RuntimeException('The xlsx file has no sheets.');
        }
        ksort($sheets);

        $strings = $hasSharedStrings ? $this->readSharedStrings($path) : [];
        foreach ($sheets as $sheet) {
            $this->startSheet();
            $this->readXlsxSheet($path, $sheet, $strings);
        }
        $this->flush();
    }

    /** The shared strings table: the text of each <si>, without its phonetic (<rPh>) runs. */
    private function readSharedStrings(string $path): array
    {
        $reader = $this->openZipXml($path, 'xl/sharedStrings.xml');
        $strings = [];
        $text = null;
        $phoneticDepth = 0;
        while ($reader->read()) {
            if ($reader->nodeType == XMLReader::ELEMENT) {
                if ($reader->localName == 'si') {
                    $text = '';
                    if ($reader->isEmptyElement) {
                        $strings[] = '';
                        $text = null;
                    }
                } elseif ($reader->localName == 'rPh' && !$reader->isEmptyElement) {
                    $phoneticDepth++;
                } elseif ($reader->localName == 't' && $text !== null && $phoneticDepth == 0) {
                    $text .= $reader->readString();
                }
            } elseif ($reader->nodeType == XMLReader::END_ELEMENT) {
                if ($reader->localName == 'rPh') {
                    $phoneticDepth--;
                } elseif ($reader->localName == 'si') {
                    $strings[] = $text;
                    $text = null;
                }
            }
        }
        $reader->close();

        return $strings;
    }

    private function readXlsxSheet(string $path, string $sheet, array $strings): void
    {
        $reader = $this->openZipXml($path, $sheet);
        $row = [];
        $column = 0;
        $type = null;
        $value = null;
        while ($reader->read()) {
            if ($reader->nodeType == XMLReader::ELEMENT) {
                switch ($reader->localName) {
                    case 'row':
                        $row = [];
                        $column = 0;
                        if ($reader->isEmptyElement) {
                            $this->addRow([]);
                        }
                        break;
                    case 'c':
                        $ref = $reader->getAttribute('r');
                        $column = $ref ? $this->columnIndex($ref) : count($row);
                        $type = $reader->getAttribute('t') ?: 'n';
                        $value = null;
                        if ($reader->isEmptyElement) {
                            $row[$column] = null;
                        }
                        break;
                    case 'v':
                    case 't': // <is><t> of an inline string
                        $value = ($value ?? '') . $reader->readString();
                        break;
                }
            } elseif ($reader->nodeType == XMLReader::END_ELEMENT) {
                if ($reader->localName == 'c') {
                    $row[$column] = $this->cellValue($type, $value, $strings);
                } elseif ($reader->localName == 'row') {
                    $this->addRow($this->fillGaps($row));
                }
            }
        }
        $reader->close();
    }

    private function cellValue(string $type, ?string $value, array $strings)
    {
        if ($value === null) {
            return null;
        }
        switch ($type) {
            case 's':
                return $strings[(int) $value] ?? null;
            case 'n':
                // A number typed into Excel, e.g. a phone number without its leading zero; large
                // ones may be stored as 9.121234567E9.
                return is_numeric($value) && preg_match('/[eE.]/', $value) ? (float) $value : $value;
            case 'e':
                return null;
            default: // str, inlineStr, b
                return $value;
        }
    }

    /** "C12" → 2 */
    private function columnIndex(string $ref): int
    {
        $letters = preg_replace('/\d/', '', strtoupper($ref));
        $index = 0;
        foreach (str_split($letters) as $letter) {
            $index = $index * 26 + (ord($letter) - 64);
        }

        return $index - 1;
    }

    private function fillGaps(array $row): array
    {
        if ($row === []) {
            return [];
        }
        $filled = array_fill(0, max(array_keys($row)) + 1, null);

        return array_replace($filled, $row);
    }

    private function openZipXml(string $path, string $entry): XMLReader
    {
        $reader = new XMLReader();
        if (!$reader->open('zip://' . $path . '#' . $entry, null, LIBXML_NONET | LIBXML_COMPACT)) {
            throw new \RuntimeException('Cannot read ' . $entry . ' of the xlsx file.');
        }

        return $reader;
    }

    // ---------------------------------------------------------------- csv

    private function readCsv(string $path): void
    {
        $handle = fopen($path, 'r');
        if (!$handle) {
            throw new \RuntimeException('Cannot open the csv file.');
        }
        $first = (string) fgets($handle);
        rewind($handle);
        if (str_starts_with($first, "\xEF\xBB\xBF")) {
            fread($handle, 3);
        }
        $delimiter = $this->csvDelimiter($first);
        // Excel saves Persian CSV files in Windows-1256 unless told to use UTF-8.
        $utf8 = mb_check_encoding($first, 'UTF-8');

        $this->startSheet();
        while (($row = fgetcsv($handle, 0, $delimiter, '"', '')) !== false) {
            if (!$utf8) {
                $row = array_map(fn ($cell) => $cell === null ? null : iconv('Windows-1256', 'UTF-8//IGNORE', $cell), $row);
            }
            $this->addRow($row);
        }
        fclose($handle);
        $this->flush();
    }

    private function csvDelimiter(string $line): string
    {
        $counts = [',' => substr_count($line, ','), ';' => substr_count($line, ';'), "\t" => substr_count($line, "\t")];
        arsort($counts);

        return array_key_first($counts);
    }

    // ---------------------------------------------------------------- xls

    private function readWithPhpSpreadsheet(string $path): void
    {
        $reader = IOFactory::createReaderForFile($path);
        $reader->setReadDataOnly(true);
        $spreadsheet = $reader->load($path);
        foreach ($spreadsheet->getWorksheetIterator() as $worksheet) {
            $this->startSheet();
            foreach ($worksheet->toArray(null, false, false, false) as $row) {
                $this->addRow($row);
            }
        }
        $spreadsheet->disconnectWorksheets();
        $this->flush();
    }
}
