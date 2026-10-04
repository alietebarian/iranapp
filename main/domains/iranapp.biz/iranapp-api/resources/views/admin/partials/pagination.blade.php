{{--
    Pagination for every list in the admin panel; the default view for ->links()
    (AppServiceProvider). Right-to-left: "previous" sits on the right, "next" on the left.
    Shows "x to y of n", first/last page, a window around the current page, and a jump-to-page
    box for long lists. Pass the paginator with ->withQueryString() so filters are kept.
--}}
@once
    <style>
        .admin-pagination {
            align-items: center;
            border-top: 1px solid #eef1f5;
            display: flex;
            flex-wrap: wrap;
            gap: 12px;
            justify-content: space-between;
            margin-top: 18px;
            padding-top: 14px;
        }

        .admin-pagination .ap-summary {
            color: #98a6ad;
            font-size: 13px;
        }

        .admin-pagination .ap-summary b {
            color: #4c5667;
        }

        .admin-pagination .ap-pages {
            display: flex;
            flex-wrap: wrap;
            gap: 4px;
            list-style: none;
            margin: 0;
            padding: 0;
        }

        .admin-pagination .ap-pages a,
        .admin-pagination .ap-pages span {
            align-items: center;
            background: #fff;
            border: 1px solid #e3e8ee;
            border-radius: 6px;
            color: #4c5667;
            display: inline-flex;
            font-size: 13px;
            height: 34px;
            justify-content: center;
            min-width: 34px;
            padding: 0 10px;
            text-decoration: none;
            transition: background .15s, border-color .15s, color .15s;
        }

        .admin-pagination .ap-pages a:hover,
        .admin-pagination .ap-pages a:focus {
            background: #f2fbf9;
            border-color: #5fbeaa;
            color: #2d8f7b;
        }

        .admin-pagination .ap-pages .active span {
            background: #5fbeaa;
            border-color: #5fbeaa;
            box-shadow: 0 2px 6px rgba(95, 190, 170, .35);
            color: #fff;
            font-weight: 600;
        }

        .admin-pagination .ap-pages .disabled span {
            background: #f7f8fa;
            color: #c4ccd3;
            cursor: not-allowed;
        }

        .admin-pagination .ap-pages .ap-gap span {
            background: none;
            border-color: transparent;
            min-width: 20px;
            padding: 0;
        }

        .admin-pagination .ap-pages i {
            font-size: 11px;
        }

        .admin-pagination .ap-jump {
            align-items: center;
            color: #98a6ad;
            display: flex;
            font-size: 13px;
            gap: 6px;
            margin: 0;
        }

        .admin-pagination .ap-jump input {
            border: 1px solid #e3e8ee;
            border-radius: 6px;
            height: 34px;
            padding: 0 6px;
            text-align: center;
            width: 64px;
        }

        .admin-pagination .ap-jump button {
            background: #fff;
            border: 1px solid #5fbeaa;
            border-radius: 6px;
            color: #2d8f7b;
            height: 34px;
            padding: 0 12px;
        }

        .admin-pagination .ap-jump button:hover {
            background: #5fbeaa;
            color: #fff;
        }

        @media (max-width: 767px) {
            .admin-pagination {
                justify-content: center;
            }

            .admin-pagination .ap-summary {
                text-align: center;
                width: 100%;
            }

            .admin-pagination .ap-hide-xs {
                display: none !important;
            }
        }
    </style>
@endonce

@php
    $isLengthAware = $paginator instanceof \Illuminate\Contracts\Pagination\LengthAwarePaginator;
    $current = $paginator->currentPage();
    $last = $isLengthAware ? $paginator->lastPage() : null;
    // Page numbers to show: the first, the last and two either side of the current page.
    $pages = [];
    if ($isLengthAware) {
        foreach (range(1, $last) as $page) {
            if ($page === 1 || $page === $last || abs($page - $current) <= 2) {
                $pages[] = $page;
            }
        }
    }
@endphp

