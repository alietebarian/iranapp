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
        Schema::table('ads', function (Blueprint $table) {
            $table->foreign(['city_id'], 'ads_ibfk_1')->references(['id'])->on('city')->onUpdate('no action')->onDelete('no action');
            $table->foreign(['sub_category_id'], 'ads_ibfk_2')->references(['id'])->on('sub_category')->onUpdate('no action')->onDelete('no action');
            $table->foreign(['ads_plan_id'], 'ads_ibfk_3')->references(['id'])->on('ads_plan')->onUpdate('no action')->onDelete('no action');
            $table->foreign(['user_id'], 'ads_ibfk_4')->references(['id'])->on('users')->onUpdate('no action')->onDelete('no action');
            $table->foreign(['visitor_id'], 'ads_ibfk_5')->references(['id'])->on('visitors')->onUpdate('no action')->onDelete('no action');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('ads', function (Blueprint $table) {
            $table->dropForeign('ads_ibfk_1');
            $table->dropForeign('ads_ibfk_2');
            $table->dropForeign('ads_ibfk_3');
            $table->dropForeign('ads_ibfk_4');
            $table->dropForeign('ads_ibfk_5');
        });
    }
};
