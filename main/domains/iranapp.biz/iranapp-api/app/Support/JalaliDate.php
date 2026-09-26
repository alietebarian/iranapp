<?php

namespace App\Support;

use App\Libraries\jdf;
use Carbon\Carbon;

/**
 * Solar Hijri dates written as "1405/07/04", the way they appear in the electronic contract.
 *
 * The app does the same month arithmetic to preview the end date (EContractActivity), but the
 * end date that is stored is always the one computed here.
 */
class JalaliDate
{
    /** Today's date in Tehran, e.g. "1405/07/04". */
    public static function today(): string
    {
        [$y, $m, $d] = jdf::gregorian_to_jalali(...explode('-', Carbon::now('Asia/Tehran')->format('Y-n-j')));

        return self::format((int) $y, (int) $m, (int) $d);
    }

    /**
     * Parses "1405/7/4", "1405-07-04" or the same in Persian digits.
     *
     * @return int[]|null [year, month, day], or null when the text is not a real Jalali date
     */
    public static function parse(?string $text): ?array
    {
        $text = trim(jdf::tr_num((string) $text, 'en'));
        if (! preg_match('#^(\d{4})[/\-](\d{1,2})[/\-](\d{1,2})$#', $text, $m)) {
            return null;
        }
        [$y, $mo, $d] = [(int) $m[1], (int) $m[2], (int) $m[3]];

        return jdf::jcheckdate($mo, $d, $y) ? [$y, $mo, $d] : null;
    }

    /** Adds whole months, clamping the day to the target month's length (e.g. 6/31 + 1 → 7/30). */
    public static function addMonths(array $date, int $months): array
    {
        [$y, $m, $d] = $date;
        $index = ($m - 1) + $months;
        $y += intdiv($index, 12);
        $m = $index % 12 + 1;

        return [$y, $m, min($d, self::monthLength($y, $m))];
    }

    public static function monthLength(int $year, int $month): int
    {
        if ($month <= 6) {
            return 31;
        }
        if ($month <= 11) {
            return 30;
        }

        return jdf::jcheckdate(12, 30, $year) ? 30 : 29;
    }

    public static function format(int $y, int $m, int $d): string
    {
        return sprintf('%04d/%02d/%02d', $y, $m, $d);
    }

    public static function toCarbon(array $date): Carbon
    {
        [$gy, $gm, $gd] = jdf::jalali_to_gregorian($date[0], $date[1], $date[2]);

        return Carbon::create((int) $gy, (int) $gm, (int) $gd, 0, 0, 0, 'Asia/Tehran');
    }

    /**
     * A stored timestamp in Tehran time, e.g. "1405/07/04 - 12:43" for 'Y/m/d - H:i'.
     * Only the tokens Y, m, d, H and i are supported.
     *
     * jdf::jdate is deliberately not used: it calls date_default_timezone_set(), after which every
     * timestamp Eloquent reads in the same request is taken as Tehran time and shown 3.5 hours off.
     */
    public static function fromTimestamp($value, string $format = 'Y/m/d'): ?string
    {
        if (! $value) {
            return null;
        }

        $tehran = ($value instanceof \DateTimeInterface
            ? Carbon::instance($value)
            : Carbon::parse($value, config('app.timezone')))->setTimezone('Asia/Tehran');
        [$y, $m, $d] = jdf::gregorian_to_jalali($tehran->year, $tehran->month, $tehran->day);

        return strtr($format, [
            'Y' => sprintf('%04d', $y),
            'm' => sprintf('%02d', $m),
            'd' => sprintf('%02d', $d),
            'H' => $tehran->format('H'),
            'i' => $tehran->format('i'),
        ]);
    }
}
