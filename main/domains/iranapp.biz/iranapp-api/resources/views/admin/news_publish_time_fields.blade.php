{{--
    When the news appears in the app: right away, or at a Jalali date and a time on the Tehran clock.
    See App\Support\NewsPublishTime.
--}}
@php
    $publishMonths = \App\Support\NewsPublishTime::MONTHS;
    // An hour from now is a sensible starting point for a scheduled item.
    $publishDefaults = \App\Support\NewsPublishTime::partsOf(\Carbon\Carbon::now()->addHour());
    $publishFirstYear = \App\Support\NewsPublishTime::partsOf(null)['year'];
    $publishMode = old('publish_mode', 'now');
    $publishValue = fn ($field, $default) => (string) old('publish_' . $field, $default);
    $publishFaDigits = fn ($number) => \App\Libraries\jdf::tr_num((string) $number, 'fa');
@endphp

<div class="col-xs-12">
    <div class="form-group">
        <label>زمان انتشار خبر در اپلیکیشن:</label>
        <div>
            <label class="radio-inline">
                <input type="radio" name="publish_mode" value="now" {{ $publishMode === 'scheduled' ? '' : 'checked' }}>
                انتشار فوری
            </label>
            <label class="radio-inline">
                <input type="radio" name="publish_mode" value="scheduled" {{ $publishMode === 'scheduled' ? 'checked' : '' }}>
                انتشار در تاریخ و ساعت مشخص
            </label>
        </div>
    </div>
</div>

<div id="publish_time_fields" style="{{ $publishMode === 'scheduled' ? '' : 'display:none;' }}">
    <div class="col-xs-4 col-md-2">
        <div class="form-group">
            <label for="publish_day">روز:</label>
            <select name="publish_day" id="publish_day" class="form-control">
                @for($d = 1; $d <= 31; $d++)
                    <option value="{{ $d }}" {{ $publishValue('day', $publishDefaults['day']) === (string) $d ? 'selected' : '' }}>{{ $publishFaDigits($d) }}</option>
                @endfor
            </select>
        </div>
    </div>
    <div class="col-xs-4 col-md-2">
        <div class="form-group">
            <label for="publish_month">ماه:</label>
            <select name="publish_month" id="publish_month" class="form-control">
                @foreach($publishMonths as $m => $monthName)
                    <option value="{{ $m }}" {{ $publishValue('month', $publishDefaults['month']) === (string) $m ? 'selected' : '' }}>{{ $monthName }}</option>
                @endforeach
            </select>
        </div>
    </div>
    <div class="col-xs-4 col-md-2">
        <div class="form-group">
            <label for="publish_year">سال:</label>
            <select name="publish_year" id="publish_year" class="form-control">
                @for($y = $publishFirstYear; $y <= $publishFirstYear + \App\Support\NewsPublishTime::YEARS_AHEAD; $y++)
                    <option value="{{ $y }}" {{ $publishValue('year', $publishDefaults['year']) === (string) $y ? 'selected' : '' }}>{{ $publishFaDigits($y) }}</option>
                @endfor
            </select>
        </div>
    </div>
    <div class="col-xs-6 col-md-3">
        <div class="form-group">
            <label for="publish_hour">ساعت:</label>
            <select name="publish_hour" id="publish_hour" class="form-control">
                @for($h = 0; $h <= 23; $h++)
                    <option value="{{ $h }}" {{ $publishValue('hour', $publishDefaults['hour']) === (string) $h ? 'selected' : '' }}>{{ $publishFaDigits(sprintf('%02d', $h)) }}</option>
                @endfor
            </select>
        </div>
    </div>
    <div class="col-xs-6 col-md-3">
        <div class="form-group">
            <label for="publish_minute">دقیقه:</label>
            <select name="publish_minute" id="publish_minute" class="form-control">
                @for($i = 0; $i <= 59; $i++)
                    <option value="{{ $i }}" {{ $publishValue('minute', $publishDefaults['minute']) === (string) $i ? 'selected' : '' }}>{{ $publishFaDigits(sprintf('%02d', $i)) }}</option>
                @endfor
            </select>
        </div>
    </div>
    <div class="col-xs-12">
        <small class="text-muted">
            تاریخ به تقویم شمسی و ساعت به وقت تهران است. خبر تا این زمان در اپلیکیشن نمایش داده نمی شود و
            اگر «ارسال نوتیفیکیشن» انتخاب شده باشد، نوتیفیکیشن هم در همین زمان ارسال می گردد.
        </small>
    </div>
</div>

<script>
    $(function () {
        $('input[name="publish_mode"]').on('change', function () {
            $('#publish_time_fields').toggle($('input[name="publish_mode"]:checked').val() === 'scheduled');
        });
    });
</script>
