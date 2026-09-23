<?php

namespace App\Support;

use App\Libraries\jdf;
use Carbon\Carbon;
use Illuminate\Http\Request;

/**
 * When a news item appears in the app.
 *
 * The admin either publishes right away or picks a Jalali (Shamsi) date and a time on the Tehran
 * clock. The moment is stored in the app timezone, the same as created_at, so it can be compared
 * with now() directly in queries.
 */
class NewsPublishTime
{
	const TIMEZONE = 'Asia/Tehran';

	const MONTHS = [
		1  => 'فروردین',
		2  => 'اردیبهشت',
		3  => 'خرداد',
		4  => 'تیر',
		5  => 'مرداد',
		6  => 'شهریور',
		7  => 'مهر',
		8  => 'آبان',
		9  => 'آذر',
		10 => 'دی',
		11 => 'بهمن',
		12 => 'اسفند',
	];

	/** How many Jalali years ahead the year dropdown reaches. */
	const YEARS_AHEAD = 2;

	public static function rules(): array {
		$scheduled = 'required_if:publish_mode,scheduled';

		return [
			'publish_mode'   => [ 'nullable' , 'in:now,scheduled' ] ,
			'publish_year'   => [ 'nullable' , $scheduled , 'integer' , 'min:1300' , 'max:1500' ] ,
			'publish_month'  => [ 'nullable' , $scheduled , 'integer' , 'min:1' , 'max:12' ] ,
			'publish_day'    => [ 'nullable' , $scheduled , 'integer' , 'min:1' , 'max:31' ] ,
			'publish_hour'   => [ 'nullable' , $scheduled , 'integer' , 'min:0' , 'max:23' ] ,
			'publish_minute' => [ 'nullable' , $scheduled , 'integer' , 'min:0' , 'max:59' ] ,
		];
	}

	public static function messages(): array {
		return [
			'publish_mode.in'             => 'نحوه انتشار خبر نامعتبر است.',
			'publish_year.required_if'    => 'سال انتشار خبر را انتخاب کنید.',
			'publish_month.required_if'   => 'ماه انتشار خبر را انتخاب کنید.',
			'publish_day.required_if'     => 'روز انتشار خبر را انتخاب کنید.',
			'publish_hour.required_if'    => 'ساعت انتشار خبر را انتخاب کنید.',
			'publish_minute.required_if'  => 'دقیقه انتشار خبر را انتخاب کنید.',
			'publish_year.*'              => 'سال انتشار خبر نامعتبر است.',
			'publish_month.*'             => 'ماه انتشار خبر نامعتبر است.',
			'publish_day.*'               => 'روز انتشار خبر نامعتبر است.',
			'publish_hour.*'              => 'ساعت انتشار خبر نامعتبر است.',
			'publish_minute.*'            => 'دقیقه انتشار خبر نامعتبر است.',
		];
	}

	/**
	 * Checks what the per-field rules cannot: that the day exists in that month (e.g. no 31 Mehr,
	 * no 30 Esfand outside leap years) and that the moment has not already passed.
	 */
	public static function validateDate( $validator , Request $request ): void {
		if ( ! self::isScheduled( $request ) || $validator->errors()->hasAny( array_keys( self::rules() ) ) ) {
			return;
		}

		if ( ! jdf::jcheckdate( (int) $request->publish_month , (int) $request->publish_day , (int) $request->publish_year ) ) {
			$validator->errors()->add( 'publish_day' , 'تاریخ انتخاب شده در تقویم وجود ندارد.' );

			return;
		}

		if ( self::fromRequest( $request )->lessThanOrEqualTo( Carbon::now() ) ) {
			$validator->errors()->add( 'publish_day' , 'زمان انتشار باید بعد از زمان فعلی باشد.' );
		}
	}

	public static function isScheduled( Request $request ): bool {
		return $request->input( 'publish_mode' ) === 'scheduled';
	}

	/** The chosen moment in the app timezone, or null when the news goes out immediately. */
	public static function fromRequest( Request $request ): ?Carbon {
		if ( ! self::isScheduled( $request ) ) {
			return null;
		}

		[ $gy , $gm , $gd ] = jdf::jalali_to_gregorian(
			(int) $request->publish_year , (int) $request->publish_month , (int) $request->publish_day
		);

		return Carbon::create(
			$gy , $gm , $gd , (int) $request->publish_hour , (int) $request->publish_minute , 0 , self::TIMEZONE
		)->setTimezone( config( 'app.timezone' ) );
	}

	/**
	 * The Jalali parts of a stored moment, for filling the form back in. jdf::jdate is avoided
	 * here because it changes PHP's default timezone as a side effect.
	 */
	public static function partsOf( ?Carbon $moment ): array {
		$tehran = ( $moment ?? Carbon::now() )->copy()->setTimezone( self::TIMEZONE );
		[ $jy , $jm , $jd ] = jdf::gregorian_to_jalali( $tehran->year , $tehran->month , $tehran->day );

		return [
			'year'   => (int) $jy ,
			'month'  => (int) $jm ,
			'day'    => (int) $jd ,
			'hour'   => $tehran->hour ,
			'minute' => $tehran->minute ,
		];
	}

	/** E.g. "۵ مهر ۱۴۰۵ ساعت ۱۴:۳۰" on the Tehran clock. */
	public static function format( $moment ): string {
		$parts = self::partsOf( Carbon::parse( $moment , config( 'app.timezone' ) ) );

		return jdf::tr_num( sprintf(
			'%d %s %d ساعت %02d:%02d' ,
			$parts['day'] , self::MONTHS[ $parts['month'] ] , $parts['year'] , $parts['hour'] , $parts['minute']
		) , 'fa' );
	}
}
