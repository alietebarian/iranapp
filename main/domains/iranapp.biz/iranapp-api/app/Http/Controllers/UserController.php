<?php

namespace App\Http\Controllers;

use App\Http\Requests\Admin\SaveNewUser;
use App\Models\User;
use Illuminate\Database\QueryException;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Mail;
use Illuminate\Support\Facades\Validator;
use Maatwebsite\Excel\Facades\Excel;
use SoapClient;


class UserController extends Controller {
	public function register( Request $request ) {
		$validator = Validator::make( $request->all() , [
			'first_name' => 'required|string|max:200' ,
			'last_name'  => 'required|string|max:200' ,
			'mobile'     => 'required|string|max:200|unique:users,mobile' ,
			'password'   => 'required|string'
		] , [
			'first_name.required' => 'وارد کردن نام الزامی است.' ,
			'first_name.string'   => 'نام نامعتبر است.' ,
			'first_name.max'      => 'نام طولانی تر از حد مجاز است.' ,
			'last_name.required'  => 'وارد کردن نام خانوادگی الزامی است.' ,
			'last_name.string'    => 'نام خانوادگی نامعتبر است.' ,
			'last_name.max'       => 'نام خانوادگی طولانی تر از حد مجاز است.' ,
			'mobile.required'     => 'وارد کردن تلفن همراه الزامی است.' ,
			'mobile.string'       => 'تلفن همراه نامعتبر است.' ,
			'mobile.max'          => 'تلفن همراه طولانی تر از حد مجاز است.' ,
			'mobile.unique'       => 'تلفن همراه وارد شده از قبل ثبت شده است.' ,
			'password.required'   => 'رمز عبور الزامی است.' ,
			'password.string'     => 'رمز عبور نامعتبر است.'
		] );
		if ( $validator->fails() ) {
			return response()->json( [ 'status' => 400 , 'errors' => $validator->errors( $request->all() ) ] );
		}
		$user               = new User();
		$user->first_name   = $request->first_name;
		$user->last_name    = $request->last_name;
		$user->mobile       = $request->mobile;
		$user->password     = Hash::make( $request->password );
		$user->verify_token = rand( 1111 , 9999 );
		$user->fcm_token    = $request->fcm_token;
		$user->save();

//        $api = new SoapClient('http://www.tsms.ir/soapWSDL/?wsdl');
//        $username = 'mehr_cart';
//        $password = '123456789';
//        $mobile_array = array($user->mobile);
//        $msg_array = array('به ایران اپ خوش آمدید! کد فعال سازی شما' . $user->verify_token);
//        $sms_number_array = array('50001717003378');
//        $messagid = rand();
//        $mclass = array('');
//        $rezult = $api->sendSms($username, $password, $sms_number_array, $mobile_array, $msg_array, $mclass, $messagid);
		send_sms( 'verify' , $user->mobile , $user->verify_token );

		return response()->json( [ 'status' => 204 , 'user_id' => $user->id ] );
	}

	public function doLogin( Request $request ) {
		$login    = $request->mobile;
		$password = $request->password;
		$fcmToken = $request->fcm_token;
		$user     = User::where( 'mobile' , $login )->first();
		if ( ! $user ) {
			return response( [ 'status' => 401 , 'error' => 'invalid_user' ] );
		}
		if ( Hash::check( $password , $user->password ) ) {
			try {
				$token = $user->createToken('mobile')->plainTextToken;
			} catch ( JWTException $exception ) {
				return response()->json( [ 'status' => 500 , 'error' => 'could not create token' ] );
			}
			$user->fcm_token = $fcmToken;
			$user->save();
			$user->jwt_token = $token;

			return response()->json( [ 'status' => 200 , 'user' => $user ] );
		} else {
			return response()->json( [ 'status' => 401 , 'error' => 'invalid credentials' ] );
		}
	}

	public function verifyAndLogin( Request $request ) {
		$mobile      = $request->mobile;
		$verifyToken = $request->token;
		$user        = User::where( 'mobile' , $mobile )->first();
		if ( ! $user ) {
			return response()->json( [ 'status' => 401 , 'error' => 'user_not_found' ] );
		}
		if ( $user->verify_token == $verifyToken ) {
			try {
				$token = $user->createToken('mobile')->plainTextToken;
			} catch ( JWTException $exception ) {
				return response()->json( [ 'status' => 500 , 'error' => 'could_not_create_token' ] );
			}
			$user->verify_token       = null;
			$user->is_mobile_verified = 1;
			$user->save();
			$user->jwt_token = $token;

			return response()->json( [ 'status' => 200 , 'user' => $user ] );
		} else {
			return response()->json( [ 'status' => 401 , 'error' => 'invalid_verify_token' ] );
		}
	}

