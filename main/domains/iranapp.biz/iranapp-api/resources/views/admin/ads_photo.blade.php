@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;">تصاویر و ویدیو آگهی
                        <span class="text-primary">{{ $ad->title }}</span>
                    </h1>
                    <div class="row">
                        @foreach($photos as $photo)
                            <div class="col-xs-6 col-md-4" style="position:relative;">
                                <a href="{{ route('deleteAdsPhotoInAdminPanel' , $photo->id) }}"
                                   class="btn btn-danger btn-xs" style="position:absolute;top:25px;left:40px;">حذف</a>
                                <img src="{{ URL::to('/ads_photo') }}/{{ $photo->file_name }}" alt="ads photo"
                                     class="img-responsive img-thumbnail"
                                     width="400"/>
                            </div>
                        @endforeach
                    </div>
                    @if($available_photo_to_upload > 0)
                        <div class="row">
                            <div class="col-xs-12">
                                <h1 style="font-size:14px;">آپلود تصویر جدید
                                </h1>
                                برای این آگهی شما می توانید حداکثر <span
                                        class="text-danger">{{ $available_photo_to_upload }}</span> عکس دیگر آپلود کنید.
                                <br>
                                <small class="text-muted">فرمت های مجاز: {{ implode('، ' , $photo_formats) }} — حداکثر {{ $photo_max_mb }} مگابایت برای هر تصویر</small>
                            </div>
                            <div class="col-xs-12">
                                @if($errors->has('photo') || $errors->has('photo.*'))
                                    <b class="text-danger">{{ $errors->first('photo') ?: $errors->first('photo.*') }}</b>
                                @endif
                                <form action="{{ route('uploadPhotoInAdminPanel' , $ad->id) }}" method="post"
                                      enctype="multipart/form-data">
                                    {{csrf_field()}}
                                    @for($i = 1 ; $i <= $available_photo_to_upload; $i++)
                                        <div class="form-group">
                                            <label for="photo-{{ $i }}-upload">تصویر شماره {{ $i }}</label>
                                            <input type="file" name="photo[]" id="photo-{{ $i }}-upload"
                                                   accept=".{{ implode(',.' , $photo_formats) }},.jfif"
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
                    <hr>
                    @php($videoMaxMb = round($video_max_bytes / 1048576 , 1))
                    <div class="row">
                        <div class="col-xs-12">
                            <h1 style="font-size:14px;">ویدیو آگهی</h1>
                            @if($video_url)
                                <div style="max-width:480px;">
                                    <video src="{{ $video_url }}" controls preload="metadata"
                                           style="width:100%;background:#000;"></video>
                                    <a href="{{ route('deleteAdsVideoInAdminPanel' , $ad->id) }}"
                                       onclick="return confirm('ویدیو این آگهی حذف شود؟');"
                                       class="btn btn-danger btn-xs">حذف ویدیو</a>
                                </div>
                            @else
                                <p class="text-muted">این آگهی ویدیو ندارد.</p>
                            @endif
                        </div>
                        <div class="col-xs-12" style="margin-top:10px;">
                            <form action="{{ route('uploadAdsVideoInAdminPanel' , $ad->id) }}" method="post"
                                  enctype="multipart/form-data" id="ads-video-form">
                                {{csrf_field()}}
                                <div class="form-group">
                                    <label for="ads-video-upload">{{ $video_url ? 'جایگزینی ویدیو' : 'آپلود ویدیو' }}</label>
                                    <input type="file" name="video" id="ads-video-upload" class="form-control"
                                           accept=".{{ implode(',.' , $video_formats) }}">
                                    <small class="text-muted">فرمت های مجاز: {{ implode('، ' , $video_formats) }} — حداکثر {{ $videoMaxMb }} مگابایت</small>
                                    @if($errors->has('video'))
                                        <br><b class="text-danger">{{ $errors->first('video') }}</b>
                                    @endif
                                </div>
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary">آپلود ویدیو</button>
                                </div>
                            </form>
                            <script>
                                // A body over PHP's post_max_size is discarded before validation can report
                                // anything, so the size is checked here before a long upload is wasted.
                                document.getElementById('ads-video-form').addEventListener('submit', function (e) {
                                    var input = document.getElementById('ads-video-upload');
                                    if (input.files.length && input.files[0].size > {{ $video_max_bytes }}) {
                                        e.preventDefault();
                                        swal('خطا', 'حجم ویدیو بیشتر از {{ $videoMaxMb }} مگابایت است.', 'error');
                                        return;
                                    }
                                    var button = this.querySelector('button[type=submit]');
                                    button.disabled = true;
                                    button.innerText = 'در حال آپلود...';
                                });
                            </script>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
