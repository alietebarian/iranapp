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
        Schema::create('awards', function (Blueprint $table) {
            $table->integer('id', true);
            $table->string('name', 191);
            $table->string('price', 191);
            $table->text('description')->nullable();
            $table->timestamp('updated_at')->useCurrentOnUpdate()->useCurrent();
            // The legacy schema had a '0000-00-00' default here, which modern MySQL rejects
            // in strict mode. Nullable is the standard Laravel timestamp behaviour.
            $table->timestamp('created_at')->nullable();
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('awards');
    }
};
