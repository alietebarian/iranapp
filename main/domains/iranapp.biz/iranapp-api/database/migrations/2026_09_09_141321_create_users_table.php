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
        Schema::create('users', function (Blueprint $table) {
            $table->increments('id');
            $table->string('first_name', 200);
            $table->string('last_name', 200);
            $table->string('mobile', 200)->unique('mobile');
            $table->text('password');
            $table->text('remember_token')->nullable();
            $table->string('verify_token', 200)->nullable();
            $table->unsignedTinyInteger('is_mobile_verified')->default(0);
            $table->enum('type', ['user', 'admin'])->default('user');
            $table->text('fcm_token')->nullable();
            $table->unsignedInteger('forget_password_token')->nullable();
            $table->unsignedTinyInteger('send_news_notifications')->default(1);
            $table->unsignedTinyInteger('send_ads_notifications')->default(1);
            $table->string('reset_password_token', 200)->nullable();
            $table->dateTime('created_at');
            $table->dateTime('updated_at');

            $table->fullText(['first_name', 'last_name'], 'first_name');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('users');
    }
};
