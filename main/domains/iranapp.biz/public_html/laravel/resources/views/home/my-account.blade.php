@extends('home.master')
@section('content')
    <div class="col-sm-12 list-of-purchases-in-my-account">
        <h2>خرید های انجام شده</h2>
        @if(count($orders) == 0 )
            <div class="alert alert-danger text-center">موردی یافت نشد!</div>
        @else
            <div class="table-responsive">
                <table class="table">
                    <thead>
                    <tr>
                        <th>محصول خریداری شده</th>
                        <th>مبلغ پرداختی</th>
                        <th>نام کاربری</th>
                        <th>رمز عبور</th>
                    </tr>
                    </thead>
                    <tbody>
                    @foreach($orders as $order)
                        <tr>
                            <td>
                                پلن {{ $order->title }}
                                - {{ $order->disk_space }} گیگابایت فضای دیسک
                                 - {{ $order->bandwidth_amount }} گیگا بایت پهنای باند ماهانه
                            </td>
                            <td>{{ $order->paid_price }} دلار</td>
                            <td>
                                {{ $order->user_name }}
                            </td>
                            <td>{{ $order->password }}</td>
                        </tr>
                    @endforeach
                    </tbody>
                </table>
            </div>
        @endif

    </div>
@endsection