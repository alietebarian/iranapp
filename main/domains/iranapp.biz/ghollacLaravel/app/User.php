<?php

namespace App;

use App\Webazin\Wallet\Wallet;
use Illuminate\Notifications\Notifiable;
use Illuminate\Foundation\Auth\User as Authenticatable;
use Laravel\Passport\HasApiTokens;

class User extends Authenticatable {
	use Notifiable , HasApiTokens;

	/**
	 * The attributes that are mass assignable.
	 *
	 * @var array
	 */
	protected $fillable = [
		'first_name' ,
		'last_name' ,
		'mobile' ,
		'email' ,
		'verify_token' ,
		'password' ,
		'remember_token' ,
		'is_mobile_verified' ,
		'type' ,
		'fcm_token' ,
		'forget_password_token' ,
		'send_news_notifications' ,
		'send_ads_notifications' ,
		'reset_password_token' ,
	];

	/**
	 * The attributes that should be hidden for arrays.
	 *
	 * @var array
	 */
	protected $hidden = [
		'password' ,
		'remember_token' ,
	];

	protected $appends = [ 'walletSum' ];

	public function wallet() {
		return $this->hasMany( Wallet::class );
	}

	public function getWalletSumAttribute() {
		return $this->walletSum();
	}

	public function walletSum() {
		return $this->hasMany( Wallet::class )->sum( 'price' );
	}


}
