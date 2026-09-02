<?php

namespace App\Http\Controllers\Webazin\admin\Awards;

use App\Webazin\Award\Award;
use Illuminate\Http\Request;
use App\Http\Controllers\Controller;

class AwardsController extends Controller {
	public function index() {
		$awards = Award::all();

		return view( 'admin.awards' , compact( 'awards' ) );
	}

	public function create() {
		$awards = Award::all();

		return view( 'admin.awards' , compact( 'awards' ) );
	}

	public function store( Request $request ) {
		$this->validate( $request , [
			'name'  => 'required' ,
			'price' => 'required' ,
		] );

		$award = Award::create( $request->only( 'name' , 'price' , 'description' ) );

		return back();
	}

	public function destroy( $id ) {
		$award = Award::findOrFail( $id );
		$award->delete();

		return back();
	}

	public function update( Request $request , $id ) {
		$this->validate( $request , [
			'name'  => 'required' ,
			'price' => 'required' ,
		] );
		$award = Award::findOrFail( $id );
		$award->update( $request->only( 'name' , 'price' , 'description' ) );

		return back();
	}
}
