{{--
    Status of an electronic contract and what the admin can do next (admin.e_contracts.show).
    pending → free approval, or a payment request (amount + card) → the user confirms paying →
    final approval, or "payment not received" which sends it back to awaiting payment.
--}}
@use('App\Models\EContract')
@php
    $reviewedBy = $contract->reviewer ? ' - توسط ' . $contract->reviewer->first_name . ' ' . $contract->reviewer->last_name : '';
    $canReject = in_array($contract->status, [EContract::STATUS_PENDING, EContract::STATUS_AWAITING_PAYMENT, EContract::STATUS_PAYMENT_SUBMITTED], true);
    $canDecide = in_array($contract->status, [EContract::STATUS_PENDING, EContract::STATUS_AWAITING_PAYMENT], true);
    // Reopen the payment form after a failed validation.
    $paymentFormOpen = $contract->status === EContract::STATUS_AWAITING_PAYMENT
        || $errors->hasAny(['amount', 'card_number', 'card_holder', 'payment_message']);
@endphp

<div class="card-box">
    <h4 class="m-t-0 header-title"><b>وضعیت قرارداد</b></h4>

    @switch($contract->status)
        @case(EContract::STATUS_PENDING)
            <div class="alert alert-warning">
                این قرارداد در انتظار بررسی است. قرارداد را به صورت رایگان تایید کنید، یا مبلغ و شماره کارت را برای کاربر بفرستید.
            </div>
            @break

        @case(EContract::STATUS_AWAITING_PAYMENT)
            <div class="alert alert-info">
                در انتظار پرداخت کاربر:
                <b>{{ $contract->formattedAmount() }} تومان</b>
                به کارت <b dir="ltr">{{ $contract->formattedCardNumber() }}</b> ({{ $contract->card_holder }})
                @if($contract->payment_requested_at)
                    <br><small>ارسال شده در {{ \App\Support\JalaliDate::fromTimestamp($contract->payment_requested_at, 'Y/m/d - H:i') }}</small>
                @endif
                @if($contract->payment_rejection_reason)
                    <br><span class="text-danger"><b>پرداخت قبلی تایید نشد:</b> {{ $contract->payment_rejection_reason }}</span>
                @endif
            </div>
            @break

        @case(EContract::STATUS_PAYMENT_SUBMITTED)
            <div class="alert alert-warning">
                <b>کاربر اعلام کرده هزینه را پرداخت کرده است.</b> پس از بررسی واریز، تایید نهایی را بزنید.
                <br>مبلغ: <b>{{ $contract->formattedAmount() }} تومان</b>
                به کارت <b dir="ltr">{{ $contract->formattedCardNumber() }}</b> ({{ $contract->card_holder }})
                <br>شماره پیگیری اعلام شده توسط کاربر: <b>{{ $contract->payment_reference ?: 'وارد نشده' }}</b>
                <br><small>زمان اعلام پرداخت: {{ \App\Support\JalaliDate::fromTimestamp($contract->payment_submitted_at, 'Y/m/d - H:i') }}</small>
            </div>
            @break

        @case(EContract::STATUS_APPROVED)
            @php($daysLeft = $contract->daysLeft())
            <div class="alert alert-success m-b-0">
                این قرارداد تایید شده و کاربر، کاربر پرو است.
                <br>نوع قرارداد: <b>{{ $contract->billingLabel() ?? 'رایگان' }}</b>
                @if($contract->isPaid())
                    - مبلغ پرداخت شده: <b>{{ $contract->formattedAmount() }} تومان</b>
                    @if($contract->payment_reference)
                        (شماره پیگیری: {{ $contract->payment_reference }})
                    @endif
                @endif
                <br>مدت: از {{ $contract->start_date }} تا {{ $contract->end_date }}
                @if($daysLeft !== null)
                    @if($daysLeft < 0)
                        <b class="text-danger">(به پایان رسیده)</b>
                    @elseif($daysLeft <= EContract::EXPIRY_REMINDER_DAYS)
                        <b class="text-danger">({{ $daysLeft }} روز مانده)</b>
                    @else
                        ({{ $daysLeft }} روز مانده)
                    @endif
                @endif
                @if($contract->reviewed_at)
                    <br><small>تاریخ تایید: {{ \App\Support\JalaliDate::fromTimestamp($contract->reviewed_at, 'Y/m/d - H:i') }}{{ $reviewedBy }}</small>
                @endif
            </div>
            @break

        @default
            <div class="alert alert-danger m-b-0">
                این قرارداد رد شده است.
                <br><b>دلیل:</b> {{ $contract->rejection_reason }}
                @if($contract->reviewed_at)
                    <br><small>تاریخ رد: {{ \App\Support\JalaliDate::fromTimestamp($contract->reviewed_at, 'Y/m/d - H:i') }}{{ $reviewedBy }}</small>
                @endif
            </div>
    @endswitch

    {{-- The user has confirmed paying: final approval, or the money did not arrive. --}}
    @if($contract->status === EContract::STATUS_PAYMENT_SUBMITTED)
        <form action="{{ route('confirmEContractPayment', $contract->id) }}" method="post"
              onsubmit="return confirm('واریز مبلغ را بررسی کرده اید؟ با تایید نهایی، قرارداد ثبت و کاربر به کاربر پرو ارتقا می یابد.');">
            {{ csrf_field() }}
            <input type="hidden" name="_method" value="PUT">
            <button type="submit" class="btn btn-success btn-block waves-effect waves-light">
                <i class="fa fa-check"></i> پرداخت دریافت شد؛ تایید نهایی قرارداد
            </button>
        </form>

        <hr>

        <form action="{{ route('rejectEContractPayment', $contract->id) }}" method="post">
            {{ csrf_field() }}
            <input type="hidden" name="_method" value="PUT">
            <div class="form-group">
                <label for="payment_rejection_reason">پرداخت دریافت نشد؟ توضیح برای کاربر</label>
                <textarea name="payment_rejection_reason" id="payment_rejection_reason" rows="3" class="form-control"
                          placeholder="مثلا: مبلغی با این شماره پیگیری به حساب واریز نشده است.">{{ old('payment_rejection_reason') }}</textarea>
                @if($errors->has('payment_rejection_reason'))
                    <span class="input-field-errors">{{ $errors->first('payment_rejection_reason') }}</span>
                @endif
            </div>
            <button type="submit" class="btn btn-warning btn-block waves-effect waves-light">
                <i class="fa fa-undo"></i> پرداخت تایید نشد؛ کاربر دوباره پرداخت کند
            </button>
        </form>
    @endif

    {{-- Free, or paid with an amount and a card. --}}
    @if($canDecide)
        @if($contract->status === EContract::STATUS_PENDING)
            <div class="form-group">
                <label>نوع قرارداد</label>
                <div>
                    <label class="radio-inline"><input type="radio" name="e_contract_billing" value="free" {{ $paymentFormOpen ? '' : 'checked' }}> رایگان</label>
                    <label class="radio-inline"><input type="radio" name="e_contract_billing" value="paid" {{ $paymentFormOpen ? 'checked' : '' }}> با هزینه</label>
                </div>
            </div>
        @endif

        <div id="e-contract-free" style="{{ $paymentFormOpen && $contract->status === EContract::STATUS_PENDING ? 'display: none;' : '' }}">
            <form action="{{ route('approveEContract', $contract->id) }}" method="post"
                  onsubmit="return confirm('قرارداد به صورت رایگان تایید و کاربر به کاربر پرو ارتقا می یابد. ادامه می دهید؟');">
                {{ csrf_field() }}
                <input type="hidden" name="_method" value="PUT">
                <button type="submit" class="btn {{ $contract->status === EContract::STATUS_PENDING ? 'btn-success' : 'btn-default' }} btn-block waves-effect waves-light">
                    <i class="fa fa-check"></i>
                    {{ $contract->status === EContract::STATUS_PENDING ? 'تایید رایگان و ثبت قرارداد' : 'صرف نظر از پرداخت و تایید رایگان' }}
                </button>
            </form>
            @if($contract->status === EContract::STATUS_AWAITING_PAYMENT)
                <hr>
            @endif
        </div>

        <div id="e-contract-paid" style="{{ $paymentFormOpen ? '' : 'display: none;' }}">
            <form action="{{ route('requestEContractPayment', $contract->id) }}" method="post"
                  onsubmit="return confirm('مبلغ و شماره کارت برای کاربر ارسال شود؟');">
                {{ csrf_field() }}
                <input type="hidden" name="_method" value="PUT">
                @if($contract->status === EContract::STATUS_AWAITING_PAYMENT)
                    <p class="text-muted"><small>برای اصلاح مبلغ یا شماره کارت، فرم زیر را تغییر دهید و دوباره ارسال کنید.</small></p>
                @endif
                <div class="form-group">
                    <label for="amount">مبلغ (تومان)</label>
                    <input type="text" name="amount" id="amount" class="form-control" dir="ltr" inputmode="numeric"
                           value="{{ old('amount', $contract->amount) }}" placeholder="مثلا 1500000">
                    @if($errors->has('amount'))
                        <span class="input-field-errors">{{ $errors->first('amount') }}</span>
                    @endif
                </div>
                <div class="form-group">
                    <label for="card_number">شماره کارت (16 رقم)</label>
                    <input type="text" name="card_number" id="card_number" class="form-control" dir="ltr" inputmode="numeric" maxlength="19"
                           value="{{ old('card_number', $contract->card_number) }}" placeholder="6104 3379 0007 4202">
                    @if($errors->has('card_number'))
                        <span class="input-field-errors">{{ $errors->first('card_number') }}</span>
                    @endif
                </div>
                <div class="form-group">
                    <label for="card_holder">به نام</label>
                    <input type="text" name="card_holder" id="card_holder" class="form-control" maxlength="100"
                           value="{{ old('card_holder', $contract->card_holder) }}" placeholder="نام صاحب کارت">
                    @if($errors->has('card_holder'))
                        <span class="input-field-errors">{{ $errors->first('card_holder') }}</span>
                    @endif
                </div>
                <div class="form-group">
                    <label for="payment_message">پیام برای کاربر (اختیاری)</label>
                    <textarea name="payment_message" id="payment_message" rows="3" class="form-control" maxlength="1000"
                              placeholder="مثلا: پس از واریز، شماره پیگیری را در برنامه وارد کنید.">{{ old('payment_message', $contract->payment_message) }}</textarea>
                    @if($errors->has('payment_message'))
                        <span class="input-field-errors">{{ $errors->first('payment_message') }}</span>
                    @endif
                </div>
                <button type="submit" class="btn btn-primary btn-block waves-effect waves-light">
                    <i class="fa fa-send"></i>
                    {{ $contract->status === EContract::STATUS_AWAITING_PAYMENT ? 'ارسال دوباره مبلغ و شماره کارت' : 'ارسال مبلغ و شماره کارت برای کاربر' }}
                </button>
            </form>
        </div>
    @endif

    @if($canReject)
        <hr>
        <form action="{{ route('rejectEContract', $contract->id) }}" method="post">
            {{ csrf_field() }}
            <input type="hidden" name="_method" value="PUT">
            <div class="form-group">
                <label for="rejection_reason">دلیل رد قرارداد (برای کاربر نمایش داده می شود)</label>
                <textarea name="rejection_reason" id="rejection_reason" rows="3" class="form-control"
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

@if($contract->status === EContract::STATUS_PENDING)
    <script>
        $(function () {
            $('input[name="e_contract_billing"]').on('change', function () {
                var paid = this.value === 'paid';
                $('#e-contract-paid').toggle(paid);
                $('#e-contract-free').toggle(!paid);
            });
        });
    </script>
@endif
