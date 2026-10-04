<?php

namespace App\Http\Controllers;

use App\Models\EContractTemplateVersion;
use App\Support\EContractTemplate;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Auth;
use Illuminate\Support\Facades\DB;

/**
 * Admin panel: متن قرارداد الکترونیک. Edits the fixed wording of the electronic contract; the
 * {placeholders} that carry what users enter cannot be removed. Each save is a new version, so
 * contracts already signed keep the wording they were signed under.
 */
class EContractTemplateController extends Controller
{
    public function edit()
    {
        $current = EContractTemplate::current();
        $old = session()->getOldInput();

        // After a failed save, show what the admin typed rather than the saved wording.
        if (! empty($old['texts'])) {
            $current['title'] = (string) ($old['title'] ?? '');
            $current['sections'] = self::sectionsFromInput($old['headings'] ?? [], $old['texts']);
        }

        return view('admin.e_contracts.template', [
            'template' => $current,
            'placeholders' => EContractTemplate::PLACEHOLDERS,
            'versions' => self::versions(),
        ]);
    }

    public function update(Request $request)
    {
        $title = trim((string) $request->input('title'));
        $sections = self::sectionsFromInput((array) $request->input('headings', []), (array) $request->input('texts', []));

        $problems = EContractTemplate::problems($title, $sections);
        if ($problems) {
            return redirect()->route('showEContractTemplateEditor')->withInput()->withErrors($problems);
        }

        $saved = EContractTemplate::saveNewVersion($title, $sections, Auth::guard('admin')->id());

        $msg = new \stdClass();
        if ($saved) {
            $msg->title = 'ذخیره شد';
            $msg->msg = 'متن قرارداد به نسخه ' . $saved->version . ' به روز شد. قراردادهای جدید با این متن ثبت می شوند.';
        } else {
            $msg->title = 'بدون تغییر';
            $msg->msg = 'متن قرارداد تغییری نکرده بود؛ نسخه جدیدی ساخته نشد.';
        }

        return redirect()->route('showEContractTemplateEditor')->with('success_msg', $msg);
    }

    public function showVersion(string $version)
    {
        $template = EContractTemplate::forVersion($version);
        abort_if(! $template, 404);

        return view('admin.e_contracts.template_version', [
            'template' => $template,
            'row' => EContractTemplateVersion::with('creator')->where('version', $version)->first(),
            'contractsCount' => DB::table('e_contracts')->where('template_version', $version)->count(),
            'isCurrent' => EContractTemplate::current()['version'] === $version,
        ]);
    }

    /**
     * Pairs the heading and text fields of the form into sections, dropping sections the admin
     * left completely empty. Line endings are normalised so an unchanged text compares equal.
     */
    private static function sectionsFromInput(array $headings, array $texts): array
    {
        $sections = [];
        foreach (array_values($texts) as $i => $text) {
            $heading = trim(str_replace("\r\n", "\n", (string) (array_values($headings)[$i] ?? '')));
            $text = trim(str_replace("\r\n", "\n", (string) $text));
            if ($heading === '' && $text === '') {
                continue;
            }
            $sections[] = ['heading' => $heading === '' ? null : $heading, 'text' => $text];
        }

        return $sections;
    }

    /** Saved versions, newest first, with how many contracts were signed under each. */
    private static function versions()
    {
        $counts = DB::table('e_contracts')
            ->select('template_version', DB::raw('count(*) as total'))
            ->groupBy('template_version')
            ->pluck('total', 'template_version');

        return EContractTemplateVersion::with('creator')->orderByDesc('id')->get()
            ->each(fn ($row) => $row->contracts_count = (int) ($counts[$row->version] ?? 0));
    }
}
