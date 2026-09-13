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
        // One row per time an ad's page was opened in the app, for the ad's performance page.
        Schema::create('ads_view', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('ads_id');
            // The signed-in viewer, if any; guests are counted as views too.
            $table->unsignedInteger('user_id')->nullable()->index('user_id');
            $table->dateTime('created_at');

            $table->index(['ads_id', 'created_at'], 'ads_id_created_at');
            $table->foreign(['ads_id'], 'ads_view_ibfk_1')->references(['id'])->on('ads')->onUpdate('cascade')->onDelete('cascade');
            $table->foreign(['user_id'], 'ads_view_ibfk_2')->references(['id'])->on('users')->onUpdate('cascade')->onDelete('set null');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ads_view');
    }
};
