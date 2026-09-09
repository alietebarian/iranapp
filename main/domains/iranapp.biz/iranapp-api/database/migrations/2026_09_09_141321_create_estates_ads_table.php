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
        Schema::create('estates_ads', function (Blueprint $table) {
            $table->increments('id');
            $table->string('thumbnail_photo', 200)->nullable();
            $table->unsignedInteger('region_id')->index('region_id');
            $table->unsignedInteger('user_id')->index('user_id');
            $table->unsignedInteger('category_id')->index('category_id');
            $table->enum('user_type', ['moshaver_amlak', 'person']);
            $table->boolean('is_in_hoome')->default(false);
            $table->enum('sell_or_buy', ['sell', 'buy']);
            $table->enum('ejare_or_kharid', ['ejare', 'kharid']);
            $table->decimal('price_kharid', 65, 0)->nullable();
            $table->decimal('pre_pay_ejare', 65, 0)->nullable();
            $table->decimal('monthly_price_ejare', 65, 0)->nullable();
            $table->bigInteger('rooms_count')->nullable();
            $table->string('meters', 200);
            $table->enum('type_karbari', ['maskooni', 'edari_tejari']);
            $table->unsignedTinyInteger('sanad_edari')->nullable();
            $table->date('valid_since');
            $table->date('valid_until');
            $table->enum('status', ['pending', 'approved', 'rejected', 'deleted'])->default('pending');
            $table->string('ads_title', 200);
            $table->text('description')->nullable();
            $table->double('latitude')->nullable();
            $table->double('longitude')->nullable();
            $table->text('address')->nullable();
            $table->string('telephone1', 200)->nullable();
            $table->string('telephone2', 200)->nullable();
            $table->string('ads_owner_name', 200)->nullable();
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
        Schema::dropIfExists('estates_ads');
    }
};
