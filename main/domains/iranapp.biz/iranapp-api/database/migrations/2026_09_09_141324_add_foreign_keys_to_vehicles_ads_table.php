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
        Schema::table('vehicles_ads', function (Blueprint $table) {
            $table->foreign(['brand_id'], 'vehicles_ads_ibfk_1')->references(['id'])->on('brand')->onUpdate('no action')->onDelete('no action');
            $table->foreign(['model_id'], 'vehicles_ads_ibfk_2')->references(['id'])->on('model')->onUpdate('no action')->onDelete('no action');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('vehicles_ads', function (Blueprint $table) {
            $table->dropForeign('vehicles_ads_ibfk_1');
            $table->dropForeign('vehicles_ads_ibfk_2');
        });
    }
};
