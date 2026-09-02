<?php

namespace App\Webazin\Pay;

use App\Ads;
use App\User;
use Illuminate\Database\Eloquent\Model;

class Pay extends Model {
	protected $guarded = [ 'id' ];

	public function ad() {
		return $this->belongsTo( Ads::class , 'ads_id' , 'id' );
	}

	public function user() {
		return $this->hasOne( User::class );
	}
}
