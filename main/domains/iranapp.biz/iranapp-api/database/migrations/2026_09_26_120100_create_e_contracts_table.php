<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * The electronic version of the paper contract businesses used to sign with Iran App.
     *
     * One row per submission: a rejected request stays as it was and the corrected one is a new
     * row, so the history of what the user sent and why it was turned down is kept. Alongside
     * the individual fields, the full wording the user agreed to is stored in contract_text.
     */
    public function up(): void
    {
        Schema::create('e_contracts', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('user_id')->unsigned();
            $table->enum('status', ['pending', 'approved', 'rejected'])->default('pending');

            // Second party (طرف دوم).
            $table->string('business_type', 20);
            $table->string('business_name', 200);
            $table->string('manager_title', 10);
            $table->string('manager_name', 200);
            $table->char('national_code', 10);
            $table->string('phone', 20);
            $table->string('mobile', 11);
            $table->text('address');

            // Subject, term and discount.
            $table->string('subject', 255);
            $table->char('start_date', 10);
            $table->char('end_date', 10);
            $table->unsignedTinyInteger('duration_months');
            $table->date('starts_on');
            $table->date('ends_on');
            $table->unsignedTinyInteger('discount_percent');

            // The date printed in the contract's header: the Tehran day it was submitted.
            $table->char('contract_date', 10);
            $table->string('template_version', 20);
            $table->text('contract_text');

            // Record of the user accepting the terms.
            $table->timestamp('terms_accepted_at');
            $table->string('ip_address', 45)->nullable();
            $table->string('app_version', 40)->nullable();
            $table->string('user_agent', 255)->nullable();

            // Admin review.
            $table->text('rejection_reason')->nullable();
            $table->integer('reviewed_by')->unsigned()->nullable();
            $table->timestamp('reviewed_at')->nullable();

            $table->timestamps();

            $table->index(['user_id', 'id']);
            $table->index('status');
            $table->foreign('user_id')->references('id')->on('users');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('e_contracts');
    }
};
