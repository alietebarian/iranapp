<?php

namespace App\Support;

use App\Models\AdTermsAcceptance;
use Carbon\Carbon;
use Illuminate\Http\Request;

/**
 * Users must agree to the terms before an ad of theirs is accepted, and that agreement is kept
 * as a legal record: who agreed, when, from where, and to which wording.
 *
 * The app sends `terms_accepted` plus the `terms_version` it displayed. The version the app
 * reports is what gets stored, because the record has to reflect the text the user actually
 * read rather than whatever the server considers current.
 */
class TermsConsent
{
	/** Bump this whenever the wording in the app's `rouls` string changes. */
	const CURRENT_VERSION = '1.0';

	/** Values `ad_type` can hold, one per ad table. */
	const TYPE_BUSINESS = 'ads';
	const TYPE_ESTATE = 'estate';
	const TYPE_VEHICLE = 'vehicle';
	const TYPE_EMPLOY = 'employ';

	public static function rules(): array {
		return [
			'terms_accepted' => [ 'required' , 'accepted' ] ,
			'terms_version'  => [ 'nullable' , 'string' , 'max:40' ] ,
		];
	}

	public static function messages(): array {
		return [
			'terms_accepted.required' => 'برای ثبت آگهی باید با قوانین و مقررات موافقت کنید.',
			'terms_accepted.accepted' => 'برای ثبت آگهی باید با قوانین و مقررات موافقت کنید.',
			'terms_version.max'       => 'نسخه قوانین نامعتبر است.',
		];
	}

	/**
	 * Stores the acceptance for an ad that was just saved. `accepted_at` is the server's own
	 * clock rather than anything the client sent, so the timestamp cannot be back-dated.
	 */
	public static function record( string $adType , int $adId , int $userId , Request $request ): AdTermsAcceptance {
		return AdTermsAcceptance::create( [
			'user_id'       => $userId ,
			'ad_type'       => $adType ,
			'ad_id'         => $adId ,
			'terms_version' => $request->filled( 'terms_version' )
				? (string) $request->input( 'terms_version' )
				: self::CURRENT_VERSION ,
			'accepted_at'   => Carbon::now()->toDateTimeString() ,
			'ip_address'    => $request->ip() ,
			'app_version'   => $request->input( 'app_version' ) ,
			'user_agent'    => $request->userAgent() ? mb_substr( $request->userAgent() , 0 , 255 ) : null ,
		] );
	}

	/** The acceptance shown to admins on an ad's approval page, or null for older ads. */
	public static function forAd( string $adType , int $adId ): ?AdTermsAcceptance {
		return AdTermsAcceptance::where( 'ad_type' , $adType )
		                        ->where( 'ad_id' , $adId )
		                        ->orderBy( 'id' , 'desc' )
		                        ->first();
	}
}
