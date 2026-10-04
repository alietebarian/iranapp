<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * The national code of the card's first member (the person the card is issued to). The other
     * members only give their names. Nullable: cards requested before this was asked have none.
     */
    public function up(): void
    {
        Schema::table('membership_cards', function (Blueprint $table) {
            $table->char('national_code', 10)->nullable()->after('members');
        });
    }

    public function down(): void
    {
        Schema::table('membership_cards', function (Blueprint $table) {
            $table->dropColumn('national_code');
        });
    }
};
