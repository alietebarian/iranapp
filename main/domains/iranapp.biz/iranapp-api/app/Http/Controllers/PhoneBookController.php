<?php

namespace App\Http\Controllers;

use App\Imports\PhoneBookImport;
use App\Models\PhoneBookEntry;
use Illuminate\Http\Request;
use Illuminate\Http\UploadedFile;

/**
 * Admin panel: «دفترچه تلفن». Contacts (name, phone, guild) are imported from Excel files, then
 * listed with a search on each of the three fields.
 */
class PhoneBookController extends Controller
{
    /** Kilobytes. PHP's upload_max_filesize / post_max_size can cap this lower — see maxUploadBytes(). */
    const FILE_MAX_KB = 102400;

    /** The largest file the server will actually accept, in bytes. */
    public static function maxUploadBytes(): int
    {
        return min(self::FILE_MAX_KB * 1024, UploadedFile::getMaxFilesize());
    }

    public function index(Request $request)
    {
        $list = PhoneBookEntry::orderBy('id', 'desc');
        if ($request->filled('name')) {
            $list->where('full_name', 'like', '%' . PhoneBookEntry::toPersianLetters(trim($request->input('name'))) . '%');
        }
        if ($request->filled('phone')) {
            $phone = preg_replace('/\D/', '', PhoneBookEntry::toEnglishDigits($request->input('phone')));
            $list->where('phone', 'like', '%' . $phone . '%');
        }
        if ($request->filled('guild')) {
            $list->where('guild', 'like', '%' . PhoneBookEntry::toPersianLetters(trim($request->input('guild'))) . '%');
        }

        return view('admin.phone_book', [
            'list' => $list->paginate(20)->appends($request->only(['name', 'phone', 'guild'])),
            'total' => PhoneBookEntry::count(),
            'guilds' => PhoneBookEntry::whereNotNull('guild')->distinct()->orderBy('guild')->limit(500)->pluck('guild'),
            'searching' => $request->anyFilled(['name', 'phone', 'guild']),
            'max_upload_bytes' => self::maxUploadBytes(),
        ]);
    }

    public function import(Request $request)
    {
        $request->validate([
            'file' => 'required|file|mimes:xlsx,xls,csv,txt|max:' . self::FILE_MAX_KB,
        ], [
            'file.required' => 'فایل اکسل را انتخاب کنید.',
            'file.uploaded' => 'حجم فایل بیشتر از سقف مجاز سرور (' . round(self::maxUploadBytes() / 1048576) . ' مگابایت) است.',
            'file.mimes' => 'فرمت فایل باید xlsx، xls یا csv باشد.',
            'file.max' => 'حجم فایل نباید بیشتر از ' . (self::FILE_MAX_KB / 1024) . ' مگابایت باشد.',
        ]);

        // A 100 MB file holds millions of rows: reading them takes minutes, and the shared strings
        // table of an xlsx file stays in memory while its sheets are read.
        set_time_limit(0);
        ini_set('memory_limit', '1024M');

        $file = $request->file('file');
        $import = new PhoneBookImport();
        try {
            $import->import($file->getRealPath(), $file->getClientOriginalExtension());
        } catch (\Throwable $e) {
            report($e);

            return redirect()->route('showPhoneBookInAdminPanel')
                ->with('error_msg', self::msg('خطا در خواندن فایل', 'فایل قابل خواندن نیست. لطفا یک فایل اکسل سالم (xlsx یا xls) انتخاب کنید.'));
        }

        $text = number_format($import->imported) . ' شماره به دفترچه تلفن اضافه شد.';
        if ($import->duplicates > 0) {
            $text .= ' ' . number_format($import->duplicates) . ' شماره تکراری بود و دوباره اضافه نشد.';
        }
        if ($import->invalid > 0) {
            $text .= ' ' . number_format($import->invalid) . ' ردیف شماره تلفن معتبر نداشت و رد شد.';
        }

        return redirect()->route('showPhoneBookInAdminPanel')
            ->with('success_msg', self::msg('آپلود فایل', $text));
    }

    public function delete(PhoneBookEntry $entry)
    {
        $entry->delete();

        return back()->with('success_msg', self::msg('حذف شماره', 'شماره از دفترچه تلفن حذف شد.'));
    }

    public function clear()
    {
        PhoneBookEntry::query()->delete();

        return redirect()->route('showPhoneBookInAdminPanel')
            ->with('success_msg', self::msg('حذف همه', 'همه شماره های دفترچه تلفن حذف شدند.'));
    }

    /** The {title, msg} object admin.sweet_alert shows. */
    private static function msg(string $title, string $text): \stdClass
    {
        $msg = new \stdClass();
        $msg->title = $title;
        $msg->msg = $text;

        return $msg;
    }
}
