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
        Schema::create('vip_ads', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('ads_id')->index('ads_id');
            $table->unsignedTinyInteger('show_in_city')->default(1);
            $table->unsignedTinyInteger('show_in_province')->default(0);
            $table->unsignedTinyInteger('show_in_main_page')->default(0);
            $table->unsignedTinyInteger('show_in_country')->default(0);
            $table->unsignedTinyInteger('show_in_category')->default(1);
            $table->unsignedTinyInteger('show_in_subcategory')->default(0);
            $table->string('photo', 300);

            $table->unique(['ads_id'], 'ads_id_2');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('vip_ads');
    }
};
