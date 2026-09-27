<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Membership cards ("کارت هدیه معرفی به مراکز طرف قرارداد"), requested from the app and
     * approved or rejected in the admin panel.
     *
     * One row per user: the serial number belongs to the user, so a rejected request is corrected
     * in place and keeps its serial rather than taking a new one.
     */
    public function up(): void
    {
        Schema::create('membership_cards', function (Blueprint $table) {
            $table->increments('id');
            $table->integer('user_id')->unsigned()->unique();
            // 1394001003631900, 1394001003631901, ... (see MembershipCard::FIRST_SERIAL).
            $table->unsignedBigInteger('serial_number')->unique();
            $table->enum('status', ['pending', 'approved', 'rejected'])->default('pending');

            // JSON list of 1 to 6 member names.
            $table->text('members');

            // The Tehran day of the (latest) request, and one year after it.
            $table->char('membership_date', 10);
            $table->char('expiry_date', 10);
            $table->date('starts_on');
            $table->date('expires_on');

            $table->string('app_version', 40)->nullable();
            $table->timestamp('submitted_at');

            // Admin review. rejection_reason is what the user must fix; it is kept when the user
            // resubmits, so the admin can see what was asked for, and cleared on approval.
            $table->text('rejection_reason')->nullable();
            $table->integer('reviewed_by')->unsigned()->nullable();
            $table->timestamp('reviewed_at')->nullable();

            $table->timestamps();

            $table->index('status');
            $table->foreign('user_id')->references('id')->on('users');
        });
    }

    public function down(): void
    {
        Schema::dropIfExists('membership_cards');
    }
};
