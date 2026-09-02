@extends('home.master')
@section('content')
    <div class="col-sm-12">
        <div class="table-responsive">
            <table class="table purchase-table">
                <thead>
                <tr>
                    <th>مشخصات محصول</th>
                    <th>قیمت</th>
                </tr>
                </thead>
                <tbody>
                <tr>
                    <td>پلن {{ $product->title }}، {{ $product->disk_space }} گیگابایت فضای دیسک، {{ $product->bandwidth_amount }} گیگا بایت پهنای باند ماهانه</td>
                    <td>{{ $product->price_per_month }} دلار</td>
                </tr>
                </tbody>
            </table>
        </div>
        <div class="finalize_purchase">
            <form action="{{ route('order.store') }}" method="POST">
                {{ csrf_field() }}
                <input type="hidden" name="product_id" value="{{ $product->id }}">
                <label for="agreement" class="checkbox">
                    <input type="checkbox" name="agreements" id="agreement">
                    قوانین و مقررات خرید را به طور کامل مطالعه کرده ام و پذیرفته ام.
                </label>
                <button type="submit" id="finalize_purchase_submit_btn" class="btn hk_btn hk_login pull-left disabled">نهایی کردن خرید</button>
            </form>
        </div>
    </div>
@endsection