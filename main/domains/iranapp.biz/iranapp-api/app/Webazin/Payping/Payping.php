<?php

namespace App\Webazin\Payping;

use App\Webazin\Payping\PapingExeption;
use App\Webazin\Payping\Traits\PaypingTrait;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\URL;
use League\Flysystem\Exception;

class Payping {
	use PaypingTrait;

	public function createPay() {
		try {
			$curl   = curl_init();
			$header = [
				"Authorization: Bearer $this->token" ,
				"Content-Type: application/json" ,
			];
			$params = [
				'amount'    => $this->amount ,
				'returnUrl' => $this->retutnUrl
			];
			curl_setopt_array( $curl , [
				CURLOPT_URL            => $this->paymentUrl ,
				CURLOPT_RETURNTRANSFER => true ,
				CURLOPT_CUSTOMREQUEST  => "POST" ,
				CURLOPT_POSTFIELDS     => json_encode( $params ) ,
				CURLOPT_HTTPHEADER     => $header ,
			] );

			$response = curl_exec( $curl );
			$err      = curl_error( $curl );

			curl_close( $curl );

			if ( $err ) {
				$err = json_decode( $err );

				return $err;
			} else {
				$response = json_decode( $response );
				if ( isset( $response->code ) ) {
					$this->code = $response->code;
				}

				return $response;
			}
		} catch ( Exception $exception ) {
			return $exception->getMessage();
		}
	}

	public function redirect() {
		if ( $this->code ) {
			$url = $this->redirectUrl . $this->code;
			header( 'Location: ' . $url );
		} else {
			return PapingExeption::send( 26 );
		}
	}

	public function verify( Request $request , $amount ) {
		try {
			if ( $request->has( 'refid' ) ) {
				$this->setRefId( $request->refid );
				$this->setPaypingRefId( $request->code );
				$this->setAmount( $amount );
			} else {
				return PapingExeption::send( 50 );
			}
			$curl   = curl_init();
			$header = [
				"Authorization: Bearer $this->token" ,
				"Content-Type: application/json" ,
			];
			$params = [
				'refId'  => $this->getRefID() ,
				'amount' => $this->getAmount()
			];
			curl_setopt_array( $curl , array (
				CURLOPT_URL            => $this->verifyUrl ,
				CURLOPT_RETURNTRANSFER => true ,
				CURLOPT_CUSTOMREQUEST  => "POST" ,
				CURLOPT_POSTFIELDS     => json_encode( $params ) ,
				CURLOPT_HTTPHEADER     => $header ,
			) );

			$response = curl_exec( $curl );
			$err      = curl_error( $curl );

			curl_close( $curl );

			if ( $err ) {
				return "cURL Error #:" . $err;
			} else {
				return $response;
			}
		} catch ( Exception $exception ) {
			return $exception->getMessage();
		}
	}

	public function shaba() {
		try {
			$curl   = curl_init();
			$header = [
				"Authorization: Bearer $this->token" ,
				"Content-Type: application/json" ,
			];
			$params = [
				'shaba'  => $this->getShaba() ,
				'amount' => $this->getShabaAmount()
			];
			curl_setopt_array( $curl , array (
				CURLOPT_URL            => $this->shabaUrl ,
				CURLOPT_RETURNTRANSFER => true ,
				CURLOPT_CUSTOMREQUEST  => "POST" ,
				CURLOPT_POSTFIELDS     => json_encode( $params ) ,
				CURLOPT_HTTPHEADER     => $header ,
			) );

			$response = curl_exec( $curl );
			$err      = curl_error( $curl );

			curl_close( $curl );

			if ( $err ) {
				echo "cURL Error #:" . $err;
			} else {
				return $response;
			}
		} catch ( Exception $exception ) {
			return $exception->getMessage();
		}
	}

}
