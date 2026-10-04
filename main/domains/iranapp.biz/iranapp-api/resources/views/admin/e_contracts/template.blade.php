@extends('admin.master')
@section('style')
    <style>
        .tpl-section {
            border: 1px solid #e3e3e3;
            border-radius: 6px;
            margin-bottom: 14px;
            padding: 12px 14px;
            background: #fafbfc;
        }

        .tpl-section .tpl-section-head {
            margin-bottom: 8px;
        }

        .tpl-section .tpl-section-number {
            font-weight: bold;
            color: #b71c1c;
        }

        .tpl-section textarea {
            line-height: 2;
            min-height: 110px;
            resize: vertical;
        }

        .tpl-chip {
            display: inline-block;
            margin: 0 0 6px 4px;
            padding: 3px 9px;
            border-radius: 12px;
            border: 1px solid #d0d7de;
            background: #fff;
            cursor: pointer;
            font-size: 12px;
        }

        .tpl-chip:hover {
            border-color: #b71c1c;
        }

        .tpl-chip.used {
            border-color: #5fbeaa;
            background: #eefaf7;
        }

        .tpl-chip.missing {
            border-color: #f05050;
            background: #fdeeee;
        }

        .tpl-chip code {
            background: none;
            color: #555;
            padding: 0;
            direction: ltr;
        }

        .tpl-preview {
            background: #fff;
            border: 1px solid #e3e3e3;
            border-radius: 6px;
            padding: 20px 24px;
            line-height: 2.2;
            text-align: justify;
        }

        .tpl-preview h4 {
            color: #b71c1c;
            font-size: 14px;
            font-weight: bold;
            margin: 14px 0 2px;
        }

        .tpl-preview .tpl-title {
            color: #b71c1c;
            font-size: 17px;
            font-weight: bold;
            text-align: center;
        }

        .tpl-preview .tpl-value {
            color: #b71c1c;
            font-weight: bold;
        }

        .tpl-preview .tpl-unknown {
            background: #fdeeee;
            color: #f05050;
        }

        .tpl-sticky {
            position: sticky;
            top: 80px;
        }
    </style>
