<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * The admin panel's bell also reports electronic contracts that end within 15 days. A row is
     * about either an ad (ads_id) or a contract (e_contract_id); valid_until is the date it ends.
     */
    public function up(): void
    {
        Schema::table('admin_notifications', function (Blueprint $table) {
            $table->unsignedInteger('ads_id')->nullable()->change();
            $table->unsignedInteger('e_contract_id')->nullable()->after('ads_id');

            $table->unique(['e_contract_id', 'valid_until'], 'e_contract_id_valid_until');
            $table->foreign('e_contract_id')->references('id')->on('e_contracts')->onDelete('cascade');
        });
    }

    public function down(): void
    {
        Schema::table('admin_notifications', function (Blueprint $table) {
            $table->dropForeign(['e_contract_id']);
            $table->dropUnique('e_contract_id_valid_until');
            $table->dropColumn('e_contract_id');
        });
    }
};
