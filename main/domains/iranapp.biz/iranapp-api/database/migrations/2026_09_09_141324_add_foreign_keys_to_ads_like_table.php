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
        Schema::table('ads_like', function (Blueprint $table) {
            $table->foreign(['ads_id'], 'ads_like_ibfk_1')->references(['id'])->on('ads')->onUpdate('cascade')->onDelete('cascade');
            $table->foreign(['user_id'], 'ads_like_ibfk_2')->references(['id'])->on('users')->onUpdate('cascade')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('ads_like', function (Blueprint $table) {
            $table->dropForeign('ads_like_ibfk_1');
            $table->dropForeign('ads_like_ibfk_2');
        });
    }
};
