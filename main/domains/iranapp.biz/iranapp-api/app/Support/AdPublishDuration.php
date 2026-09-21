<?php

namespace App\Support;

use Carbon\Carbon;
use Illuminate\Http\Request;

/**
 * How long an ad stays visible in the app after an admin approves it.
 *
 * The admin panel offers a few preset lengths plus a free day count for anything else; one of
 * the two must be filled in, so no ad is ever published without someone having decided how long
 * it runs. The window always restarts the day the admin saves, so "three months" means three
 * months from approval rather than from whenever the user submitted the ad.
 */
class AdPublishDuration
{
	/** Preset lengths for the dropdown, in days. */
	const PRESETS = [
		30  => 'یک ماه',
		90  => 'سه ماه',
		180 => 'شش ماه',
		365 => 'یک سال',
	];

	/** A typed duration longer than this is far likelier to be a typo than an intention. */
	const MAX_CUSTOM_DAYS = 3650;

	/**
	 * `required_without` sits on the dropdown alone: that way an empty form reports the missing
	 * duration once instead of repeating itself under both fields.
	 */
	public static function rules(): array {
		return [
			'publish_duration'        => [
				'nullable' ,
				'required_without:publish_duration_custom' ,
				'integer' ,
				'in:' . implode( ',' , array_keys( self::PRESETS ) ) ,
			] ,
			'publish_duration_custom' => [
				'nullable' ,
				'integer' ,
				'min:1' ,
				'max:' . self::MAX_CUSTOM_DAYS ,
			] ,
		];
	}

	public static function messages(): array {
		return [
			'publish_duration.required_without' => 'مدت انتشار آگهی را مشخص کنید؛ یا از لیست انتخاب کنید یا تعداد روز دلخواه را وارد نمایید.',
			'publish_duration.integer'          => 'مدت انتشار انتخاب شده نامعتبر است.',
			'publish_duration.in'               => 'مدت انتشار انتخاب شده نامعتبر است.',
			'publish_duration_custom.integer'   => 'مدت انتشار دلخواه باید بر حسب روز و به صورت عدد وارد شود.',
			'publish_duration_custom.min'       => 'مدت انتشار دلخواه باید حداقل 1 روز باشد.',
			'publish_duration_custom.max'       => 'مدت انتشار دلخواه نمی تواند بیشتر از ' . self::MAX_CUSTOM_DAYS . ' روز باشد.',
		];
	}

	/** The typed day count wins when the admin fills in both fields. */
	public static function daysFrom( Request $request ): ?int {
		if ( $request->filled( 'publish_duration_custom' ) ) {
			return (int) $request->input( 'publish_duration_custom' );
		}

		if ( $request->filled( 'publish_duration' ) ) {
			return (int) $request->input( 'publish_duration' );
		}

		return null;
	}

	/**
	 * Moves the ad's visibility window to [today, today + duration]. Callers validate first, so a
	 * missing duration here means validation was skipped; the existing dates are left untouched
	 * rather than silently reset.
	 */
	public static function applyTo( $ads , Request $request ): void {
		$days = self::daysFrom( $request );
		if ( $days === null ) {
			return;
		}

		$ads->valid_since = Carbon::now()->toDateString();
		$ads->valid_until = Carbon::now()->addDays( $days )->toDateString();
	}
}
