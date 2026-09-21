<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * A legal record of the user ticking "با تمامی قوانین و مقررات موافقم" before submitting an
     * ad. One row per submission, kept apart from the ad tables so the record survives edits to
     * the ad and stays readable even if the ad itself is deleted.
     */
    public function up(): void
    {
        Schema::create('ad_terms_acceptances', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('user_id')->unsigned();
            // Which ad table the acceptance belongs to: ads, estate, vehicle or employ.
            $table->string('ad_type', 20);
            $table->integer('ad_id')->unsigned();
            // The wording the user was shown, as reported by the app that sent it.
            $table->string('terms_version', 40);
            $table->timestamp('accepted_at');
            // IPv6 needs 45 characters.
            $table->string('ip_address', 45)->nullable();
            $table->string('app_version', 40)->nullable();
            $table->string('user_agent', 255)->nullable();
            $table->timestamps();

            $table->index(['ad_type', 'ad_id']);
            $table->index('user_id');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('ad_terms_acceptances');
    }
};
