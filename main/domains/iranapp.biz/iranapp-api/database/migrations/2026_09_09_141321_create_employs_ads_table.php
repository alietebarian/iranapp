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
        Schema::create('employs_ads', function (Blueprint $table) {
            $table->increments('id');
            $table->unsignedInteger('user_id')->index('user_id');
            $table->unsignedInteger('specialty_id')->index('specialty_id');
            $table->unsignedInteger('region_id')->index('region_id');
            $table->enum('education_level', ['underdiploma', 'diploma', 'tact', 'expertise', 'masterdegree', 'doctoral']);
            $table->enum('agremment_type', ['tamamvaght', 'parevaght', 'moshaveri', 'projei']);
            $table->text('description')->nullable();
            $table->enum('type', ['karjoo', 'forsatshoghli'])->nullable();
            $table->string('thumbnail_photo')->nullable();
            $table->enum('status', ['pending', 'approved', 'rejected', 'deleted'])->default('pending');
            $table->date('valid_since');
            $table->date('valid_until');
            $table->double('longitude')->nullable();
            $table->double('latitude')->nullable();
            $table->text('address')->nullable();
            $table->string('telephone1')->nullable();
            $table->string('telephone2')->nullable();
            $table->string('ads_owner_name', 200)->nullable();
            $table->enum('person_or_company', ['person', 'company']);
            $table->string('ads_title', 200);
            $table->dateTime('created_at_2');
            $table->dateTime('created_at');
            $table->dateTime('updated_at');

            $table->fullText(['ads_title', 'description'], 'ads_title');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('employs_ads');
    }
};
