@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                @if(count($errors->all()) > 0 )
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="alert alert-danger text-center">
                                @foreach($errors->all() as $error)
                                    {{ $error }} <br>
                                @endforeach
                            </div>
                        </div>
                    </div>
                @endif
                <div class="row">
                    <div class="col-sm-12">
                        <div class="card-box">
                            <h1 style="font-size:16px;">افزودن پلن برای آگهی ها</h1>
                            <form method="post" action="{{ route('saveAdPlan') }}" class="form-horizontal" role="form">
                                {{ csrf_field() }}
                                <div class="row">
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group">
                                            <label for="plan-title-in-add-new" class="col-md-2 control-label">عنوان
                                                پلن</label>
                                            <div class="col-md-10">
                                                <input type="text" name="name" value="{{ old('name') }}"
                                                       class="form-control"
                                                       id="plan-title-in-add-new">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group">
                                            <label for="num-of-stars-in-add-new" class="col-md-2 control-label">تعداد
                                                ستاره</label>
                                            <div class="col-md-10">
                                                <input type="number" value="{{ old('stars') }}" name="stars"
                                                       class="form-control"
                                                       id="num-of-stars-in-add-new">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group">
                                            <label for="max-num-of-photos-in-add-new" class="col-md-2 control-label">حداکثر
                                                تعداد عکس برای آگهی</label>
                                            <div class="col-md-10">
                                                <input type="number" class="form-control"
                                                       name="max_photo" value="{{ old('max_photo') }}"
                                                       id="max-num-of-photos-in-add-new">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group">
                                            <label for="price-in-add-new" class="col-md-2 control-label">قیمت
                                                خرید</label>
                                            <div class="col-md-10">
                                                <input type="number" name="price" class="form-control"
                                                       value="{{ old('price') }}"
                                                       id="price-in-add-new">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group">
                                            <label for="updates-count-in-add-new" class="col-md-2 control-label">حداکثر
                                                به روز رسانی های آگهی</label>
                                            <div class="col-md-10">
                                                <input type="number" name="updates_count" class="form-control"
                                                       value="{{ old('updates_count') }}"
                                                       id="updates-count-in-add-new">
                                            </div>
                                        </div>
                                    </div>
                                    <div class="col-xs-12 col-md-6">
                                        <div class="form-group">
                                            <label for="ads-valid-days-in-add-new" class="col-md-2 control-label">روز
                                                های اعتبار آگهی</label>
                                            <div class="col-md-10">
                                                <input type="number" name="interval_days" class="form-control"
                                                       value="{{ old('interval_days') }}"
                                                       id="ads-valid-days-in-add-new">
                                            </div>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="form-group">
                                            <button type="submit" class="btn btn-primary">ثبت پلن</button>
                                        </div>
                                    </div>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="row">
                        <div class="col-sm-12">
                            <div class="card-box">
                                <div class="row">
                                    <div class="col-lg-12">
                                        <h4 class="m-t-0 header-title"><b>پلن های آگهی ها</b></h4>
                                        <p class="text-muted font-13">
                                        </p>
                                        <div class="p-20">
                                            <div data-force="30" class="layer block hani_class">
                                                <ul class="table_header">
                                                    <li>#</li>
                                                    <li>عنوان پلن</li>
                                                    <li>تعداد ستاره</li>
                                                    <li>حداکثر تعداد تصویر</li>
                                                    <li>قیمت خرید</li>
                                                    <li>حداکثر دفعات به روز رسانی برای آگهی</li>
                                                    <li>روزهای اعتبار</li>
                                                    <li>ویرایش</li>
                                                </ul>
                                                <form method="get" id="order-form">
                                                    <ul id="foo" class="block__list block__list_words">
                                                        @foreach($plans as $plan)
                                                            <li>
                                                                <input type="hidden" data-order name="order[]"
                                                                       value="{{ $plan->ordering_factor }}">
                                                                <input type="hidden" name="plan_id[]"
                                                                       value="{{ $plan->id }}">
                                                                <div class="cls1">{{ $plan->id }}</div>
                                                                <div class="cls1">{{ $plan->plan_title }}</div>
                                                                <div class="cls1">{{ $plan->num_of_stars }}</div>
                                                                <div class="cls1">{{ $plan->max_number_of_photos }}</div>
                                                                <div class="cls1">{{ $plan->price }} تومان</div>
                                                                <div class="cls1">{{ $plan->num_of_updates }}</div>
                                                                <div class="cls1">{{ $plan->interval_days }}</div>
                                                                <div class="cls1">
                                                                    <a class="btn btn-primary btn-xs" href="" data-target="#update-plan-{{ $plan->id }}-modal" data-toggle="modal">ویرایش</a>
                                                                    <a class="btn btn-danger btn-xs" href="{{ route('deleteAdsPlan' , $plan->id) }}">حذف</a>

                                                                </div>
                                                            </li>
                                                        @endforeach
                                                    </ul>
                                                </form>
                                            </div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div> <!-- container -->

            </div> <!-- content -->
            @include('admin.footer')
        </div>
        <script>
            var foo = document.getElementById("foo");
            var fooJq = $('#foo');
            Sortable.create(foo, {
                group: "omega",
                onUpdate: function (evt) {
                    var index = 0;
                    fooJq.find('li').each(function () {
                        console.log(index);
                        var $this = $(this);
                        $this.find('input[data-order]').val(index);
                        index += 1;
                    });
                    $.ajax({
                        type: 'get',
                        url: '{{ route('updateAdsPlansOrderingFactor') }}',
                        data: $('#order-form').serialize(),
                        success: function (response) {

                        }
                    });
                }
            });

        </script>

        @foreach($plans as $plan)
            <div id="update-plan-{{ $plan->id }}-modal" class="modal fade" tabindex="-1" role="dialog" aria-labelledby="myModalLabel"
                 aria-hidden="true" style="display: none;">
                <div class="modal-dialog">
                    <div class="modal-content">
                        <div class="modal-header">
                            <button type="button" class="close" data-dismiss="modal" aria-hidden="true">×</button>
                            <h4 class="modal-title"> ویرایش پلن  </h4>
                        </div>
                        <form action="{{ route('updateAdsPlanInAdminPanel' , $plan->id) }}" method="post" >
                            {{ csrf_field() }}
                            <input type="hidden" name="_method" value="PUT">
                            <div class="modal-body">
                                <div class="row">
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="plan-{{ $plan->id }}-title-in-update-modal" class="control-label">عنوان پلن</label>
                                            <input name="name" value="{{ $plan->plan_title }}" type="text" class="form-control" id="plan-{{ $plan->id }}-title-in-update-modal">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="plan-{{ $plan->id }}-stars-in-update-modal" class="control-label">تعداد ستاره</label>
                                            <input name="stars" value="{{ $plan->num_of_stars }}" type="text" class="form-control" id="plan-{{ $plan->id }}-stars-in-update-modal">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="plan-{{ $plan->id }}-max-photos-in-update-modal" class="control-label">حداکثر تعداد عکس آگهی</label>
                                            <input name="max_photo" value="{{ $plan->max_number_of_photos }}" type="text" class="form-control" id="plan-{{ $plan->id }}-max-photos-in-update-modal">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="plan-{{ $plan->id }}-price-in-update-modal" class="control-label">قیمت خرید</label>
                                            <input name="price" value="{{ $plan->price }}" type="text" class="form-control" id="plan-{{ $plan->id }}-price-in-update-modal">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="plan-{{ $plan->id }}-interval-days-in-update-modal" class="control-label">روزهای اعتبار آگهی</label>
                                            <input name="interval_days" value="{{ $plan->interval_days }}" type="text" class="form-control" id="plan-{{ $plan->id }}-interval-days-in-update-modal">
                                        </div>
                                    </div>
                                    <div class="col-md-6">
                                        <div class="form-group">
                                            <label for="plan-{{ $plan->id }}-updates-count-in-update-modal" class="control-label">حداکثر تعداد به روز رسانی</label>
                                            <input name="updates_count" value="{{ $plan->interval_days }}" type="text" class="form-control" id="plan-{{ $plan->id }}-updates-count-in-update-modal">
                                        </div>
                                    </div>

                                </div>
                            </div>
                            <div class="modal-footer">
                                <button type="button" class="btn btn-default waves-effect" data-dismiss="modal">بستن
                                </button>
                                <button type="submit" class="btn btn-info waves-effect waves-light">ذخیره تغیرات</button>
                            </div>
                        </form>
                    </div>
                </div>
            </div>
    @endforeach

@endsection