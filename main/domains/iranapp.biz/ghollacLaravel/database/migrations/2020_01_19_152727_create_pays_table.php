<?php

use Illuminate\Support\Facades\Schema;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Database\Migrations\Migration;

class CreatePaysTable extends Migration {
	/**
	 * Run the migrations.
	 *
	 * @return void
	 */
	public function up() {
		Schema::create( 'pays' , function ( Blueprint $table ) {
			$table->increments( 'id' );
			$table->unsignedInteger( 'user_id' );
			$table->foreign( 'user_id' )->references( 'id' )->on( 'users' )->onDelete( 'cascade' );
			$table->unsignedInteger( 'ads_id' );
			$table->foreign( 'ads_id' )->references( 'id' )->on( 'ads' )->onDelete( 'cascade' );
			$table->decimal( 'price' , 10 , 0 );
			$table->string('hash')->nullable();
			$table->string('ref_id')->nullable();
			$table->enum( 'status' , [ 'initial' , 'pay' , 'cancel' ] )->default('initial');
			$table->timestamps();
		} );
	}

	/**
	 * Reverse the migrations.
	 *
	 * @return void
	 */
	public function down() {
		Schema::dropIfExists( 'pays' );
	}
}
