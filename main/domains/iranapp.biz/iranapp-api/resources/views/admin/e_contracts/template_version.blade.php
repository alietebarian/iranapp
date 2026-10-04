@extends('admin.master')
@section('style')
    <style>
        .tpl-preview {
            background: #fff;
            border: 1px solid #e3e3e3;
            border-radius: 6px;
            padding: 24px 30px;
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
    </style>
@endsection
@section('content')
    @php
        $labels = \App\Support\EContractTemplate::PLACEHOLDERS;
        $labelled = \App\Support\EContractTemplate::fillHtml(
            $template['sections'],
            array_map(fn ($label) => '«' . $label . '»', $labels)
        );
    @endphp
    <div class="content-page">
        <div class="content">
            <div class="container">
                <div class="row">
                    <div class="col-xs-12">
                        <a href="{{ route('showEContractTemplateEditor') }}" class="btn btn-default btn-sm m-b-10">بازگشت به ویرایش متن قرارداد</a>
                    </div>
                </div>

                <div class="card-box">
                    <h4 class="text-dark header-title m-t-0">
                        متن قرارداد - نسخه {{ $template['version'] }}
                        @if($isCurrent)
                            <span class="label label-success">نسخه فعلی</span>
                        @endif
                    </h4>
                    <p class="text-muted">
                        @if($row)
                            ذخیره شده در {{ \App\Support\JalaliDate::fromTimestamp($row->created_at, 'Y/m/d - H:i') }}
                            @if($row->creator)
                                توسط {{ $row->creator->first_name }} {{ $row->creator->last_name }}
                            @endif
                            ·
                        @endif
                        {{ $contractsCount }} قرارداد با این متن ثبت شده است.
                    </p>

                    <div class="tpl-preview">
                        <p class="tpl-title">{{ $template['title'] }}</p>
                        @foreach($labelled as $section)
                            @if($section['heading'])
                                <h4>{{ $section['heading'] }}:</h4>
                            @endif
                            <p>{!! $section['html'] !!}</p>
                        @endforeach
                    </div>
                </div>
            </div>
        </div>
    </div>
@endsection
