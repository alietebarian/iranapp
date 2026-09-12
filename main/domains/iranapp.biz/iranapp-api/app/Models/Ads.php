<?php

namespace App\Models;

use Carbon\Carbon;
use Illuminate\Database\Eloquent\Model;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;

class Ads extends Model {
	const FIELDS = 'ads.* , city.name as city_name , province.id as province_id ,  province.name as province_name , sub_category.name as sub_category_name,
    category.id as category_id , category.name as category_name , ads_plan.num_of_stars , ads_plan.num_of_updates as max_number_of_update , ads_plan.ordering_factor, ads_plan.max_number_of_photos,
    ads_plan.price as plan_price , ads_plan.plan_title , ads_plan.interval_days as plan_interval_days ,
     ( select count(*) from ads_like where ads_like.ads_id = ads.id and like_type = "like" ) as likes,
     ( select count(*) from ads_like where ads_like.ads_id = ads.id and like_type = "dislike"  ) as dislikes';
	/** Containers the app's player (Media3) can play; validated against the file's content, not its name. */
	const VIDEO_FORMATS = [ 'mp4' , 'm4v' , 'mov' , 'webm' , '3gp' , 'mkv' ];
	/** Kilobytes. PHP's upload_max_filesize / post_max_size can cap this lower — see maxVideoUploadBytes(). */
	const VIDEO_MAX_KB = 102400;
	const VIDEO_DIR = 'ads_video';
	protected $table = 'ads';
	private $query;

	public function __construct( array $attributes = [] ) {
		parent::__construct( $attributes );
		$this->query = DB::table( $this->table )
		                 ->join( 'city' , 'city.id' , '=' , 'ads.city_id' )
		                 ->join( 'province' , 'province.id' , '=' , 'city.province_id' )
		                 ->join( 'sub_category' , 'sub_category.id' , '=' , 'ads.sub_category_id' )
		                 ->join( 'category' , 'category.id' , '=' , 'sub_category.category_id' )
		                 ->join( 'ads_plan' , 'ads_plan.id' , '=' , 'ads.ads_plan_id' );

	}

	protected static function booted() {
		// Videos are large; don't leave the file behind when the ad itself is deleted.
		static::deleted( function ( Ads $ads ) {
			self::deleteVideoFile( $ads->video );
		} );
	}

	public function getValidAds() {
		$this->query = $this->query->where( 'ads.valid_since' , '<=' , Carbon::now()->toDateTimeString() )
		                           ->where( 'ads.valid_until' , '>=' , Carbon::now()->toDateTimeString() );

		return $this;
	}

	public function getApproved() {
		$this->query = $this->query->where( 'ads.status' , '=' , 'approved' );

		return $this;
	}

	public function getQuery() {
		return $this->query;
	}

	public function selectFields( $fields ) {
		$this->query = $this->query->selectRaw( $fields );

		return $this;
	}

	public function photo() {
		return $this->hasMany( AdsPhoto::class );
	}

	public function plan() {
		return $this->belongsTo( AdsPlan::class , 'ads_plan_id' );
	}

	public function vip() {
		return $this->hasOne( VipAds::class );
	}

	public function userAds() {
		return $this->belongsToMany( User::class , 'user_ads' , 'ads_id' , 'user_id' );
	}

	public function visitorAds() {
		return $this->belongsToMany( Visitor::class , 'user_ads' , 'ads_id' , 'user_id' );
	}

	public static function videoUrl( $fileName ) {
		return $fileName ? URL::to( '/' . self::VIDEO_DIR ) . '/' . $fileName : null;
	}

	/** The largest video the server will actually accept, in bytes. */
	public static function maxVideoUploadBytes() {
		return min( self::VIDEO_MAX_KB * 1024 , UploadedFile::getMaxFilesize() );
	}

	/** Stores the upload as this ad's video; the file it replaces is deleted only once the new one is in place. */
	public function replaceVideo( UploadedFile $file ) {
		$oldVideo = $this->video;
		$fileName = uniqid() . '.' . ( $file->guessExtension() ?: strtolower( $file->getClientOriginalExtension() ) );
		$file->move( public_path( self::VIDEO_DIR ) , $fileName );
		$this->video = $fileName;
		$this->save();
		self::deleteVideoFile( $oldVideo );
	}

	public function removeVideo() {
		$oldVideo    = $this->video;
		$this->video = null;
		$this->save();
		self::deleteVideoFile( $oldVideo );
	}

	private static function deleteVideoFile( $fileName ) {
		$path = public_path( self::VIDEO_DIR ) . '/' . $fileName;
		if ( $fileName && is_file( $path ) ) {
			unlink( $path );
		}
	}
}
