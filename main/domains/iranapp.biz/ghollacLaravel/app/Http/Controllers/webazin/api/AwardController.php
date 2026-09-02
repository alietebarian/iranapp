<?php


namespace App\Http\Controllers\webazin\api;


use App\Http\Controllers\Controller;
use App\Webazin\Award\Award;
use App\Webazin\Wallet\Wallet;
use Tymon\JWTAuth\Exceptions\JWTException;
use Tymon\JWTAuth\Exceptions\TokenExpiredException;
use Tymon\JWTAuth\Exceptions\TokenInvalidException;
use Tymon\JWTAuth\Facades\JWTAuth;

class AwardController extends Controller {

	public function index() {
		$awards = Award::all();

		return response()->json( [ 'status' => 200 , 'list' => $awards ] );
	}

	public function get( $id ) {
		try {
			$user = JWTAuth::parseToken()->authenticate();
		} catch ( TokenExpiredException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_expired' ] );

		} catch ( TokenInvalidException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_invalid' ] );

		} catch ( JWTException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_absent' ] );

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
			$user = JWTAuth::parseToken()->authenticate();
		} catch ( TokenExpiredException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_expired' ] );

		} catch ( TokenInvalidException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_invalid' ] );

		} catch ( JWTException $e ) {

			return response()->json( [ 'status' => $e->getStatusCode() , 'error' => 'token_absent' ] );

		}

		return response()->json($user->walletSum);
	}

}
