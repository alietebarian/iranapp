<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Every user starts as a regular user. A user becomes pro when an admin approves their
     * electronic contract (see App\Http\Controllers\EContractController::approve).
     */
    public function up(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->enum('role', ['normal', 'pro'])->default('normal')->after('type');
            $table->timestamp('pro_since')->nullable()->after('role');
        });
    }

    public function down(): void
    {
        Schema::table('users', function (Blueprint $table) {
            $table->dropColumn(['role', 'pro_since']);
        });
    }
};
