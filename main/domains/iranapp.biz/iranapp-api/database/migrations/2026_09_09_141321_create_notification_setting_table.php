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
        Schema::create('notification_setting', function (Blueprint $table) {
            $table->increments('id');
            $table->text('fcm_token');
            $table->unsignedTinyInteger('send_ads_notifications')->default(1);
            $table->unsignedTinyInteger('send_news_notifications')->default(1);
            $table->unsignedInteger('city_id')->nullable()->index('city_id');
            $table->dateTime('created_at');
            $table->dateTime('updated_at');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('notification_setting');
    }
};
