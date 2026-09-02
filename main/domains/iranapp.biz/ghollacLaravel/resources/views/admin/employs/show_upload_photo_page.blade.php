@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;">تصاویر آگهی
                        <span class="text-primary">{{ $ad->title }}</span>
                    </h1>
                    <a href="{{ route('showAllEmploysAdsInAdminPanel') }}"><b>بازگشت به لیست آگهی ها</b></a>
                    <div class="row">
                        @foreach($photos as $photo)
                            <div class="col-xs-6 col-md-4" style="position:relative;">
                                <a href="{{ route('deleteEmploysAdsPhoto' , $photo->id) }}"
                                   class="btn btn-danger btn-xs" style="position:absolute;top:25px;left:40px;">حذف</a>
                                <img src="{{ url()->to('/ads_photo') }}/{{ $photo->file_name }}" alt="ads photo"
                                     class="img-responsive img-thumbnail"
                                     width="400"/>
                            </div>
                        @endforeach
                    </div>
                    @if($available_photo_to_upload > 0)
                        <div class="row">
                            <div class="col-xs-12">
                                <h1 style="font-size:14px;">آپلود تصویر جدید
                                </h1>برای این آگهی شما می توانید حداکثر <span class="text-danger">{{ $available_photo_to_upload }}</span> عکس دیگر آپلود کنید.
                            </div>
                            <div class="col-xs-12">
                                <form action="{{ route('uploadEmploysAdsPhotoInAdminPanel' , $ad->id) }}" method="post"
                                      enctype="multipart/form-data">
                                    {{csrf_field()}}
                                    @for($i = 1 ; $i <= $available_photo_to_upload; $i++)
                                        <div class="form-group">
                                            <label for="photo-{{ $i }}-upload">تصویر شماره {{ $i }}</label>
                                            <input type="file" name="photos[]" id="photo-{{ $i }}-upload"
                                                   class="form-control">
                                        </div>
                                    @endfor
                                    <div class="form-group">
                                        <button type="submit" class="btn btn-primary">ارسال</button>
                                    </div>
                                </form>
                            </div>
                        </div>
                    @endif
                </div>
            </div>
        </div>
    </div>
@endsection