@if($isLengthAware ? $paginator->total() > 0 : $paginator->count() > 0)
    <nav class="admin-pagination" aria-label="صفحه بندی">
        <div class="ap-summary">
            @if($isLengthAware)
                نمایش <b>{{ number_format($paginator->firstItem()) }}</b> تا <b>{{ number_format($paginator->lastItem()) }}</b>
                از <b>{{ number_format($paginator->total()) }}</b> مورد
                @if($last > 1)
                    <span class="ap-hide-xs">- صفحه {{ number_format($current) }} از {{ number_format($last) }}</span>
                @endif
            @else
                صفحه {{ number_format($current) }}
            @endif
        </div>

        @if($paginator->hasPages())
            <ul class="ap-pages">
                {{-- First and previous: on the right in RTL. --}}
                @if($isLengthAware && $last > 5)
                    <li class="ap-hide-xs {{ $paginator->onFirstPage() ? 'disabled' : '' }}">
                        @if($paginator->onFirstPage())
                            <span title="اولین صفحه"><i class="fa fa-angle-double-right"></i></span>
                        @else
                            <a href="{{ $paginator->url(1) }}" title="اولین صفحه"><i class="fa fa-angle-double-right"></i></a>
                        @endif
                    </li>
                @endif
                <li class="{{ $paginator->onFirstPage() ? 'disabled' : '' }}">
                    @if($paginator->onFirstPage())
                        <span><i class="fa fa-angle-right"></i>&nbsp;قبلی</span>
                    @else
                        <a href="{{ $paginator->previousPageUrl() }}" rel="prev"><i class="fa fa-angle-right"></i>&nbsp;قبلی</a>
                    @endif
                </li>

                @foreach($pages as $i => $page)
                    @if($i > 0 && $page - $pages[$i - 1] > 1)
                        <li class="ap-gap ap-hide-xs"><span>…</span></li>
                    @endif
                    @if($page === $current)
                        <li class="active" aria-current="page"><span>{{ number_format($page) }}</span></li>
                    @else
                        <li class="{{ abs($page - $current) > 1 && $page !== 1 && $page !== $last ? 'ap-hide-xs' : '' }}">
                            <a href="{{ $paginator->url($page) }}">{{ number_format($page) }}</a>
                        </li>
                    @endif
                @endforeach

                {{-- Next and last: on the left in RTL. --}}
                <li class="{{ $paginator->hasMorePages() ? '' : 'disabled' }}">
                    @if($paginator->hasMorePages())
                        <a href="{{ $paginator->nextPageUrl() }}" rel="next">بعدی&nbsp;<i class="fa fa-angle-left"></i></a>
                    @else
                        <span>بعدی&nbsp;<i class="fa fa-angle-left"></i></span>
                    @endif
                </li>
                @if($isLengthAware && $last > 5)
                    <li class="ap-hide-xs {{ $paginator->hasMorePages() ? '' : 'disabled' }}">
                        @if($paginator->hasMorePages())
                            <a href="{{ $paginator->url($last) }}" title="آخرین صفحه"><i class="fa fa-angle-double-left"></i></a>
                        @else
                            <span title="آخرین صفحه"><i class="fa fa-angle-double-left"></i></span>
                        @endif
                    </li>
                @endif
            </ul>

            @if($isLengthAware && $last > 10)
                {{-- Jump to a page; the other query parameters (filters) are kept. --}}
                <form method="get" action="{{ $paginator->path() }}" class="ap-jump ap-hide-xs">
                    @foreach(request()->except($paginator->getPageName()) as $key => $value)
                        @foreach((array) $value as $nested => $item)
                            @if(! is_array($item))
                                <input type="hidden" name="{{ is_array($value) ? $key . '[' . $nested . ']' : $key }}" value="{{ $item }}">
                            @endif
                        @endforeach
                    @endforeach
                    <label for="ap-jump-{{ $paginator->getPageName() }}" class="m-0">برو به صفحه</label>
                    <input type="number" min="1" max="{{ $last }}" name="{{ $paginator->getPageName() }}"
                           id="ap-jump-{{ $paginator->getPageName() }}" value="{{ $current }}">
                    <button type="submit">برو</button>
                </form>
            @endif
        @endif
    </nav>
@endif
