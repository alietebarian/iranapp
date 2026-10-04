@extends('admin.master')
@section('content')
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">کارت عضویت</h1>

                    <ul class="nav nav-tabs" style="margin-bottom: 15px;">
                        @foreach(\App\Models\MembershipCard::STATUS_LABELS as $key => $label)
                            <li class="{{ $status == $key ? 'active' : '' }}">
                                <a href="{{ route('showMembershipCardsInAdminPanel', ['status' => $key, 'q' => request('q')]) }}">
                                    {{ $label }}
                                    <span class="badge {{ $key == 'pending' ? 'badge-danger' : '' }}">{{ $counts[$key] ?? 0 }}</span>
                                </a>
                            </li>
                        @endforeach
                        <li class="{{ $status == 'all' ? 'active' : '' }}">
                            <a href="{{ route('showMembershipCardsInAdminPanel', ['status' => 'all', 'q' => request('q')]) }}">
                                همه <span class="badge">{{ $counts->sum() }}</span>
                            </a>
                        </li>
                    </ul>

                    <form action="{{ url()->current() }}" method="get">
                        <input type="hidden" name="status" value="{{ $status }}">
                        <div class="row">
                            <div class="col-xs-8 col-md-4">
                                <div class="form-group">
                                    <label for="q-in-filter">جستجو (نام اعضا، نام کاربر، شماره همراه، شماره سریال یا کد ملی)</label>
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
                                    <th>شماره سریال</th>
                                    <th>کاربر</th>
                                    <th>اعضا</th>
                                    <th>تاریخ عضویت</th>
                                    <th>تاریخ اعتبار</th>
                                    <th>تاریخ ارسال</th>
                                    <th>وضعیت</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $card)
                                    <tr>
                                        <td>{{ $card->id }}</td>
                                        <td dir="ltr" style="text-align: right; white-space: nowrap;">{{ $card->formattedSerial() }}</td>
                                        <td>
                                            @if($card->user)
                                                {{ $card->user->first_name }} {{ $card->user->last_name }}
                                                <br><small class="text-muted">{{ $card->user->mobile }}</small>
                                            @endif
                                        </td>
                                        <td>
                                            {{ implode('، ', $card->members) }}
                                            <br><small class="text-muted">{{ count($card->members) }} نفر</small>
                                        </td>
                                        <td>{{ $card->membership_date }}</td>
                                        <td>
                                            {{ $card->expiry_date }}
                                            @if($card->status == 'approved' && $card->isExpired())
                                                <br><span class="label label-default">منقضی شده</span>
                                            @endif
                                        </td>
                                        <td>{{ \App\Support\JalaliDate::fromTimestamp($card->submitted_at, 'Y/m/d - H:i') }}</td>
                                        <td>
                                            @if($card->status == 'pending')
                                                <span class="label label-warning">{{ $card->statusLabel() }}</span>
                                            @elseif($card->status == 'approved')
                                                <span class="label label-success">{{ $card->statusLabel() }}</span>
                                            @else
                                                <span class="label label-danger">{{ $card->statusLabel() }}</span>
                                            @endif
                                        </td>
                                        <td>
                                            <a href="{{ route('showMembershipCardInAdminPanel', $card->id) }}"
                                               class="btn btn-primary btn-xs">
                                                {{ $card->status == 'pending' ? 'بررسی درخواست' : 'مشاهده' }}
                                            </a>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">درخواستی یافت نشد!</div>
                        @endif
                    </div>
                    {{ $list->links() }}
                </div>
            </div>
        </div>
    </div>
@endsection
