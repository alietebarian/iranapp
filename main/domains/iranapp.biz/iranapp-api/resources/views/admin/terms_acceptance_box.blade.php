{{--
    The legal record of the user accepting the terms before submitting this ad.
    Expects $ads (the ad being edited) and $adType (one of App\Support\TermsConsent's TYPE_* values).
--}}
@php
    $termsAcceptance = \App\Support\TermsConsent::forAd($adType, $ads->id);
    $termsAcceptedAt = null;
    if ($termsAcceptance) {
        try {
            $termsAcceptedAt = \App\Libraries\jdf::jdate(
                'j F Y - H:i',
                \Carbon\Carbon::parse($termsAcceptance->accepted_at)->getTimestamp()
            );
        } catch (\Throwable $e) {
            $termsAcceptedAt = $termsAcceptance->accepted_at;
        }
    }
@endphp

<div class="col-xs-12">
    <div class="form-group">
        <label>پذیرش قوانین و مقررات:</label>
        @if($termsAcceptance)
            <div class="alert alert-success" style="margin-bottom:0">
                کاربر پیش از ثبت این آگهی، قوانین و مقررات را پذیرفته است.
                <br>نسخه قوانین: <b>{{ $termsAcceptance->terms_version }}</b>
                <br>تاریخ و ساعت پذیرش: <b>{{ $termsAcceptedAt }}</b>
                @if($termsAcceptance->ip_address)
                    <br>آدرس IP: <b>{{ $termsAcceptance->ip_address }}</b>
                @endif
                @if($termsAcceptance->app_version)
                    <br>نسخه اپلیکیشن: <b>{{ $termsAcceptance->app_version }}</b>
                @endif
            </div>
        @else
            <div class="alert alert-warning" style="margin-bottom:0">
                برای این آگهی سابقه پذیرش قوانین ثبت نشده است؛ یعنی آگهی از پنل ادمین ثبت شده
                یا مربوط به پیش از افزوده شدن این قابلیت است.
            </div>
        @endif
    </div>
</div>
