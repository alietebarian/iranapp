@extends('admin.master')
@section('style')
    <style>
        .e-contract-doc {
            background: #fff;
            border: 1px solid #e3e3e3;
            border-radius: 6px;
            padding: 30px 35px;
            line-height: 2.2;
            font-size: 14px;
            text-align: justify;
        }

        .e-contract-doc .doc-header {
            border-bottom: 2px solid #b71c1c;
            padding-bottom: 12px;
            margin-bottom: 20px;
        }

        .e-contract-doc .doc-header img {
            height: 45px;
            margin-top: 8px;
        }

        .e-contract-doc .doc-title {
            color: #b71c1c;
            font-size: 18px;
            font-weight: bold;
            margin: 0;
        }

        .e-contract-doc h4 {
            color: #b71c1c;
            font-size: 14px;
            font-weight: bold;
            margin: 18px 0 4px;
        }

        .e-contract-doc .doc-plain {
            white-space: pre-wrap;
        }

        .e-contract-info th {
            width: 190px;
            color: #777;
            font-weight: normal;
        }
    </style>
@endsection
@section('content')
    @php
        $sections = $contract->highlightedSections();
    @endphp
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="row">
                    <div class="col-xs-12">
                        <a href="{{ route('showEContractsInAdminPanel', ['status' => $contract->status]) }}"
                           class="btn btn-default btn-sm m-b-10">بازگشت به لیست قراردادها</a>
                    </div>
                </div>

                <div class="row">
                    {{-- The contract itself --}}
                    <div class="col-md-7">
                        <div class="card-box">
                            <div class="e-contract-doc">
                                <div class="doc-header clearfix">
                                    {{-- The same wordmark the app shows at the top of the contract (R.drawable.iran_app_logo1). --}}
                                    <img src="{{ URL::to('/admin/assets/images/iran_app_logo.png') }}" alt="ایران اپ" class="pull-right">
                                    <div class="pull-left text-left">
                                        <p class="doc-title">{{ \App\Support\EContractTemplate::TITLE }}</p>
                                        <div>تاریخ: <b>{{ $contract->contract_date }}</b></div>
                                        <div class="text-muted"><small>شماره: {{ $contract->id }}</small></div>
                                    </div>
                                </div>

                                @if($sections)
                                    @foreach($sections as $section)
                                        @if($section['heading'])
                                            <h4>{{ $section['heading'] }}:</h4>
                                        @endif
                                        <p>{!! $section['html'] !!}</p>
                                    @endforeach
                                @else
                                    <div class="alert alert-info">
                                        این قرارداد با نسخه {{ $contract->template_version }} متن قرارداد ثبت شده است؛
                                        متن دقیقی که کاربر پذیرفته در زیر آمده است.
                                    </div>
                                    <div class="doc-plain">{{ $contract->contract_text }}</div>
                                @endif
                            </div>
                        </div>
                    </div>

                    {{-- Review panel --}}
                    <div class="col-md-5">
                        <div class="card-box">
                            <h4 class="m-t-0 header-title"><b>وضعیت قرارداد</b></h4>
                            @if($contract->status == 'pending')
                                <div class="alert alert-warning">این قرارداد در انتظار بررسی است.</div>
                            @elseif($contract->status == 'approved')
                                <div class="alert alert-success">
                                    این قرارداد تایید شده و کاربر، کاربر پرو است.
                                    @if($contract->reviewed_at)
                                        <br><small>تاریخ تایید: {{ \App\Support\JalaliDate::fromTimestamp($contract->reviewed_at, 'Y/m/d - H:i') }}
                                            @if($contract->reviewer) - توسط {{ $contract->reviewer->first_name }} {{ $contract->reviewer->last_name }} @endif
                                        </small>
                                    @endif
                                </div>
                            @else
                                <div class="alert alert-danger">
                                    این قرارداد رد شده است.
                                    <br><b>دلیل:</b> {{ $contract->rejection_reason }}
                                    @if($contract->reviewed_at)
                                        <br><small>تاریخ رد: {{ \App\Support\JalaliDate::fromTimestamp($contract->reviewed_at, 'Y/m/d - H:i') }}
                                            @if($contract->reviewer) - توسط {{ $contract->reviewer->first_name }} {{ $contract->reviewer->last_name }} @endif
                                        </small>
                                    @endif
                                </div>
                            @endif

                            @if($contract->isPending())
                                <form action="{{ route('approveEContract', $contract->id) }}" method="post"
                                      onsubmit="return confirm('با تایید این قرارداد، کاربر به کاربر پرو ارتقا می یابد. ادامه می دهید؟');">
                                    {{ csrf_field() }}
                                    <input type="hidden" name="_method" value="PUT">
                                    <button type="submit" class="btn btn-success btn-block waves-effect waves-light">
                                        <i class="fa fa-check"></i> تایید و ثبت قرارداد
                                    </button>
                                </form>

                                <hr>

                                <form action="{{ route('rejectEContract', $contract->id) }}" method="post">
                                    {{ csrf_field() }}
                                    <input type="hidden" name="_method" value="PUT">
                                    <div class="form-group">
                                        <label for="rejection_reason">دلیل رد قرارداد (برای کاربر نمایش داده می شود)</label>
                                        <textarea name="rejection_reason" id="rejection_reason" rows="4" class="form-control"
                                                  placeholder="مثلا: کد ملی با نام مدیر مطابقت ندارد؛ لطفا اصلاح کنید.">{{ old('rejection_reason') }}</textarea>
                                        @if($errors->has('rejection_reason'))
                                            <span class="input-field-errors">{{ $errors->first('rejection_reason') }}</span>
                                        @endif
                                    </div>
                                    <button type="submit" class="btn btn-danger btn-block waves-effect waves-light">
                                        <i class="fa fa-times"></i> رد قرارداد
                                    </button>
                                </form>
                            @endif
                        </div>

                        <div class="card-box">
                            <h4 class="m-t-0 header-title"><b>مشخصات طرف دوم</b></h4>
                            <table class="table table-condensed e-contract-info m-0">
                                <tr><th>نوع و نام کسب و کار</th><td>{{ $contract->businessTypeLabel() }} {{ $contract->business_name }}</td></tr>
                                <tr><th>مدیر</th><td>{{ $contract->managerTitleLabel() }} {{ $contract->manager_name }}</td></tr>
                                <tr><th>کد ملی</th><td>{{ $contract->national_code }}</td></tr>
                                <tr><th>تلفن ثابت</th><td>{{ $contract->phone }}</td></tr>
                                <tr><th>شماره همراه</th><td>{{ $contract->mobile }}</td></tr>
                                <tr><th>آدرس</th><td>{{ $contract->address }}</td></tr>
                                <tr><th>خدمات / محصولات</th><td>{{ $contract->subject }}</td></tr>
                                <tr><th>مدت قرارداد</th><td>از {{ $contract->start_date }} لغایت {{ $contract->end_date }} ({{ \App\Models\EContract::durationLabel($contract->duration_months) }})</td></tr>
                                <tr><th>درصد تخفیف</th><td>{{ $contract->discount_percent }}٪</td></tr>
                            </table>
                        </div>

                        <div class="card-box">
                            <h4 class="m-t-0 header-title"><b>حساب کاربری و سابقه پذیرش</b></h4>
                            <table class="table table-condensed e-contract-info m-0">
                                @if($contract->user)
                                    <tr>
                                        <th>کاربر</th>
                                        <td>
                                            <a href="{{ route('showUserUpdatePage', $contract->user->id) }}">
                                                {{ $contract->user->first_name }} {{ $contract->user->last_name }}
                                            </a>
                                            ({{ $contract->user->mobile }})
                                        </td>
                                    </tr>
                                    <tr>
                                        <th>نقش فعلی کاربر</th>
                                        <td>
                                            @if($contract->user->isPro())
                                                <span class="label label-success">کاربر پرو</span>
                                            @else
                                                <span class="label label-default">کاربر عادی</span>
                                            @endif
                                        </td>
                                    </tr>
                                @endif
                                <tr><th>تاریخ و ساعت ارسال</th><td>{{ \App\Support\JalaliDate::fromTimestamp($contract->created_at, 'Y/m/d - H:i') }}</td></tr>
                                <tr><th>پذیرش شرایط قرارداد</th><td>{{ \App\Support\JalaliDate::fromTimestamp($contract->terms_accepted_at, 'Y/m/d - H:i') }}</td></tr>
                                <tr><th>نسخه متن قرارداد</th><td>{{ $contract->template_version }}</td></tr>
                                @if($contract->ip_address)
                                    <tr><th>آدرس IP</th><td>{{ $contract->ip_address }}</td></tr>
                                @endif
                                @if($contract->app_version)
                                    <tr><th>نسخه اپلیکیشن</th><td>{{ $contract->app_version }}</td></tr>
                                @endif
                            </table>
                        </div>

                        @if(count($history) > 0)
                            <div class="card-box">
                                <h4 class="m-t-0 header-title"><b>درخواست های دیگر این کاربر</b></h4>
                                <table class="table table-condensed m-0">
                                    @foreach($history as $item)
                                        <tr>
                                            <td>#{{ $item->id }}</td>
                                            <td>{{ \App\Support\JalaliDate::fromTimestamp($item->created_at, 'Y/m/d') }}</td>
                                            <td>{{ $item->statusLabel() }}</td>
                                            <td><a href="{{ route('showEContractInAdminPanel', $item->id) }}">مشاهده</a></td>
                                        </tr>
                                        @if($item->rejection_reason)
                                            <tr>
                                                <td colspan="4" class="text-muted" style="border-top: 0;">
                                                    <small>دلیل رد: {{ $item->rejection_reason }}</small>
                                                </td>
                                            </tr>
                                        @endif
                                    @endforeach
                                </table>
                            </div>
                        @endif
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
