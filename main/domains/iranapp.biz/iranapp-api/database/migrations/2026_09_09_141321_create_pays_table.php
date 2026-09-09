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
        Schema::create('pays', function (Blueprint $table) {
            $table->increments('id');
            $table->string('shaba_code', 191)->nullable();
            $table->string('code', 191)->nullable();
            $table->unsignedInteger('user_id')->index('pays_user_id_foreign');
            $table->unsignedInteger('ads_id')->index('pays_ads_id_foreign');
            $table->decimal('price', 10, 0);
            $table->string('hash')->nullable();
            $table->string('ref_id')->nullable();
            $table->enum('status', ['initial', 'pay', 'cancel'])->default('initial');
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('pays');
    }
};
