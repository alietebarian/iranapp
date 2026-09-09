<?php

use Carbon\Carbon;
use GuzzleHttp\Client;

if ( ! function_exists( 'make_url_validate' ) ) {
	function make_url_validate( $url ) {
		if ( stripos( $url , "http://" ) === false && stripos( $url , "https://" ) === false ) {
			$url = "http://" . $url;
		}

		return $url;
	}
}

if ( ! function_exists( 'getElapsedTime' ) ) {
	function getElapsedTime( $dateTime ) {
		$timestamp        = Carbon::now()->getTimestamp() - Carbon::createFromFormat( 'Y-m-d H:i:s' , $dateTime )->getTimestamp();
		$arr              = [
			12 * 30 * 24 * 60 * 60 => 'سال' ,
			30 * 24 * 60 * 60      => 'ماه' ,
			24 * 60 * 60           => 'روز' ,
			60 * 60                => 'ساعت' ,
			60                     => 'دقیقه' ,
			1                      => 'ثانیه'
		];
		$PassedSecondText = '';
		$breakLoop        = false;
		foreach ( $arr as $sec => $unit ) {
			if ( ( (float) $timestamp / $sec ) > 1 ) {
				$devision         = ceil( $timestamp / $sec );
				$PassedSecondText = $devision . ' ' . $unit . ' قبل';
				$breakLoop        = true;
				break;
			}
		}
		if ( ! $breakLoop ) {
			return 'لحظاتی قبل';
		}

		return $PassedSecondText;
	}
}

if ( ! function_exists( 'convertDateTimeToJalaliDate' ) ) {
	function convertDateTimeToJalali( $dateTime ) {
		$jDate = \App\Libraries\jdf::jdate( 'j F Y' , Carbon::createFromFormat( 'Y-m-d H:i:s' , $dateTime )->getTimestamp() );

		return $jDate;
	}
}

if ( ! function_exists( 'send_sms' ) ) {
	function send_sms( $templateName , $number , $token , $token2 = null , $token10 = null , $token20 = null ) {
//        ini_set("soap.wsdl_cache_enabled", "0");
//        $sms_client = new SoapClient('http://payamak-service.ir/SendService.svc?wsdl', array('encoding' => 'UTF-8'));
//
//        $userName = \App\Models\Setting::where('setting_key' , 'payamak_service_user_name')->first();
//        if($userName){
//            $userName = $userName->setting_value;
//        }else{
//            $userName = null;
//        }
//        $password = \App\Models\Setting::where('setting_key' , 'payamak_service_password')->first();
//        if($password){
//            $password = $password->setting_value;
//        }else{
//            $password = null;
//        }
//
//        $parameters['userName'] = $userName;
//        $parameters['password'] = $password;
////        $parameters['fromNumber'] = "10009611";
//        $parameters['fromNumber'] = "SimCard";
//        $parameters['toNumbers'] = array($number);
//        $parameters['messageContent'] = $message;
//        $parameters['isFlash'] = false;
//        $recId = array();
//        $status = array();
//        $parameters['recId'] = &$recId;
//        $parameters['status'] = &$status;
//
//        $sms_client->SendBatchSms($parameters)->SendBatchSmsResult;

		$token2 ? $url = "https://api.kavenegar.com/v1/" . config( 'app.kaveh_negar.api_key' ) . "/verify/lookup.json?receptor=" . $number . "&token=" . $token . "&token2=" . $token2 . "&token10=" . $token10 . "&token20=" . $token20 . "&template=" . $templateName
			: $url = "https://api.kavenegar.com/v1/" . config( 'app.kaveh_negar.api_key' ) . "/verify/lookup.json?receptor=" . $number . "&token=" . $token . "&template=" . $templateName;

		$client = new Client();
		$res    = $client->get( $url );
	}
}
