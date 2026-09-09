<?php

namespace App\Exports;

use Illuminate\Support\Facades\DB;
use Maatwebsite\Excel\Concerns\FromArray;
use Maatwebsite\Excel\Concerns\WithHeadings;
use Maatwebsite\Excel\Concerns\WithTitle;

/**
 * Admin export of user phone numbers.
 *
 * The legacy version used maatwebsite/excel v2's Excel::create() closure API, which was
 * built on the abandoned PHPExcel. v3+ expresses the same thing as an export class.
 */
class UserMobilesExport implements FromArray, WithHeadings, WithTitle
{
    public function array(): array
    {
        return DB::table('users')
            ->select('first_name', 'last_name', 'mobile')
            ->get()
            ->map(fn ($user) => [$user->first_name, $user->last_name, $user->mobile])
            ->toArray();
    }

    public function headings(): array
    {
        return ['نام', 'نام خانوادگی', 'تلفن همراه'];
    }

    public function title(): string
    {
        return 'users';
    }
}
