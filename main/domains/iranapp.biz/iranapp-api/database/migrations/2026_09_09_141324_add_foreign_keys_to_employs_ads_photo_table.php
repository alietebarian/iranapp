<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('employs_ads_photo', function (Blueprint $table) {
            $table->foreign(['employs_ads_id'], 'employs_ads_photo_ibfk_1')->references(['id'])->on('employs_ads')->onUpdate('no action')->onDelete('no action');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('employs_ads_photo', function (Blueprint $table) {
            $table->dropForeign('employs_ads_photo_ibfk_1');
        });
    }
};
