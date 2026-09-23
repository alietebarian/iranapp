<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * Run the migrations.
     */
    public function up(): void
    {
        Schema::table('news', function (Blueprint $table) {
            // When the news appears in the app; stored in the app timezone like created_at.
            $table->dateTime('publish_at')->nullable()->index();
            // Set while a push notification still has to go out once publish_at arrives.
            $table->boolean('notify_on_publish')->default(false);
        });

        // Everything already in the table was published the moment it was created.
        DB::table('news')->update(['publish_at' => DB::raw('created_at')]);
    }

    /**
     * Reverse the migrations.
     */
    public function down(): void
    {
        Schema::table('news', function (Blueprint $table) {
            $table->dropIndex(['publish_at']);
            $table->dropColumn(['publish_at', 'notify_on_publish']);
        });
    }
};
