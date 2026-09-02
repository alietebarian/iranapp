@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ثبت خبر جدید</h1>
                    <form action="{{ route('saveNewsInAdminPanel') }}" method="post">
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="news_title">عنوان خبر:</label>
                                    <input type="text" name="title" id="news_title" value="{{ old('title') }}" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="news_text">متن خبر:</label>
                                    <textarea class="form-control" name="news_text" id="news_text" cols="30" rows="8">{{ old('news_text') }}</textarea>
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="send_notification">
                                        <input checked type="checkbox" name="send_notification" id="send_notification">
                                        ارسال نوتیفیکیشن
                                    </label>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary">ثبت خبر</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection