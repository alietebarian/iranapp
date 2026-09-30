<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

/**
 * A contact of the admin panel's «دفترچه تلفن», imported from an Excel file.
 */
class PhoneBookEntry extends Model
{
    protected $table = 'phone_book_entries';

    protected $fillable = ['full_name', 'phone', 'guild'];

    /**
     * A phone number as stored: Persian/Arabic digits converted and everything but digits removed.
     * Excel drops the leading zero of numbers typed as numbers (9121234567), so a 10 digit number
     * gets it back.
     */
    public static function normalizePhone($value): string
    {
        if (is_float($value)) {
            $value = sprintf('%.0f', $value);
        }
        $digits = preg_replace('/\D/', '', self::toEnglishDigits((string) $value));
        if (strlen($digits) == 10 && $digits[0] != '0') {
            $digits = '0' . $digits;
        }

        return $digits;
    }

    /**
     * Arabic ي and ك (what Windows-1256 files and Arabic keyboards give) as the Persian ی and ک,
     * so a name or guild is found whichever the file or the search used.
     */
    public static function toPersianLetters(string $text): string
    {
        return strtr($text, ['ي' => 'ی', 'ى' => 'ی', 'ك' => 'ک']);
    }

    public static function toEnglishDigits(string $text): string
    {
        return strtr($text, [
            '۰' => '0', '۱' => '1', '۲' => '2', '۳' => '3', '۴' => '4',
            '۵' => '5', '۶' => '6', '۷' => '7', '۸' => '8', '۹' => '9',
            '٠' => '0', '١' => '1', '٢' => '2', '٣' => '3', '٤' => '4',
            '٥' => '5', '٦' => '6', '٧' => '7', '٨' => '8', '٩' => '9',
        ]);
    }
}
