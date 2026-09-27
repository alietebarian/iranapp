<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Asked at sign-up from now on; users who registered before stay null and are never asked.
     * Stored in the Gregorian calendar, but birthdays are greeted on the Jalali date
     * (see App\Support\Birthdays).
     */
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->date('birth_date')->nullable()->after('last_name');
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropColumn('birth_date');
        });
    }
};
