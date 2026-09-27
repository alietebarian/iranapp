@extends('admin.master')
@section('style')
    <style>
        /* The card as the app shows it once approved (MembershipCardActivity). */
        .membership-card {
            max-width: 460px;
            margin: 0 auto;
            border-radius: 14px;
            padding: 18px 22px;
            color: #fff;
            background: radial-gradient(circle at 75% 30%, #ef5350, #b71c1c 70%);
            box-shadow: 0 4px 14px rgba(0, 0, 0, .25);
            line-height: 1.9;
        }

        .membership-card .mc-head {
            border-bottom: 1px solid rgba(255, 255, 255, .35);
            padding-bottom: 8px;
            margin-bottom: 10px;
        }

        .membership-card .mc-head img {
            height: 34px;
            filter: brightness(0) invert(1);
        }

        .membership-card .mc-title {
            font-weight: bold;
            font-size: 13px;
            margin-top: 6px;
        }

        .membership-card .mc-serial {
            direction: ltr;
            text-align: center;
            font-size: 20px;
            letter-spacing: 2px;
            font-weight: bold;
            margin: 8px 0 12px;
        }

        .membership-card .mc-label {
            opacity: .8;
            font-size: 12px;
        }

        .membership-card ol {
            margin: 0 0 8px;
            padding-right: 20px;
        }

        .membership-card.mc-pending {
            background: radial-gradient(circle at 75% 30%, #9e9e9e, #616161 70%);
        }

        .membership-info th {
            width: 160px;
            color: #777;
            font-weight: normal;
        }
    </style>
@endsection
@section('content')
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="row">
                    <div class="col-xs-12">
                        <a href="{{ route('showMembershipCardsInAdminPanel', ['status' => $card->status]) }}"
                           class="btn btn-default btn-sm m-b-10">بازگشت به لیست درخواست ها</a>
                    </div>
                </div>

                <div class="row">
                    {{-- The card --}}
                    <div class="col-md-7">
                        <div class="card-box">
                            <h4 class="m-t-0 header-title"><b>پیش نمایش کارت</b></h4>
                            <div class="membership-card {{ $card->status == 'approved' ? '' : 'mc-pending' }}">
                                <div class="mc-head clearfix">
                                    <img src="{{ URL::to('/admin/assets/images/iran_app_logo.png') }}" alt="ایران اپ" class="pull-right">
                                    <div class="pull-left mc-title">{{ \App\Models\MembershipCard::TITLE }}</div>
                                </div>
                                <div class="mc-label text-center">شماره سریال</div>
                                <div class="mc-serial">{{ $card->formattedSerial() }}</div>
                                <div class="mc-label">اعضا</div>
                                <ol>
                                    @foreach($card->members as $member)
                                        <li>{{ $member }}</li>
                                    @endforeach
                                </ol>
                                <div class="row">
                                    <div class="col-xs-6">
                                        <div class="mc-label">تاریخ عضویت</div>
                                        <div>{{ $card->membership_date }}</div>
                                    </div>
                                    <div class="col-xs-6">
                                        <div class="mc-label">اعتبار تا</div>
                                        <div>{{ $card->expiry_date }}</div>
                                    </div>
                                </div>
                            </div>
                            @if($card->status != 'approved')
                                <p class="text-muted text-center m-t-10"><small>کارت پس از تایید، با همین اطلاعات در اپلیکیشن برای کاربر نمایش داده می شود.</small></p>
                            @endif
                        </div>
                    </div>

                    {{-- Review panel --}}
                    <div class="col-md-5">
                        <div class="card-box">
                            <h4 class="m-t-0 header-title"><b>وضعیت درخواست</b></h4>
                            @if($card->status == 'pending')
                                <div class="alert alert-warning">
                                    این درخواست در انتظار بررسی است.
                                    @if($card->rejection_reason)
                                        <br><small><b>توضیحی که پیش از این برای کاربر فرستاده شد:</b> {{ $card->rejection_reason }}</small>
                                        <br><small>کاربر درخواست را اصلاح کرده و دوباره ارسال کرده است.</small>
                                    @endif
                                </div>
                            @elseif($card->status == 'approved')
                                <div class="alert alert-success">
                                    این کارت تایید شده است.
                                    @if($card->isExpired())
                                        <br><b>اعتبار کارت به پایان رسیده است.</b>
                                    @endif
                                    @if($card->reviewed_at)
                                        <br><small>تاریخ تایید: {{ \App\Support\JalaliDate::fromTimestamp($card->reviewed_at, 'Y/m/d - H:i') }}
                                            @if($card->reviewer) - توسط {{ $card->reviewer->first_name }} {{ $card->reviewer->last_name }} @endif
                                        </small>
                                    @endif
                                </div>
                            @else
                                <div class="alert alert-danger">
                                    این درخواست رد شده و منتظر اصلاح توسط کاربر است.
                                    <br><b>توضیح برای کاربر:</b> {{ $card->rejection_reason }}
                                    @if($card->reviewed_at)
                                        <br><small>تاریخ رد: {{ \App\Support\JalaliDate::fromTimestamp($card->reviewed_at, 'Y/m/d - H:i') }}
                                            @if($card->reviewer) - توسط {{ $card->reviewer->first_name }} {{ $card->reviewer->last_name }} @endif
                                        </small>
                                    @endif
                                </div>
                            @endif

                            @if($card->isPending())
                                <form action="{{ route('approveMembershipCard', $card->id) }}" method="post"
                                      onsubmit="return confirm('کارت عضویت تایید شود؟');">
                                    {{ csrf_field() }}
                                    <input type="hidden" name="_method" value="PUT">
                                    <button type="submit" class="btn btn-success btn-block waves-effect waves-light">
                                        <i class="fa fa-check"></i> تایید کارت عضویت
                                    </button>
                                </form>

                                <hr>

                                <form action="{{ route('rejectMembershipCard', $card->id) }}" method="post">
                                    {{ csrf_field() }}
                                    <input type="hidden" name="_method" value="PUT">
                                    <div class="form-group">
                                        <label for="rejection_reason">مواردی که کاربر باید اصلاح کند (برای کاربر نمایش داده می شود)</label>
                                        <textarea name="rejection_reason" id="rejection_reason" rows="4" class="form-control"
                                                  placeholder="مثلا: نام خانوادگی عضو دوم را کامل وارد کنید.">{{ old('rejection_reason') }}</textarea>
                                        @if($errors->has('rejection_reason'))
                                            <span class="input-field-errors">{{ $errors->first('rejection_reason') }}</span>
                                        @endif
                                    </div>
                                    <button type="submit" class="btn btn-danger btn-block waves-effect waves-light">
                                        <i class="fa fa-times"></i> رد درخواست و ارسال توضیح به کاربر
                                    </button>
                                </form>
                            @endif
                        </div>

                        <div class="card-box">
                            <h4 class="m-t-0 header-title"><b>اطلاعات درخواست</b></h4>
                            <table class="table table-condensed membership-info m-0">
                                <tr><th>شماره سریال</th><td dir="ltr" style="text-align: right;">{{ $card->formattedSerial() }}</td></tr>
                                <tr>
                                    <th>اعضا ({{ count($card->members) }} نفر)</th>
                                    <td>
                                        @foreach($card->members as $i => $member)
                                            {{ $i + 1 }}. {{ $member }}<br>
                                        @endforeach
                                    </td>
                                </tr>
                                <tr><th>تاریخ عضویت</th><td>{{ $card->membership_date }}</td></tr>
                                <tr><th>تاریخ پایان اعتبار</th><td>{{ $card->expiry_date }}</td></tr>
                                @if($card->user)
                                    <tr>
                                        <th>کاربر</th>
                                        <td>
                                            <a href="{{ route('showUserUpdatePage', $card->user->id) }}">
                                                {{ $card->user->first_name }} {{ $card->user->last_name }}
                                            </a>
                                            ({{ $card->user->mobile }})
                                        </td>
                                    </tr>
                                @endif
                                <tr><th>تاریخ و ساعت ارسال</th><td>{{ \App\Support\JalaliDate::fromTimestamp($card->submitted_at, 'Y/m/d - H:i') }}</td></tr>
                                @if($card->app_version)
                                    <tr><th>نسخه اپلیکیشن</th><td>{{ $card->app_version }}</td></tr>
                                @endif
                            </table>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
