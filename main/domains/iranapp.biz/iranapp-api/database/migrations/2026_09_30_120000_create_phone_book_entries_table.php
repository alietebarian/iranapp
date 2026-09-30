<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * The admin panel's «دفترچه تلفن»: contacts imported from Excel files.
     *
     * phone is unique, so importing a file twice, or a file that overlaps an earlier one, does not
     * add the same number again.
     */
    public function up(): void
    {
        Schema::create('phone_book_entries', function (Blueprint $table) {
            $table->increments('id');
            $table->string('full_name', 150)->nullable();
            // Digits only (Persian digits converted), e.g. 09121234567.
            $table->string('phone', 20)->unique();
            $table->string('guild', 150)->nullable();
            $table->timestamps();

            $table->index('guild');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('phone_book_entries');
    }
};