@endsection
@section('content')
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="row">
                    <div class="col-sm-12">
                        <h4 class="page-title">ویرایش متن قرارداد الکترونیک</h4>
                        <p class="text-muted page-title-alt">
                            نسخه فعلی: <b>{{ $template['version'] }}</b>
                        </p>
                    </div>
                </div>

                <div class="alert alert-info">
                    <ul class="m-0" style="line-height: 2;">
                        <li>فقط متن ثابت قرارداد را ویرایش می کنید. عبارت هایی مثل <code>{business_name}</code> جای اطلاعاتی است که کاربر وارد می کند؛ می توانید جایشان را در متن عوض کنید، ولی نباید حذف شوند.</li>
                        <li>با هر ذخیره، یک نسخه جدید ساخته می شود. قراردادهایی که قبلا ثبت شده اند با همان متنی که کاربر پذیرفته باقی می مانند.</li>
                        <li>اگر کاربری در حال پر کردن فرم باشد، هنگام ارسال متن جدید به او نشان داده می شود و باید دوباره آن را بپذیرد.</li>
                    </ul>
                </div>

                @if($errors->any())
                    <div class="alert alert-danger">
                        <b>متن ذخیره نشد:</b>
                        <ul class="m-0">
                            @foreach($errors->all() as $error)
                                <li>{{ $error }}</li>
                            @endforeach
                        </ul>
                    </div>
                @endif

                <div class="row">
                    <div class="col-md-8">
                        <form action="{{ route('saveEContractTemplate') }}" method="post" id="tpl-form">
                            {{ csrf_field() }}
                            <input type="hidden" name="_method" value="PUT">

                            <div class="card-box">
                                <div class="form-group m-b-0">
                                    <label for="tpl-title">عنوان قرارداد</label>
                                    <input type="text" name="title" id="tpl-title" class="form-control"
                                           maxlength="200" value="{{ $template['title'] }}">
                                </div>
                            </div>

                            <div class="card-box">
                                <h4 class="text-dark header-title m-t-0">بندهای قرارداد</h4>
                                <div id="tpl-sections">
                                    @foreach($template['sections'] as $section)
                                        @include('admin.e_contracts.template_section', ['heading' => $section['heading'], 'text' => $section['text']])
                                    @endforeach
                                </div>
                                <button type="button" class="btn btn-default btn-sm" id="tpl-add">
                                    <i class="fa fa-plus"></i> افزودن بند
                                </button>
                            </div>

                            <div class="card-box">
                                <div id="tpl-problems" class="alert alert-danger" style="display: none;"></div>
                                <button type="submit" class="btn btn-success waves-effect waves-light" id="tpl-save">
                                    <i class="fa fa-save"></i> ذخیره به عنوان نسخه جدید
                                </button>
                            </div>
                        </form>

                        <div class="card-box">
                            <h4 class="text-dark header-title m-t-0">پیش نمایش</h4>
                            <p class="text-muted"><small>اطلاعاتی که کاربر وارد می کند با رنگ قرمز نمایش داده شده است.</small></p>
                            <div class="tpl-preview" id="tpl-preview"></div>
                        </div>
                    </div>

                    <div class="col-md-4">
                        <div class="tpl-sticky">
                            <div class="card-box">
                                <h4 class="text-dark header-title m-t-0">اطلاعات کاربر</h4>
                                <p class="text-muted"><small>
                                    برای افزودن، اول روی جای مورد نظر در متن کلیک کنید و بعد یکی از موارد زیر را بزنید.
                                    <span style="color: #5fbeaa;">سبز</span>: در متن هست،
                                    <span style="color: #f05050;">قرمز</span>: از متن حذف شده و باید برگردد.
                                </small></p>
                                @foreach($placeholders as $name => $label)
                                    <span class="tpl-chip" data-name="{{ $name }}" title="افزودن به متن">
                                        {{ $label }} <code>{{ '{' . $name . '}' }}</code>
                                    </span>
                                @endforeach
                            </div>

                            <div class="card-box">
                                <h4 class="text-dark header-title m-t-0">نسخه ها</h4>
                                <table class="table table-condensed m-0">
                                    <thead>
                                    <tr>
                                        <th>نسخه</th>
                                        <th>تاریخ</th>
                                        <th>قرارداد ثبت شده</th>
                                        <th></th>
                                    </tr>
                                    </thead>
                                    <tbody>
                                    @forelse($versions as $row)
                                        <tr>
                                            <td>
                                                {{ $row->version }}
                                                @if($loop->first)
                                                    <span class="label label-success">فعلی</span>
                                                @endif
                                            </td>
                                            <td>
                                                {{ \App\Support\JalaliDate::fromTimestamp($row->created_at, 'Y/m/d') }}
                                                @if($row->creator)
                                                    <br><small class="text-muted">{{ $row->creator->first_name }} {{ $row->creator->last_name }}</small>
                                                @endif
                                            </td>
                                            <td>{{ $row->contracts_count }}</td>
                                            <td><a href="{{ route('showEContractTemplateVersion', $row->version) }}">مشاهده</a></td>
                                        </tr>
                                    @empty
                                        <tr><td colspan="4" class="text-muted">نسخه ای ذخیره نشده است.</td></tr>
                                    @endforelse
                                    </tbody>
                                </table>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    {{-- Blank section, cloned by "افزودن بند". --}}
    <template id="tpl-section-template">
        @include('admin.e_contracts.template_section', ['heading' => null, 'text' => ''])
    </template>
