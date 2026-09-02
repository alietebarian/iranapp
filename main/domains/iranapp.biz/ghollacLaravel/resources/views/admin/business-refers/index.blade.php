@extends('admin.master')

@section('style')
    <link href="{{ URL::to('/admin') }}/assets/css/daterangepicker.css" rel="stylesheet"/>
    <link href="{{ URL::to('/admin') }}/assets/css/datepicker-theme.css" rel="stylesheet"/>
    <link href="{{ URL::to('/admin') }}/assets/css/select2.min.css" rel="stylesheet" type="text/css"/>
@endsection

@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">فیلتر</h1>
                    <form action="{{ url()->current() }}" method="get">
                        <div class="row">

                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label class="control-label">تاریخ از</label>
                                    <input name="date_from" value="{{ request()->input('date_from') }}" type="text" class="form-control" id="dateRangePicker"/>
                                    <input type="hidden" name="from_date_ts" id="from_date_ts" value="{{ request()->input('from_date_ts') }}">
                                </div>
                            </div>
                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label class="control-label">تاریخ تا</label>
                                    <input value="{{ request()->input('date_to') }}" name="date_to" type="text" class="form-control" id="dateRangePickerEnd"/>
                                    <input value="{{ request()->input('to_date_ts') }}" type="hidden" name="to_date_ts" id="to_date_ts">
                                </div>
                            </div>

                            <div class="col-xs-6 col-md-4">
                                <div class="form-group">
                                    <label for="user_select" class="control-label">انتخاب کاربر</label>
                                    <select name="user_id" id="user_select" class="selectpicker form-control"
                                            data-live-search="true">
                                        <option {{ !request()->has('user_id') ? 'selected' : '' }} value="all">:: انتخاب کنید ::</option>
                                        @foreach($users as $user)
                                            <option {{ request()->input('user_id') == $user->id ? 'selected' : '' }} value="{{ $user->id }}">{{ $user->first_name }} {{ $user->last_name }}</option>
                                        @endforeach
                                    </select>
                                </div>
                                @if($errors->has('user'))
                                    <b class="text-danger">{{ $errors->first('user') }}</b>
                                @endif
                            </div>

                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="ads_title">عنوان آگهی:</label>
                                    <select name="ads_title" id="ads_title" class="form-control">
                                    </select>
                                </div>
                            </div>
                        </div>

                        <div class="row">
                            <div class="col-xs-12">
                                <button type="submit" class="btn btn-success">فیلتر</button>
                            </div>
                        </div>
                    </form>
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">لیست مراجعات به آگهی ها</h1>
                    @if(count($list) > 0 )
                        <div class="table-responsive">
                            {{--@if(count($ads) > 0 )--}}
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>عنوان</th>
                                    <th>تاریخ</th>
                                    <th>نام کاربر</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $item)
                                    <tr>
                                        <td>{{ $item->id }}</td>
                                        <td>
                                            مراجعه به آگهی
                                            {{ $item->ads_title }}
                                        </td>
                                        <td>{{ $item->created_at_fa }}</td>
                                        <td>{{ $item->first_name }} {{ $item->last_name }}</td>
                                        <td>
                                            <a href="{{ route('business.refers.destroy' , $item->id) }}" data-alert-before-delete class="btn btn-xs btn-danger">حذف</a>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>

                        </div>
                        {{$list->appends(\Illuminate\Support\Facades\Input::except('page'))->links()}}
                    @else
                        <div class="alert alert-success text-center">موردی یافت نشد!</div>
                    @endif

                </div>
            </div>
        </div>
    </div>
@endsection

@section('js')
    <script src="{{ URL::to('admin') }}/assets/js/moment.min.js"></script>
    <script src="{{ URL::to('admin') }}/assets/js/moment-jalaali.js"></script>
    <script src="{{ URL::to('admin') }}/assets/js/daterangepicker-fa-ex.js"></script>
    <script src="{{ URL::to('admin') }}/assets/js/select2.full.min.js"></script>
    <script>
        $(document).ready(function () {
            var night;
            var isRtl = true;
            var dateFormat = isRtl ? 'jYYYY/jMM/jDD' : 'YYYY/MM/DD';
            var dateFrom = undefined;
            var dateTo = undefined;
            var $dateRanger = $("#dateRangePicker");

            $dateRanger.daterangepicker({
                clearLabel: 'Clear',
                autoUpdateInput: !!(dateFrom && dateTo),
                autoApply: true,
                opens: isRtl ? 'left' : 'right',
                locale: {
                    separator: ' - ',
                    format: dateFormat
                },
                startDate: dateFrom,
                endDate: dateTo,
                jalaali: isRtl,
                showDropdowns: true
            }).on('apply.daterangepicker', function (ev, picker) {
                $('#from_date_ts').val(picker.startDate.format('X'));
                $('#to_date_ts').val(picker.endDate.format('X'));
                night = picker.endDate.diff(picker.startDate, 'days');
                if (night > 0) {
                    $(this).val(picker.startDate.format(dateFormat));
                    $('#dateRangePickerEnd').val(picker.endDate.format(dateFormat));
                } else {
                    $(this).val('')
                }
            });


            $('.ga-datepicker').daterangepicker({
                clearLabel: 'Clear',
                // autoUpdateInput: !!(dateFrom && dateTo),
                //minDate: moment(),
                autoApply: true,
                opens: 'right',
                singleDatePicker: true,
                showDropdowns: true,
                language: 'en'
            }).on('apply.daterangepicker', function () {
                $('.tooltip').hide();
                $('.date-select').text($(this).val());
            });

            $('.jalali-datepicker').daterangepicker({
                clearLabel: 'Clear',
                autoApply: true,
                opens: 'left',
                singleDatePicker: true,
                showDropdowns: true,
                jalaali: true,
                language: 'fa'
            }).on('apply.daterangepicker', function () {
                $('.tooltip').hide();
                $('.date-select').text($(this).val());
            });

            $(document).on('mouseover', '.daterangepicker .calendar td', function () {
                var gagDate = $(this).attr('data-original-title');
                $('.date-hover').text('');
                $('.date-hover').text(gagDate);

                $('[data-toggle="tooltip"]').tooltip()
            });
            $('#user_select').select2();
        });

        $('#ads_title').select2({
            placeholder: 'عنوان آگهی  را وارد کنید ...',
            minimumInputLength: 3,
            ajax: {
                url: '{{ route('getAdsViaAjax') }}',
                dataType: 'json',
                delay: 250,
                params: {
                    contentType: 'application/ajax ; charset=utf-8'
                },
                processResults: function (data) {
                    return {
                        results: $.map(data, function (item) {
                            return {
                                text: item.title,
                                id: item.id
                            }
                        })
                    };
                },
                cache: true
            }
        });

        $('a[data-alert-before-delete]').click(function(){
            if(!confirm('آیا در مورد حذف این آیتم مطمئن هستید؟')){
                return false;
            }
        });
    </script>
@endsection