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
        Schema::create('notification', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('category_id')->nullable()->index('category_id');
            $table->unsignedInteger('news_id')->nullable()->index('news_id');
            $table->text('msg_text');
            $table->integer('successfully_sent');
            $table->integer('failures_on_send');
            $table->dateTime('created_at');
            $table->dateTime('updated_at');

            $table->index(['news_id'], 'news_id_2');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('notification');
    }
};