@endsection
@section('js')
    <script>
        $(function () {
            var placeholders = @json($placeholders);
            var $sections = $('#tpl-sections');
            var lastTextarea = null;

            function escapeHtml(text) {
                return $('<div>').text(text).html();
            }

            function renumber() {
                $sections.children('.tpl-section').each(function (i) {
                    $(this).find('.tpl-section-number').text('بند ' + (i + 1));
                });
            }

            function autoGrow(textarea) {
                textarea.style.height = 'auto';
                textarea.style.height = (textarea.scrollHeight + 4) + 'px';
            }

            /** Checks the placeholders, updates the chips and the preview, and blocks saving on problems. */
            function refresh() {
                var used = {}, unknown = {};
                var title = $('#tpl-title').val();
                var html = '<p class="tpl-title">' + escapeHtml(title) + '</p>';

                $sections.children('.tpl-section').each(function () {
                    var heading = $(this).find('input[name="headings[]"]').val().trim();
                    var text = $(this).find('textarea[name="texts[]"]').val();
                    if (heading) {
                        html += '<h4>' + escapeHtml(heading) + ':</h4>';
                    }
                    var body = escapeHtml(text).replace(/\{(\w+)\}/g, function (match, name) {
                        if (placeholders[name]) {
                            used[name] = true;
                            return '<span class="tpl-value">«' + escapeHtml(placeholders[name]) + '»</span>';
                        }
                        unknown[name] = true;
                        return '<span class="tpl-unknown">' + match + '</span>';
                    });
                    html += '<p>' + body.replace(/\n/g, '<br>') + '</p>';
                });
                $('#tpl-preview').html(html);

                var problems = [];
                if (!title.trim()) {
                    problems.push('عنوان قرارداد خالی است.');
                }
                $('.tpl-chip').each(function () {
                    var name = $(this).data('name');
                    $(this).toggleClass('used', !!used[name]).toggleClass('missing', !used[name]);
                    if (!used[name]) {
                        problems.push('«' + placeholders[name] + '» ({' + name + '}) در متن نیست.');
                    }
                });
                $.each(unknown, function (name) {
                    problems.push('عبارت {' + name + '} شناخته شده نیست.');
                });

                if (problems.length) {
                    $('#tpl-problems').html('<b>قبل از ذخیره این موارد را برطرف کنید:</b><ul class="m-0">' +
                        problems.map(function (p) { return '<li>' + escapeHtml(p) + '</li>'; }).join('') + '</ul>').show();
                } else {
                    $('#tpl-problems').hide();
                }
                $('#tpl-save').prop('disabled', problems.length > 0);
            }

            $sections.on('focus click keyup', 'textarea', function () {
                lastTextarea = this;
            });
            $sections.on('input', 'textarea', function () {
                autoGrow(this);
            });
            $(document).on('input', '#tpl-form input, #tpl-form textarea', refresh);

            $sections.on('click', '.tpl-up', function () {
                var $s = $(this).closest('.tpl-section');
                $s.prev('.tpl-section').before($s);
                renumber();
                refresh();
            });
            $sections.on('click', '.tpl-down', function () {
                var $s = $(this).closest('.tpl-section');
                $s.next('.tpl-section').after($s);
                renumber();
                refresh();
            });
            $sections.on('click', '.tpl-remove', function () {
                if (!confirm('این بند حذف شود؟')) {
                    return;
                }
                $(this).closest('.tpl-section').remove();
                renumber();
                refresh();
            });

            $('#tpl-add').on('click', function () {
                var $s = $($('#tpl-section-template').html());
                $sections.append($s);
                renumber();
                refresh();
                $s.find('textarea').focus();
            });

            $('.tpl-chip').on('click', function () {
                var textarea = lastTextarea || $sections.find('textarea').get(0);
                if (!textarea) {
                    return;
                }
                var token = '{' + $(this).data('name') + '}';
                var start = textarea.selectionStart, end = textarea.selectionEnd;
                textarea.value = textarea.value.slice(0, start) + token + textarea.value.slice(end);
                textarea.focus();
                textarea.selectionStart = textarea.selectionEnd = start + token.length;
                autoGrow(textarea);
                refresh();
            });

            $('#tpl-form').on('submit', function () {
                return confirm('متن جدید به عنوان یک نسخه تازه ذخیره می شود و از این به بعد برای قراردادهای جدید استفاده می شود. ادامه می دهید؟');
            });

            $sections.find('textarea').each(function () {
                autoGrow(this);
            });
            renumber();
            refresh();
        });
    </script>
@endsection
