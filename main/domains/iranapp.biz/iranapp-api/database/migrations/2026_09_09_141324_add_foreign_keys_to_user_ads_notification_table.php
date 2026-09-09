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
        Schema::table('user_ads_notification', function (Blueprint $table) {
            $table->foreign(['ads_id'], 'user_ads_notification_ibfk_1')->references(['id'])->on('ads')->onUpdate('cascade')->onDelete('cascade');
            $table->foreign(['user_id'], 'user_ads_notification_ibfk_2')->references(['id'])->on('users')->onUpdate('no action')->onDelete('no action');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('user_ads_notification', function (Blueprint $table) {
            $table->dropForeign('user_ads_notification_ibfk_1');
            $table->dropForeign('user_ads_notification_ibfk_2');
        });
    }
};
