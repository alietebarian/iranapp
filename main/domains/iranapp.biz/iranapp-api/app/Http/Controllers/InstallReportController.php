<?php

namespace App\Http\Controllers;

use App\Support\InstallStats;
use App\Support\JalaliDate;
use Illuminate\Support\Facades\DB;

/**
 * Admin panel: گزارش نصب ها. The same totals the app shows on its landing page, broken down by
 * install source, by day and by app version.
 */
class InstallReportController extends Controller
{
    public function index()
    {
        $cutover = InstallStats::cutover();
        $daily = InstallStats::daily(30);

        return view('admin.install_report', [
            'summary' => InstallStats::summary(),
            'bySource' => InstallStats::bySource(),
            'daily' => $daily,
            'dailyMax' => max(1, max(array_column($daily, 'count'))),
            'byVersion' => InstallStats::byVersion(),
            'upgrades' => DB::table('app_installs')->where('is_upgrade', true)->count(),
            'cutover' => $cutover ? JalaliDate::fromTimestamp($cutover, 'Y/m/d - H:i') : null,
        ]);
    }
}
