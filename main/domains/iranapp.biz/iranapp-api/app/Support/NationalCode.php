<?php

namespace App\Support;

use App\Libraries\jdf;

/**
 * Iranian national ID (کد ملی): 10 digits, the last one a check digit.
 * Used by the electronic contract and the membership card.
 */
class NationalCode
{
    public static function isValid(?string $code): bool
    {
        $code = (string) $code;
        if (! preg_match('/^\d{10}$/', $code) || preg_match('/^(\d)\1{9}$/', $code)) {
            return false;
        }
        $sum = 0;
        for ($i = 0; $i < 9; $i++) {
            $sum += (int) $code[$i] * (10 - $i);
        }
        $remainder = $sum % 11;
        $check = (int) $code[9];

        return $remainder < 2 ? $check === $remainder : $check === 11 - $remainder;
    }

    /** Persian or Arabic digits to Latin, spaces and dashes removed. */
    public static function normalize(?string $code): string
    {
        $code = strtr((string) $code, [
            '٠' => '0', '١' => '1', '٢' => '2', '٣' => '3', '٤' => '4',
            '٥' => '5', '٦' => '6', '٧' => '7', '٨' => '8', '٩' => '9',
        ]);

        return preg_replace('/[\s\-]/', '', jdf::tr_num($code, 'en'));
    }
}
