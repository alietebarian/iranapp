@extends('admin.master')
@section('content')
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">قراردادهای الکترونیک</h1>

                    <ul class="nav nav-tabs" style="margin-bottom: 15px;">
                        @foreach(\App\Models\EContract::STATUS_LABELS as $key => $label)
                            <li class="{{ $status == $key ? 'active' : '' }}">
                                <a href="{{ route('showEContractsInAdminPanel', ['status' => $key, 'q' => request('q')]) }}">
                                    {{ $label }}
                                    <span class="badge {{ $key == 'pending' ? 'badge-danger' : '' }}">{{ $counts[$key] ?? 0 }}</span>
                                </a>
                            </li>
                        @endforeach
                        <li class="{{ $status == 'all' ? 'active' : '' }}">
                            <a href="{{ route('showEContractsInAdminPanel', ['status' => 'all', 'q' => request('q')]) }}">
                                همه <span class="badge">{{ $counts->sum() }}</span>
                            </a>
                        </li>
                    </ul>

                    <form action="{{ url()->current() }}" method="get">
                        <input type="hidden" name="status" value="{{ $status }}">
                        <div class="row">
                            <div class="col-xs-8 col-md-4">
                                <div class="form-group">
                                    <label for="q-in-filter">جستجو (نام کسب و کار، نام مدیر، شماره همراه یا کد ملی)</label>
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
                                    <th>کسب و کار</th>
                                    <th>مدیر</th>
                                    <th>شماره همراه</th>
                                    <th>کاربر</th>
                                    <th>تخفیف</th>
                                    <th>مدت قرارداد</th>
                                    <th>تاریخ ارسال</th>
                                    <th>وضعیت</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $contract)
                                    <tr>
                                        <td>{{ $contract->id }}</td>
                                        <td>{{ $contract->businessTypeLabel() }} {{ $contract->business_name }}</td>
                                        <td>{{ $contract->managerTitleLabel() }} {{ $contract->manager_name }}</td>
                                        <td>{{ $contract->mobile }}</td>
                                        <td>
                                            @if($contract->user)
                                                {{ $contract->user->first_name }} {{ $contract->user->last_name }}
                                                <br><small class="text-muted">{{ $contract->user->mobile }}</small>
                                            @endif
                                        </td>
                                        <td>{{ $contract->discount_percent }}٪</td>
                                        <td>{{ \App\Models\EContract::durationLabel($contract->duration_months) }}</td>
                                        <td>{{ \App\Support\JalaliDate::fromTimestamp($contract->created_at, 'Y/m/d - H:i') }}</td>
                                        <td>
                                            @if($contract->status == 'pending')
                                                <span class="label label-warning">{{ $contract->statusLabel() }}</span>
                                            @elseif($contract->status == 'approved')
                                                <span class="label label-success">{{ $contract->statusLabel() }}</span>
                                            @else
                                                <span class="label label-danger">{{ $contract->statusLabel() }}</span>
                                            @endif
                                        </td>
                                        <td>
                                            <a href="{{ route('showEContractInAdminPanel', $contract->id) }}"
                                               class="btn btn-primary btn-xs">
                                                {{ $contract->status == 'pending' ? 'بررسی قرارداد' : 'مشاهده قرارداد' }}
                                            </a>
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
