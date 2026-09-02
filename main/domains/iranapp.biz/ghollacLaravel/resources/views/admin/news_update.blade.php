@extends('admin.master')
@section('content')
    <div class="content-page">
        <!-- Start content -->
        <div class="content">
            <div class="container">
                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">ویرایش خبر</h1>
                    <form action="{{ route('updateNewsInAdminPanel' , $news->id) }}" method="post">
                        {{ csrf_field() }}
                        <input type="hidden" name="_method" value="PUT">
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="news_title">عنوان خبر:</label>
                                    <input type="text" name="title" id="news_title" value="{{ $news->title }}" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <label for="news_text">متن خبر:</label>
                                    <textarea class="form-control" name="news_text" id="news_text" cols="30" rows="8">{{ $news->passage }}</textarea>
                                </div>
                            </div>
                        </div>
                        <div class="row">
                            <div class="col-xs-12">
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary">ویرایش</button>
                                </div>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
@endsection