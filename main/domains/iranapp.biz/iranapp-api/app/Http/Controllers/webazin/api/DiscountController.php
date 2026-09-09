<?php


namespace App\Http\Controllers\webazin\api;

use App\Models\Ads;
use App\Models\AdsPhoto;
use App\Http\Controllers\Controller;
use App\Models\User;
use App\Webazin\Pay\Pay;
use App\Webazin\Payping\Payping;
use App\Webazin\Wallet\Wallet;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Hash as Hashing;
use Illuminate\Support\Facades\URL;
use Illuminate\Support\Facades\Validator;
use Illuminate\Support\Str;
use phpseclib\Crypt\Hash;

class DiscountController extends Controller {
	public function show( $ads_id ) {
		try {
			$user = auth('sanctum')->user();
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
		$adsObj  = new Ads();
		$adsItem = $adsObj->selectFields( Ads::FIELDS )
		                  ->getQuery()
		                  ->where( 'ads.id' , '=' , $ads_id )
		                  ->first();
		if ( ! $adsItem ) {
			abort( 404 );
		}
		$photos = AdsPhoto::where( 'ads_id' , $ads_id )->get();
		$photos->each( function ( $item , $index ) use ( $photos ) {
			$photos[ $index ]->file_name = URL::to( '/ads_photo' ) . '/' . $item->file_name;
		} );
		$adsItem->photos = $photos;

		return view( 'webazin.api.discount.index' , compact( 'adsItem' , 'token' ) );

		return response()->json( [ 'status' => 200 , 'ad' => $adsItem ] );
	}

	public function pay( Request $request , $ads_id ) {
		$validator = Validator::make( $request->all()
			, [
				'price' => 'required|numeric'
			] ,
			[
				'price.required' => 'مبلغ را وارد کنید' ,
				'price.numeric'  => 'مبلغ را به درستی وارد کنید' ,
			] );
		if ( $validator->fails() ) {
			return response()->json( [ 'status' => 400 , 'error' => [ $validator->errors() ] ] );
		}

		try {
			$user = auth('sanctum')->user();
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
		$payPing = new Payping();
		$payPing->setAmount( $request->input( 'price' ) / 10 );
		$pay         = Pay::create( [
			'user_id' => $user->id ,
			'ads_id'  => $ads_id ,
			'price'   => $request->input( 'price' ) ,
		] );
		$hash        = Hashing::make( $pay->id . $pay->created_at );
		$pay->hash   = $hash;
		$pay->ref_id = Str::random( 16 );
		$pay->save();
		$payPing->setRetutnUrl( route( 'webazin.discount.callback' , [
			'ref_id' => $pay->ref_id
		] ) );
		$result = $payPing->createPay();

		if ( isset( $result->Error ) ) {
			$pay->delete();

			return back()->withErrors( [ $result->Error ] );
		}

		return $payPing->redirect();
	}

	public function callback( Request $request , $ref_id ) {
		$pay     = Pay::where( 'ref_id' , $ref_id )->firstOrFail();
		$user    = User::findOrFail( $pay->user_id );
		$payPing = new Payping();
		$verify  = $payPing->verify( $request , $pay->price / 10 );
		$verify  = json_decode( $verify );
		if ( ! isset( $verify->amount ) ) {
			$error = $verify;

			return view( 'webazin.api.discount.error' , compact( 'error' , 'user' ) );
		}
//		if ( isset( $verify->code ) && $verify->code == 1 ) {
		$pay->status = 'pay';
		$pay->code   = $payPing->getPaypingRefId();
		$pay->save();
		Wallet::create( [
			'user_id'     => $pay->user_id ,
			'description' => 'افزایش اعتبار جهت پرداخت شماره ' . $pay->id ,
			'price'       => $pay->price / 50000 // Todo check price for emteyaz example $pay->price/1000
		] );
		$toman                = $pay->price / 10; //60,000
		$percent              = 100 - $pay->ad->discount; //60%
		$priceWithOutDiscount = ( $toman * 100 ) / $percent; //get price discount //
		$priceDiscount        = $priceWithOutDiscount - $toman;
//		$price = ( 20 * $priceDiscount ) / 100; //get 90 percent off discount
		$price = ( 10 * $toman ) / 100; //get 10 percent off price
		send_sms( 'payed' , $pay->ad->mobile ,  str_replace(' ','_',$pay->ad->title) , $toman . 'تومان' , 'اکنون' , 'تخفیف' );
		// TODO calculate shaba amount and send sms
		$payPing->setShaba( $pay->ad->shaba );
		$payPing->setShabaAmount( (integer) ( $toman - $price ) );
		$shaba = $payPing->shaba();
		$shaba = json_decode( $shaba );
		if ( isset( $shaba->code ) ) {
			$pay->shaba_code = $shaba->code;
		} else {
			$error = 'تراکنش شبا برای پذیرنده انجام نشد '; //TODO set error
		}

		$pay->save();

		return view( 'webazin.api.discount.accept' , compact( 'user' ) );
//		}


	}
}
