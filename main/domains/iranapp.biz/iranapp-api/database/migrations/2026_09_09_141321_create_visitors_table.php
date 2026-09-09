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
        Schema::create('visitors', function (Blueprint $table) {
            $table->integer('id', true);
            $table->string('first_name');
            $table->string('last_name');
            $table->string('password');
            $table->string('mobile', 20)->nullable();
            $table->enum('status', ['active', 'deactive'])->default('active');
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
        Schema::dropIfExists('visitors');
    }
};
