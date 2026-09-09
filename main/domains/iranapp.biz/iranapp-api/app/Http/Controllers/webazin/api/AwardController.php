<?php


namespace App\Http\Controllers\webazin\api;


use App\Http\Controllers\Controller;
use App\Webazin\Award\Award;
use App\Webazin\Wallet\Wallet;

class AwardController extends Controller {

	public function index() {
		$awards = Award::all();

		return response()->json( [ 'status' => 200 , 'list' => $awards ] );
	}

	public function get( $id ) {
		try {
			$user = auth('sanctum')->user();
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
		$award = Award::findOrFail( $id );
		if ( $award->price > $user->walletSum ) {
			return view( 'webazin.api.award.error' );
		} else {
			Wallet::create( [
				'user_id'     => $user->id ,
				'price'       => - $award->price ,
				'description' => 'کسر موجودی بابت دریافت جایزه ' . $award->name
			] );

			return view( 'webazin.api.award.accept' );
		}
	}

	public function userCount() {
		try {
			$user = auth('sanctum')->user();
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}

		return response()->json($user->walletSum);
	}

}
