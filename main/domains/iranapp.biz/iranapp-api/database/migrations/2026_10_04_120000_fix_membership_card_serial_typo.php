<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Support\Facades\DB;

return new class extends Migration
{
    /**
     * Card serials were meant to read "1394 0010 1363 ...", not "1394 0010 0363 ..." (a typo in
     * MembershipCard::FIRST_SERIAL). Adding 10,000,000 turns the "0363" group into "1363" and
     * leaves every other digit as it was, so each card keeps its own number. New cards continue
     * from the highest serial, so the existing ones must move too.
     */
    const SHIFT = 10000000;
    const OLD_FIRST = 1394001003631900;
    const NEW_FIRST = 1394001013631900;

    public function up(): void
    {
        DB::table('membership_cards')
            ->where('serial_number', '>=', self::OLD_FIRST)
            ->where('serial_number', '<', self::NEW_FIRST)
            ->update(['serial_number' => DB::raw('serial_number + ' . self::SHIFT)]);
    }

    public function down(): void
    {
        DB::table('membership_cards')
            ->where('serial_number', '>=', self::NEW_FIRST)
            ->where('serial_number', '<', self::NEW_FIRST + self::SHIFT)
            ->update(['serial_number' => DB::raw('serial_number - ' . self::SHIFT)]);
    }
};
