<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * One row per installation of the app, reported by the app itself on first launch with a
     * random ID it generates and keeps. Unlike notification_setting (which only gets a row once
     * Firebase hands out a token), this does not depend on Google services being reachable.
     */
    public function up(): void
    {
        Schema::create('app_installs', function (Blueprint $table) {
            $table->increments('id');
            $table->string('install_id', 64)->unique();
            // Where the APK came from: bazaar, google_play, myket, direct (no installer) or other.
            $table->string('source', 20);
            // The installer's package name as Android reported it, kept for sources not mapped yet.
            $table->string('installer_package', 150)->nullable();
            // True when an older version of the app was already on the device before the first
            // version that reports installs; such devices are already counted via notification_setting.
            $table->boolean('is_upgrade')->default(false);
            $table->string('app_version', 40)->nullable();
            $table->string('android_version', 20)->nullable();
            $table->string('device_model', 100)->nullable();
            $table->timestamps();

            $table->index('created_at');
            $table->index(['source', 'created_at']);
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('app_installs');
    }
};
