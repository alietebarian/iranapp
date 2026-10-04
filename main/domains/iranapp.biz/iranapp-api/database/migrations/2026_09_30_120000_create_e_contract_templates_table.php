<?php

use App\Support\EContractTemplate;
use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

return new class extends Migration
{
    /**
     * The wording of the electronic contract, editable in the admin panel. Every save is a new
     * row (a new version), never an update: each e_contracts row records the version it was
     * signed under, and that version's text must stay as it was.
     */
    public function up(): void
    {
        Schema::create('e_contract_templates', function (Blueprint $table) {
            $table->increments('id');
            $table->string('version', 20)->unique();
            $table->string('title', 200);
            // JSON list of {heading: ?string, text: string}; the text holds {placeholders}.
            $table->text('sections');
            $table->integer('created_by')->unsigned()->nullable();
            $table->timestamps();
        });

        // Version 1.0 is the wording contracts have been signed under so far.
        EContractTemplate::ensureDefaultStored();
    }

    public function down(): void
    {
        Schema::dropIfExists('e_contract_templates');
    }
};
