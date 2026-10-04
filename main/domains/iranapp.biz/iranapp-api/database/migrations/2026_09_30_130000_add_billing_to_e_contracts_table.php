<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Free or paid contracts.
     *
     * When approving, the admin either makes the contract free, or asks for a payment: an amount
     * and a card number the user pays to. The user pays outside the app, confirms it in the app
     * (awaiting_payment → payment_submitted) and the admin gives the final approval.
     *
     * expiry_notified_at records the one reminder sent to the user and the admin when fewer than
     * 15 days of the contract are left (App\Support\EContractExpiry).
     */
    public function up(): void
    {
        Schema::table('e_contracts', function (Blueprint $table) {
            $table->enum('status', ['pending', 'awaiting_payment', 'payment_submitted', 'approved', 'rejected'])
                ->default('pending')->change();

            // free | paid; set when the admin decides.
            $table->string('billing', 10)->nullable()->after('status');
            // In toman.
            $table->unsignedBigInteger('amount')->nullable()->after('billing');
            $table->string('card_number', 16)->nullable()->after('amount');
            $table->string('card_holder', 100)->nullable()->after('card_number');
            $table->text('payment_message')->nullable()->after('card_holder');
            $table->timestamp('payment_requested_at')->nullable()->after('payment_message');
            // What the user entered when confirming the payment (tracking number etc.), optional.
            $table->string('payment_reference', 100)->nullable()->after('payment_requested_at');
            $table->timestamp('payment_submitted_at')->nullable()->after('payment_reference');
            // Why the admin did not accept a confirmed payment; shown to the user.
            $table->text('payment_rejection_reason')->nullable()->after('payment_submitted_at');
            $table->timestamp('expiry_notified_at')->nullable()->after('reviewed_at');

            $table->index(['status', 'ends_on']);
        });

        // Contracts approved before payments existed were free.
        DB::table('e_contracts')->where('status', 'approved')->update(['billing' => 'free']);
    }

    public function down(): void
    {
        DB::table('e_contracts')->whereIn('status', ['awaiting_payment', 'payment_submitted'])->update(['status' => 'pending']);

        Schema::table('e_contracts', function (Blueprint $table) {
            $table->dropIndex(['status', 'ends_on']);
            $table->dropColumn([
                'billing', 'amount', 'card_number', 'card_holder', 'payment_message', 'payment_requested_at',
                'payment_reference', 'payment_submitted_at', 'payment_rejection_reason', 'expiry_notified_at',
            ]);
            $table->enum('status', ['pending', 'approved', 'rejected'])->default('pending')->change();
        });
    }
};
