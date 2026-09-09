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
        Schema::create('ads', function (Blueprint $table) {
            $table->increments('id');
            $table->string('title', 300);
            $table->double('latitude')->nullable()->default(0);
            $table->double('longitude')->nullable()->default(0);
            $table->unsignedInteger('city_id')->index('city_id');
            $table->unsignedInteger('user_id')->nullable()->index('user_id');
            $table->integer('visitor_id')->nullable()->index('visitor_id');
            $table->text('address')->nullable();
            $table->enum('type', ['discount', 'need']);
            $table->unsignedInteger('sub_category_id')->index('sub_category_id');
            $table->unsignedInteger('updates_count')->default(0);
            $table->string('email', 200)->nullable();
            $table->string('mobile', 200)->nullable();
            $table->string('tel1', 200)->nullable();
            $table->string('tel2', 200)->nullable();
            $table->string('link', 500)->nullable();
            $table->string('discount', 300)->nullable();
            $table->string('working_time', 500)->nullable();
            $table->text('telegram')->nullable();
            $table->text('instagram')->nullable();
            $table->text('notes')->nullable();
            $table->string('shaba', 191)->nullable();
            $table->enum('status', ['pending', 'approved', 'rejected']);
            $table->string('ads_owner_name', 200)->nullable();
            $table->unsignedInteger('ads_plan_id')->index('ads_plan_id');
            $table->date('valid_since');
            $table->date('valid_until');
            $table->dateTime('created_at');
            $table->dateTime('updated_at');

            $table->fullText(['title', 'address', 'notes'], 'title');
        });
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::dropIfExists('ads');
    }
};
