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
        // Alerts in the admin panel's top bar that an ad is about to expire.
        Schema::create('admin_notifications', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('ads_id');
            // The expiry date the alert is about; a renewed ad gets a new row for its new date.
            $table->date('valid_until');
            $table->dateTime('read_at')->nullable()->index('read_at');
            $table->dateTime('created_at');

            $table->unique(['ads_id', 'valid_until'], 'ads_id_valid_until');
            $table->foreign(['ads_id'], 'admin_notifications_ibfk_1')->references(['id'])->on('ads')->onUpdate('cascade')->onDelete('cascade');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('admin_notifications');
    }
};
