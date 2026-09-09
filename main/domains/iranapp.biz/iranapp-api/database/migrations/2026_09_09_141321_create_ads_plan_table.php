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
        Schema::create('ads_plan', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('num_of_stars')->nullable()->default(1);
            $table->integer('ordering_factor');
            $table->unsignedInteger('max_number_of_photos')->default(3);
            $table->integer('price');
            $table->string('plan_title', 300);
            $table->unsignedInteger('num_of_updates');
            $table->unsignedInteger('interval_days');
            $table->unsignedTinyInteger('deleted')->default(0);
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ads_plan');
    }
};
