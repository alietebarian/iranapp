@extends('admin.master')
@use('App\Models\EContract')
@section('content')
    @php
        $free = $counts[EContract::BILLING_FREE] ?? null;
        $paid = $counts[EContract::BILLING_PAID] ?? null;
    @endphp
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="row">
                    <div class="col-sm-12">
                        <h4 class="page-title">کاربران دارای قرارداد</h4>
                        <p class="text-muted page-title-alt">قراردادهای تایید شده، به ترتیب تاریخ پایان</p>
                    </div>
                </div>

                <div class="row">
                    <div class="col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-card-giftcard text-success"></i>
                            <h2 class="m-0 text-dark font-600">{{ number_format($free->total ?? 0) }}</h2>
                            <div class="text-muted m-t-5">قرارداد رایگان</div>
                        </div>
                    </div>
                    <div class="col-sm-6">
                        <div class="widget-panel widget-style-2 bg-white">
                            <i class="md md-attach-money text-info"></i>
                            <h2 class="m-0 text-dark font-600">{{ number_format($paid->total ?? 0) }}</h2>
                            <div class="text-muted m-t-5">
                                قرارداد پولی
                                @if($paid && $paid->amount_total)
                                    - مجموع دریافتی {{ number_format($paid->amount_total) }} تومان
                                @endif
                            </div>
                        </div>
                    </div>
                </div>

                <div class="card-box">
                    <ul class="nav nav-tabs" style="margin-bottom: 15px;">
                        <li class="{{ $billing === EContract::BILLING_FREE ? 'active' : '' }}">
                            <a href="{{ route('showActiveEContractsInAdminPanel', ['billing' => EContract::BILLING_FREE, 'q' => request('q')]) }}">
                                رایگان <span class="badge">{{ $free->total ?? 0 }}</span>
                            </a>
                        </li>
                        <li class="{{ $billing === EContract::BILLING_PAID ? 'active' : '' }}">
                            <a href="{{ route('showActiveEContractsInAdminPanel', ['billing' => EContract::BILLING_PAID, 'q' => request('q')]) }}">
                                پولی <span class="badge">{{ $paid->total ?? 0 }}</span>
                            </a>
                        </li>
                    </ul>

                    <form action="{{ url()->current() }}" method="get">
                        <input type="hidden" name="billing" value="{{ $billing }}">
                        <div class="row">
                            <div class="col-xs-8 col-md-4">
                                <div class="form-group">
                                    <label for="q-in-filter">جستجو (نام کسب و کار، نام مدیر یا شماره همراه)</label>
                                    <input type="text" value="{{ request('q') }}" name="q" id="q-in-filter" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-4 col-md-2">
                                <label>&nbsp;</label><br>
                                <button type="submit" class="btn btn-success">جستجو</button>
                            </div>
                        </div>
                    </form>

                    <div class="table-responsive">
                        @if(count($list) > 0)
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>کاربر</th>
                                    <th>کسب و کار</th>
                                    <th>شروع</th>
                                    <th>پایان</th>
                                    <th>مانده</th>
                                    @if($billing === EContract::BILLING_PAID)
                                        <th>مبلغ (تومان)</th>
                                    @endif
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $contract)
                                    @php($daysLeft = $contract->daysLeft())
                                    <tr class="{{ $daysLeft !== null && $daysLeft < 0 ? 'text-muted' : '' }}">
                                        <td>{{ $contract->id }}</td>
                                        <td>
                                            @if($contract->user)
                                                {{ $contract->user->first_name }} {{ $contract->user->last_name }}
                                                <br><small class="text-muted">{{ $contract->user->mobile }}</small>
                                            @endif
                                        </td>
                                        <td>{{ $contract->businessTypeLabel() }} {{ $contract->business_name }}</td>
                                        <td>{{ $contract->start_date }}</td>
                                        <td>{{ $contract->end_date }}</td>
                                        <td>
                                            @if($daysLeft === null)
                                                -
                                            @elseif($daysLeft < 0)
                                                <span class="label label-default">به پایان رسیده</span>
                                            @elseif($daysLeft <= EContract::EXPIRY_REMINDER_DAYS)
                                                <span class="label label-danger">{{ $daysLeft }} روز</span>
                                            @else
                                                {{ $daysLeft }} روز
                                            @endif
                                        </td>
                                        @if($billing === EContract::BILLING_PAID)
                                            <td>{{ $contract->formattedAmount() }}</td>
                                        @endif
                                        <td>
                                            <a href="{{ route('showEContractInAdminPanel', $contract->id) }}" class="btn btn-primary btn-xs">مشاهده قرارداد</a>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">قراردادی یافت نشد!</div>
                        @endif
                    </div>
                    {{ $list->links() }}
                </div>
            </div>
        </div>
    </div>
@endsection
