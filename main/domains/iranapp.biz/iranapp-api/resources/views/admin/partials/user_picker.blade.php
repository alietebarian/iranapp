{{--
    A user field for the admin panel. Users are looked up on /admin/ajax/users as the admin types
    (at least 3 letters), the way the business-ad page already did: putting every user in the page
    ran PHP out of memory (HTTP 500) once there were tens of thousands of them.

    Options, all optional:
      name       field name (default 'user_id'); with multiple, posted as name[]
      id         element id (default 'user')
      multiple   allow several users (default false)
      selected   id or ids shown as chosen; old input wins after a failed validation
      clearable  show a clear button; a cleared field posts nothing (default false)
--}}
@php
    $name = $name ?? 'user_id';
    $id = $id ?? 'user';
    $multiple = $multiple ?? false;
    $clearable = $clearable ?? false;
    $selectedIds = collect(\Illuminate\Support\Arr::wrap(old($name) ?? ($selected ?? [])))
        ->filter(fn ($value) => is_numeric($value))
        ->map(fn ($value) => (int) $value)
        ->unique();
    $pickedUsers = $selectedIds->isEmpty()
        ? collect()
        : \App\Models\User::query()->select('id', 'first_name', 'last_name')->whereIn('id', $selectedIds)->get();
@endphp
<select name="{{ $multiple ? $name.'[]' : $name }}" id="{{ $id }}" class="form-control" style="width: 100%"
        @if($multiple) multiple="multiple" @endif>
    @foreach($pickedUsers as $pickedUser)
        <option value="{{ $pickedUser->id }}" selected>{{ $pickedUser->first_name }} {{ $pickedUser->last_name }}</option>
    @endforeach
</select>
@once
    <script src="{{ URL::to('/admin') }}/assets/plugins/select2/js/select2.min.js"></script>
@endonce
<script>
    $(function () {
        $('#{{ $id }}').select2({
            dir: 'rtl',
            placeholder: 'نام کاربر را جستجو کنید',
            allowClear: {{ $clearable ? 'true' : 'false' }},
            minimumInputLength: 3,
            language: {
                inputTooShort: function (args) {
                    return 'حداقل ' + (args.minimum - args.input.length) + ' حرف دیگر وارد کنید';
                },
                searching: function () {
                    return 'در حال جستجو...';
                },
                noResults: function () {
                    return 'کاربری یافت نشد';
                },
                errorLoading: function () {
                    return 'خطا در دریافت نتایج';
                }
            },
            ajax: {
                url: '{{ url()->to('/admin/ajax/users') }}',
                dataType: 'json',
                delay: 600,
                data: function (params) {
                    return {q: params.term};
                },
                processResults: function (data) {
                    return {
                        results: $.map(data, function (item) {
                            return {text: item.first_name + ' ' + item.last_name, id: item.id};
                        })
                    };
                },
                cache: true
            }
        });
    });
</script>