	public function changeAccountInfo( Request $request ) {
		try {
			if ( ! $user = auth('sanctum')->user() ) {
				return response()->json( [ 'status' => 404 , 'error' => 'user_not_found' ] );
			}
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
		$user->first_name = $request->first_name;
		$user->last_name  = $request->last_name;
		$user->save();

		return response()->json( [ 'status' => 204 ] );
	}

	public function changePassword( Request $request ) {
		try {
			if ( ! $user = auth('sanctum')->user() ) {
				return response()->json( [ 'status' => 404 , 'error' => 'user_not_found' ] );
			}
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
		if ( Hash::check( $request->old_password , $user->password ) ) {
			$user->password = Hash::make( $request->new_password );
			$user->save();

			return response()->json( [ 'status' => 204 ] );
		} else {
			return response()->json( [ 'status' => 401 , 'error' => 'old_password_does_not_match' ] );
		}

	}

	public function reGenerateVerifyToken( Request $request ) {
		$user               = User::where( 'mobile' , $request->mobile )->firstOrFail();
		$user->verify_token = rand( 1111 , 9999 );
		$user->save();
		send_sms( 'verify' , $request->mobile , $user->verify_token );

		return response()->json( [ 'status' => 204 ] );
	}

	public function updateNotificationSetting( Request $request ) {
		try {
			$user = auth('sanctum')->user();
		} catch ( \Throwable $exception ) {
			return response()->json( [ 'status' => 401 , 'error' => 'token_invalid' ] );
		}
		$user->send_news_notifications = $request->send_news_notifications;
		$user->send_ads_notifications  = $request->send_ads_notifications;

		$user->save();

		return response()->json( [ 'status' => 204 ] );

	}

	public function updateMobileNumber( Request $request ) {
		$mobile = $request->old_mobile;
		$user   = User::where( 'mobile' , $mobile )->first();
		if ( ! $user ) {
			return response()->json( [ 'status' => 401 , 'error' => 'user_not_found' ] );
		}
		$userExistedWithNewMobile = User::where( 'mobile' , $request->new_mobile )->count();
		if ( $userExistedWithNewMobile > 0 ) {
			return response()->json( [ 'status' => 401 , 'error' => 'mobile_existed' ] );
		}
		$user->mobile = $request->new_mobile;
		$user->save();

		return response()->json( [ 'status' => 204 ] );
	}

	public function showListInAdminPanel( Request $request ) {
		$list = DB::table( 'users' )->orderBy( 'created_at' , 'desc' );
		if ( $request->has( 'mobile' ) ) {
			$list = $list->where( 'users.mobile' , '=' , $request->mobile );
		}
		if ( $request->has( 'full_name' ) ) {
			$list = $list->whereRaw( ' MATCH(first_name , last_name) AGAINST("' . $request->full_name . '" IN NATURAL LANGUAGE MODE)' );
		}
		$list           = $list->paginate( 15 )
		                       ->appends( [
			                       'mobile'    => $request->mobile ,
			                       'full_name' => $request->full_name
		                       ] );
		$data[ 'list' ] = $list;

		return view( 'admin.users_list' )->with( $data );
	}

	public function showUpdatePageInAdminPanel( Request $request , User $user ) {
		$data[ 'user' ] = $user;

		return view( 'admin.user_edit' )->with( $data );
	}

	public function updateUserInAdminPanel( Request $request , User $user ) {
		$user->first_name = $request->first_name;
		$user->last_name  = $request->last_name;
		if ( $request->has( 'password' ) ) {
			$user->password = Hash::make( $request->password );
		}
		if ( $request->has( 'send_news_notifications' ) ) {
			$user->send_news_notifications = 1;
		} else {
			$user->send_news_notifications = 0;
		}
		if ( $request->has( 'send_ads_notifications' ) ) {
			$user->send_ads_notifications = 1;
		} else {
			$user->send_ads_notifications = 0;
		}

		$user->save();
		$msg        = new \stdClass();
		$msg->title = 'به روز رسانی موفقیت آمیز';
		$msg->msg   = 'کاربر مورد نظر با موفقیت به روز رسانی شد.';

		return redirect()->route( 'showUsersListInAdminPanel' )->with( 'success_msg' , $msg );
	}

	public function showUserCreatePage( Request $request ) {
		return view( 'admin.user_create' );
	}

	public function saveNewUserInAdminPanel( SaveNewUser $request ) {
		$user             = new User();
		$user->first_name = $request->first_name;
		$user->last_name  = $request->last_name;
		$user->mobile     = $request->mobile;
		$user->password   = Hash::make( $request->password );
		$user->save();

		$msg        = new \stdClass();
		$msg->title = 'ثبت موفقیت آمیز';
		$msg->msg   = 'کاربر مورد نظر با موفقیت ثبت شد.';

		return redirect()->route( 'showUsersListInAdminPanel' )->with( 'success_msg' , $msg );
	}

	public function delete( Request $request , User $user ) {
		try {
			$user->delete();
			$msg        = new \stdClass();
			$msg->title = 'حذف موفقیت آمیز';
			$msg->msg   = 'کاربر مورد نظر با موفقیت حذف شد.';

			return redirect()->back()->with( 'success_msg' , $msg );
		} catch ( QueryException $exception ) {
			if ( $exception->errorInfo[ 1 ] == 1451 ) {
				$msg        = new \stdClass();
				$msg->title = 'خطا';
				$msg->msg   = 'امکان حذف کاربر به دلیل وجود رکوردهای مرتبط وجود ندارد.';

				return redirect()->back()->with( 'error_msg' , $msg );
			}
		}
	}

	public function showUserMobileBanks( Request $request ) {
		$list           = DB::table( 'users' )->select( 'first_name' , 'last_name' , 'mobile' )->paginate( 15 );
		$data[ 'list' ] = $list;

		return view( 'admin.user_mobile_banks' )->with( $data );
	}

	public function exportUserMobilesInExcelFormat( Request $request ) {
		Excel::create( 'user_mobiles' , function ( $excel ) {
			$users      = DB::table( 'users' )->select( 'first_name' , 'last_name' , 'mobile' )->get();
			$usersArr   = [];
			$headerArr  = [ 'نام' , 'نام خانوادگی' , 'تلفن همراه' ];
			$usersArr[] = $headerArr;
			foreach ( $users as $user ) {
				$userArr    = [];
				$userArr[]  = $user->first_name;
				$userArr[]  = $user->last_name;
				$userArr[]  = $user->mobile;
				$usersArr[] = $userArr;
			}
			$excel->sheet( 'users' , function ( $sheet ) use ( $usersArr ) {
				$sheet->fromArray( $usersArr );
			} );
		} )->download( 'xlsx' );
	}

	public function sendResetPasswordToken( Request $request ) {
		$mobile = $request->mobile;
		$user   = User::where( 'mobile' , $mobile )->first();
		if ( $user ) {
			$user->reset_password_token = rand( 10000 , 99999 );
			$user->save();

//            $api = new SoapClient('http://www.tsms.ir/soapWSDL/?wsdl');
//            $username = 'mehr_cart';
//            $password = '123456789';
//            $mobile_array = array($user->mobile);
//            $msg_array = array('کد بازنشانی رمز عبور شما در ایران اپ:' . $user->reset_password_token);
//            $sms_number_array = array('50001717003378');
//            $messagid = rand();
//            $mclass = array('');
//            $rezult = $api->sendSms($username, $password, $sms_number_array, $mobile_array, $msg_array, $mclass, $messagid);
			send_sms( 'forgetpassword' , $user->mobile , $user->reset_password_token );


			return response()->json( [ 'status' => 204 ] );
		} else {
			return response()->json( [ 'status' => 404 ] );
		}

	}

	public function verifyResetPasswordToken( Request $request ) {
		$mobile      = $request->mobile;
		$verifyToken = $request->verifyToken;

		$user = User::where( 'mobile' , $mobile )->where( 'reset_password_token' , $verifyToken )->first();

		if ( $user ) {
			return response()->json( [ 'status' => 204 ] );
		} else {
			return response()->json( [ 'status' => 401 ] );
		}
	}

	public function resetPassword( Request $request ) {
		$mobile      = $request->mobile;
		$verifyToken = $request->verifyToken;

		$user = User::where( 'mobile' , $mobile )->where( 'reset_password_token' , $verifyToken )->first();
		if ( $user ) {
			$user->password             = Hash::make( $request->new_password );
			$user->reset_password_token = null;
			$user->save();

			return response()->json( [ 'status' => 204 ] );
		} else {
			return response()->json( [ 'status' => 401 ] );
		}

	}

	public function searchNames( Request $request ) {
		$users = DB::table( 'users' )
		           ->select( 'id' , 'first_name' , 'last_name' )
		           ->where( function ( $nameQuery ) use ( $request ) {
			           $nameQuery->where( 'first_name' , 'like' , "%" . $request->term . "%" )
			                     ->orWhere( 'last_name' , 'like' , "%" . $request->term . "%" );
		           } )
		           ->where( 'users.is_mobile_verified' , '=' , 1 )
		           ->get();

		return response()->json( $users );
	}

	public function getUsersListViaAjax( Request $request ) {
		$this->validate( $request , [
			'q' => 'required|string|min:3'
		] );
		$list = User::whereRaw( '(
            MATCH(users.first_name , users.last_name) AGAINST("' . $request->q . '")
        )' )
		            ->get();

		return response()->json( $list );
	}
}
