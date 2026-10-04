@extends('admin.master')
@section('content')
    <div class="content-page">
        <div class="content">
            <div class="container">

                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">دفترچه تلفن — آپلود فایل اکسل</h1>
                    <p class="text-muted" style="margin-bottom: 15px;">
                        فایل باید سه ستون «نام و نام خانوادگی»، «شماره تلفن» و «صنف» داشته باشد. اگر ردیف اول عنوان ستون ها باشد،
                        ترتیب ستون ها مهم نیست؛ بدون ردیف عنوان، ستون ها به همین ترتیب خوانده می شوند.
                        شماره هایی که از قبل در دفترچه تلفن هستند دوباره اضافه نمی شوند.
                    </p>
                    @if($errors->has('file'))
                        <b class="text-danger">{{ $errors->first('file') }}</b>
                    @endif
                    @php($maxUploadMb = round($max_upload_bytes / 1048576, 1))
                    <form action="{{ route('importPhoneBookInAdminPanel') }}" method="post" enctype="multipart/form-data" id="phone-book-form">
                        {{ csrf_field() }}
                        <div class="row">
                            <div class="col-xs-8 col-md-4">
                                <div class="form-group">
                                    <label for="phone-book-file">فایل اکسل (xlsx، xls یا csv — حداکثر {{ $maxUploadMb }} مگابایت)</label>
                                    <input type="file" name="file" id="phone-book-file" accept=".xlsx,.xls,.csv" class="form-control" required>
                                </div>
                            </div>
                            <div class="col-xs-4 col-md-2">
                                <label>&nbsp;</label><br>
                                <button type="submit" class="btn btn-primary">آپلود</button>
                            </div>
                        </div>
                    </form>
                    <script>
                        // A body over PHP's post_max_size is discarded before validation can report
                        // anything, so the size is checked here before a long upload is wasted.
                        document.getElementById('phone-book-form').addEventListener('submit', function (e) {
                            var input = document.getElementById('phone-book-file');
                            if (input.files.length && input.files[0].size > {{ $max_upload_bytes }}) {
                                e.preventDefault();
                                swal('خطا', 'حجم فایل بیشتر از {{ $maxUploadMb }} مگابایت است.', 'error');
                                return;
                            }
                            var button = this.querySelector('button[type=submit]');
                            button.disabled = true;
                            button.innerText = 'در حال آپلود و پردازش...';
                        });
                    </script>
                </div>

                <div class="card-box">
                    <h1 style="font-size:14px;font-weight: bold;" class="text-pink">
                        لیست دفترچه تلفن
                        <span class="badge">{{ number_format($total) }}</span>
                    </h1>

                    <form action="{{ url()->current() }}" method="get">
                        <div class="row">
                            <div class="col-xs-12 col-md-3">
                                <div class="form-group">
                                    <label for="name-in-filter">نام و نام خانوادگی</label>
                                    <input type="text" value="{{ request('name') }}" name="name" id="name-in-filter" class="form-control">
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-3">
                                <div class="form-group">
                                    <label for="phone-in-filter">شماره تلفن</label>
                                    <input type="text" value="{{ request('phone') }}" name="phone" id="phone-in-filter" class="form-control" dir="ltr">
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-3">
                                <div class="form-group">
                                    <label for="guild-in-filter">صنف</label>
                                    <input type="text" value="{{ request('guild') }}" name="guild" id="guild-in-filter" class="form-control" list="phone-book-guilds">
                                    <datalist id="phone-book-guilds">
                                        @foreach($guilds as $guild)
                                            <option value="{{ $guild }}"></option>
                                        @endforeach
                                    </datalist>
                                </div>
                            </div>
                            <div class="col-xs-12 col-md-3">
                                <label>&nbsp;</label><br>
                                <button type="submit" class="btn btn-success">جستجو</button>
                                @if($searching)
                                    <a href="{{ route('showPhoneBookInAdminPanel') }}" class="btn btn-default">حذف فیلتر</a>
                                @endif
                            </div>
                        </div>
                    </form>

                    @if($searching)
                        <p class="text-muted">{{ number_format($list->total()) }} نتیجه یافت شد.</p>
                    @endif

                    <div class="table-responsive">
                        @if(count($list) > 0)
                            <table class="table m-0">
                                <thead>
                                <tr>
                                    <th>#</th>
                                    <th>نام و نام خانوادگی</th>
                                    <th>شماره تلفن</th>
                                    <th>صنف</th>
                                    <th></th>
                                </tr>
                                </thead>
                                <tbody>
                                @foreach($list as $entry)
                                    <tr>
                                        <td>{{ ($list->currentPage() - 1) * $list->perPage() + $loop->iteration }}</td>
                                        <td>{{ $entry->full_name ?? '—' }}</td>
                                        <td dir="ltr" style="text-align: right;">{{ $entry->phone }}</td>
                                        <td>{{ $entry->guild ?? '—' }}</td>
                                        <td>
                                            <form action="{{ route('deletePhoneBookEntryInAdminPanel', $entry->id) }}" method="post"
                                                  onsubmit="return confirm('این شماره از دفترچه تلفن حذف شود؟');">
                                                {{ csrf_field() }}
                                                {{ method_field('DELETE') }}
                                                <button type="submit" class="btn btn-danger btn-xs">حذف</button>
                                            </form>
                                        </td>
                                    </tr>
                                @endforeach
                                </tbody>
                            </table>
                        @else
                            <div class="alert alert-success text-center">
                                {{ $searching ? 'نتیجه ای یافت نشد!' : 'دفترچه تلفن خالی است. یک فایل اکسل آپلود کنید.' }}
                            </div>
                        @endif
                    </div>
                    {{ $list->withQueryString()->links() }}

                    @if($total > 0)
                        <form action="{{ route('clearPhoneBookInAdminPanel') }}" method="post" style="margin-top: 15px;"
                              onsubmit="return confirm('همه {{ number_format($total) }} شماره دفترچه تلفن حذف شوند؟ این کار قابل بازگشت نیست.');">
                            {{ csrf_field() }}
                            {{ method_field('DELETE') }}
                            <button type="submit" class="btn btn-default btn-xs text-danger">حذف همه شماره ها</button>
                        </form>
                    @endif
                </div>

            </div>
        </div>
    </div>
@endsection
