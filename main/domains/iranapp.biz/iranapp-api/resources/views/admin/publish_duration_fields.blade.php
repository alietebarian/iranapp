{{--
    How long the ad stays visible once it is approved. Expects $ads (the ad being edited).
    One of the two fields must be filled in; see App\Support\AdPublishDuration.
--}}
@php
    $publishDurationPresets = \App\Support\AdPublishDuration::PRESETS;
    $publishDurationMaxDays = \App\Support\AdPublishDuration::MAX_CUSTOM_DAYS;
    // Legacy rows can hold an unparsable date, and that must not break the edit page.
    try {
        $currentValidUntil = \App\Libraries\jdf::jdate(
            'j F Y',
            \Carbon\Carbon::createFromFormat('Y-m-d', $ads->valid_until)->getTimestamp()
        );
    } catch (\Throwable $e) {
        $currentValidUntil = null;
    }
@endphp

<div class="col-xs-12 col-md-6">
    <div class="form-group">
        <label for="publish_duration">مدت انتشار آگهی:
            <b class="input-field-errors">*</b>
        </label>
        <select name="publish_duration" id="publish_duration" class="form-control">
            <option value="" {{ old('publish_duration') ? '' : 'selected' }}>:: انتخاب کنید ::</option>
            @foreach($publishDurationPresets as $publishDurationDays => $publishDurationLabel)
                <option value="{{ $publishDurationDays }}"
                        {{ (string) old('publish_duration') === (string) $publishDurationDays ? 'selected' : '' }}>
                    {{ $publishDurationLabel }}
                </option>
            @endforeach
        </select>
        @if($currentValidUntil)
            <small class="text-muted">انقضای فعلی آگهی: <b>{{ $currentValidUntil }}</b></small>
        @endif
        @if($errors->has('publish_duration'))
            <br><b class="text-danger">{{ $errors->first('publish_duration') }}</b>
        @endif
    </div>
</div>
<div class="col-xs-12 col-md-6">
    <div class="form-group">
        <label for="publish_duration_custom">یا مدت دلخواه (به روز):</label>
        <input type="number" min="1" max="{{ $publishDurationMaxDays }}" step="1"
               value="{{ old('publish_duration_custom') }}" name="publish_duration_custom"
               id="publish_duration_custom" class="form-control" placeholder="مثلاً 45">
        <small class="text-muted">
            اگر این فیلد پر شود، به جای لیست روبرو اعمال می گردد. مدت انتشار از امروز محاسبه می شود.
        </small>
        @if($errors->has('publish_duration_custom'))
            <br><b class="text-danger">{{ $errors->first('publish_duration_custom') }}</b>
        @endif
    </div>
</div>
