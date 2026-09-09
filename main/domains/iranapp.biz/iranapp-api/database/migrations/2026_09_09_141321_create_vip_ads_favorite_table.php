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
        Schema::create('vip_ads_favorite', function (Blueprint $table) {
            $table->integer('ads_id')->index('ads_id_2');
            $table->unsignedInteger('user_id')->index('user_id');
            $table->enum('ads_type', ['vehicles', 'employs', 'estates']);

            $table->unique(['ads_id', 'user_id', 'ads_type'], 'ads_id');
            $table->index(['user_id'], 'user_id_2');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('vip_ads_favorite');
    }
};
