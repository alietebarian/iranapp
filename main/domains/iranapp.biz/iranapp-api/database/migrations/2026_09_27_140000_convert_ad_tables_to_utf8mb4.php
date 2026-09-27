<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * The ad tables came from the legacy database as 3-byte utf8 (utf8mb3), while the app connects
     * as utf8mb4. Any ad whose text held a 4-byte character — an emoji from the phone keyboard —
     * failed to save with MySQL error 3988 and the app showed «در ثبت آگهی به مشکل برخوردیم».
     */
    const TABLES = ['ads', 'vehicles_ads', 'estates_ads', 'employs_ads'];

    public function up(): void
    {
        if (DB::getDriverName() !== 'mysql') {
            return;
        }

        foreach (self::TABLES as $table) {
            $collation = DB::selectOne(
                'select TABLE_COLLATION as collation from information_schema.TABLES where TABLE_SCHEMA = ? and TABLE_NAME = ?',
                [DB::getDatabaseName(), $table]
            )?->collation;

            if ($collation === null || str_starts_with($collation, 'utf8mb4')) {
                continue;
            }

            // Keep Persian sorting where the table already used it.
            $target = str_contains($collation, 'persian') ? 'utf8mb4_persian_ci' : 'utf8mb4_unicode_ci';
            DB::statement("ALTER TABLE `{$table}` CONVERT TO CHARACTER SET utf8mb4 COLLATE {$target}");
        }
    }

    /** Not reversed: converting back to utf8mb3 would fail on (or mangle) ads that now hold emoji. */
    public function down(): void
    {
    }
};
