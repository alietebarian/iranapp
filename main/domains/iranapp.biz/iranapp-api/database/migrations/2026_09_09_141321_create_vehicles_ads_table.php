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
        Schema::create('vehicles_ads', function (Blueprint $table) {
            $table->increments('id');
            $table->string('thumbnail_photo', 200)->nullable();
            $table->unsignedInteger('region_id')->index('region_id');
            $table->unsignedInteger('user_id')->index('user_id');
            $table->date('valid_since');
            $table->date('valid_until');
            $table->enum('status', ['pending', 'approved', 'rejected', 'deleted'])->default('pending');
            $table->string('ads_title', 200)->fulltext('ads_title');
            $table->text('description')->nullable();
            $table->double('longitude')->nullable();
            $table->double('latitude')->nullable();
            $table->text('address')->nullable();
            $table->string('telephone1', 200)->nullable();
            $table->string('telephone2', 200)->nullable();
            $table->string('ads_owner_name', 200)->nullable();
            $table->enum('type', ['khodro', 'motorcycle', 'khodroclasic', 'khordrosorn', 'lavazem', 'other']);
            $table->decimal('price', 65, 0)->nullable();
            $table->decimal('kilometre', 30, 0)->unsigned()->nullable();
            $table->integer('production_year')->nullable();
            $table->enum('chassis_type', ['savari', 'hachback', 'shasiboland', 'vanet', 'krook', 'van', 'cupe', 'station', 'other'])->nullable();
            $table->unsignedInteger('cylinder_volume')->nullable()->index('cylinder_volume');
            $table->enum('neworold', ['new', 'old'])->default('old');
            $table->integer('brand_id')->nullable()->index('brand_id');
            $table->integer('model_id')->nullable()->index('model_id');
            $table->enum('person_or_company', ['person', 'company']);
            $table->dateTime('created_at_2');
            $table->dateTime('created_at')->useCurrent();
            $table->dateTime('updated_at');

            $table->index(['cylinder_volume'], 'cylinder_volume_2');
            $table->index(['region_id'], 'region_id_2');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('vehicles_ads');
    }
};
