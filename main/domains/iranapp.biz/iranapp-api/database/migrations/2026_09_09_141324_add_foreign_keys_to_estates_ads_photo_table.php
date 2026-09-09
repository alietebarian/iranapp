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
        Schema::table('estates_ads_photo', function (Blueprint $table) {
            $table->foreign(['estates_ads_id'], 'estates_ads_photo_ibfk_1')->references(['id'])->on('estates_ads')->onUpdate('no action')->onDelete('no action');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('estates_ads_photo', function (Blueprint $table) {
            $table->dropForeign('estates_ads_photo_ibfk_1');
        });
    }
};
