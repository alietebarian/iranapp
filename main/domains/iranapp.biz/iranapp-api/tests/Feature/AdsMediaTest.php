<?php

namespace Tests\Feature;

use App\Models\Admin;
use App\Models\Ads;
use Carbon\Carbon;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Http\UploadedFile;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\File;
use Illuminate\Support\Facades\Hash;
use Illuminate\Support\Facades\URL;
use Tests\TestCase;

/**
 * The admin media page of a main ad (photos plus the per-ad video) and the video_url the
 * app reads. Uploads go to a throwaway public path so the real public/ is never written to.
 */
class AdsMediaTest extends TestCase
{
    use RefreshDatabase;

    private string $publicPath;

    /** @var string[] */
    private array $tempFiles = [];

    protected function setUp(): void
    {
        parent::setUp();

        $this->publicPath = sys_get_temp_dir() . DIRECTORY_SEPARATOR . 'iranapp-media-' . uniqid();
        File::makeDirectory($this->publicPath . '/ads_photo', 0755, true);
        $this->app->usePublicPath($this->publicPath);

        // Every photo upload stamps this watermark, so it has to exist.
        imagepng(imagecreatetruecolor(40, 10), $this->publicPath . '/ads_photo/watermark2.png');
    }

    protected function tearDown(): void
    {
        File::deleteDirectory($this->publicPath);
        File::delete($this->tempFiles);

        parent::tearDown();
    }

    private function actingAsAdmin(): self
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        return $this->actingAs($admin, 'admin');
    }

    private function makeAd(): Ads
    {
        $provinceId = DB::table('province')->insertGetId(['name' => 'اصفهان']);
        $cityId = DB::table('city')->insertGetId(['province_id' => $provinceId, 'name' => 'اصفهان']);
        $categoryId = DB::table('category')->insertGetId(['name' => 'خدمات']);
        $subCategoryId = DB::table('sub_category')->insertGetId(['category_id' => $categoryId, 'name' => 'تعمیرات']);
        $planId = DB::table('ads_plan')->insertGetId([
            'ordering_factor' => 1,
            'max_number_of_photos' => 3,
            'price' => 0,
            'plan_title' => 'پایه',
            'num_of_updates' => 1,
            'interval_days' => 30,
        ]);

        $ad = new Ads();
        $ad->title = 'آگهی تست';
        $ad->city_id = $cityId;
        $ad->type = 'need';
        $ad->sub_category_id = $subCategoryId;
        $ad->status = 'approved';
        $ad->ads_plan_id = $planId;
        $ad->valid_since = Carbon::now()->subDay()->toDateString();
        $ad->valid_until = Carbon::now()->addDays(30)->toDateString();
        $ad->save();

        return $ad;
    }

    /**
     * A real uploaded file, whose type is sniffed from its bytes the way a live upload's is.
     * UploadedFile::fake() would just trust the extension in the name.
     */
    private function upload(string $name, string $content): UploadedFile
    {
        $path = tempnam(sys_get_temp_dir(), 'upl');
        file_put_contents($path, $content);
        $this->tempFiles[] = $path;

        return new UploadedFile($path, $name, null, null, true);
    }

    /** Just enough of an ISO-BMFF header for content sniffing to call it video/mp4. */
    private function mp4(string $name = 'clip.mp4'): UploadedFile
    {
        $ftyp = pack('N', 32) . 'ftypisom' . pack('N', 512) . 'isomiso2avc1mp41';

        return $this->upload($name, $ftyp . pack('N', 8) . 'free');
    }

    private function imageBytes(callable $encode): string
    {
        ob_start();
        $encode(imagecreatetruecolor(600, 400));

        return ob_get_clean();
    }

    private function videoPath(Ads $ad): string
    {
        return $this->publicPath . '/ads_video/' . $ad->video;
    }

    /** These pages referenced pre-port App\X classes and crashed as soon as an ad existed. */
    public function test_admin_ad_pages_render_with_an_ad_present(): void
    {
        $ad = $this->makeAd();
        $this->actingAsAdmin();

        $this->get(route('showAdsListInAdminPanel'))->assertOk()->assertSee('تصاویر و ویدیو آگهی');
        $this->get(route('showAdsUpdatePageInAdminPanel', $ad->id))->assertOk()->assertSee('افزودن ویدیو');
        $this->get(route('showAdsPhotoById', $ad->id))->assertOk()->assertSee('ویدیو آگهی');
    }

    public function test_uploaded_video_is_stored_and_returned_to_the_app(): void
    {
        $ad = $this->makeAd();

        $this->actingAsAdmin()
            ->post(route('uploadAdsVideoInAdminPanel', $ad->id), ['video' => $this->mp4()])
            ->assertSessionHasNoErrors()
            ->assertRedirect(route('showAdsPhotoById', $ad->id));

        $ad->refresh();
        $this->assertStringEndsWith('.mp4', $ad->video);
        $this->assertFileExists($this->videoPath($ad));

        $videoUrl = URL::to('/ads_video/' . $ad->video);
        $this->getJson('/api/ads/id/' . $ad->id)->assertOk()->assertJsonPath('ad.video_url', $videoUrl);
        $this->getJson('/api/ads/latest?city_id=' . $ad->city_id . '&offset=0&limit=10')
            ->assertOk()
            ->assertJsonPath('list.0.video_url', $videoUrl);
    }

    public function test_ad_without_video_reports_a_null_video_url(): void
    {
        $ad = $this->makeAd();

        $this->getJson('/api/ads/id/' . $ad->id)->assertOk()->assertJsonPath('ad.video_url', null);
    }

    public function test_a_renamed_non_video_is_rejected(): void
    {
        $ad = $this->makeAd();

        $this->actingAsAdmin()
            ->from(route('showAdsPhotoById', $ad->id))
            ->post(route('uploadAdsVideoInAdminPanel', $ad->id), [
                'video' => $this->upload('clip.mp4', 'not really a video'),
            ])
            ->assertSessionHasErrors('video');

        $this->assertNull($ad->fresh()->video);
    }

    public function test_replacing_or_deleting_the_video_removes_the_old_file(): void
    {
        $ad = $this->makeAd();
        $this->actingAsAdmin();

        $this->post(route('uploadAdsVideoInAdminPanel', $ad->id), ['video' => $this->mp4()]);
        $first = $this->videoPath($ad->refresh());

        $this->post(route('uploadAdsVideoInAdminPanel', $ad->id), ['video' => $this->mp4('second.mp4')]);
        $second = $this->videoPath($ad->refresh());
        $this->assertNotSame($first, $second);
        $this->assertFileDoesNotExist($first);
        $this->assertFileExists($second);

        $this->get(route('deleteAdsVideoInAdminPanel', $ad->id))->assertRedirect();
        $this->assertNull($ad->refresh()->video);
        $this->assertFileDoesNotExist($second);
    }

    public function test_deleting_the_ad_removes_its_video_file(): void
    {
        $ad = $this->makeAd();
        $this->actingAsAdmin()->post(route('uploadAdsVideoInAdminPanel', $ad->id), ['video' => $this->mp4()]);
        $path = $this->videoPath($ad->refresh());

        $this->get(route('deleteAdsByIdInAdminPanel', $ad->id));

        $this->assertFileDoesNotExist($path);
    }

    public function test_common_photo_formats_are_accepted_by_their_content(): void
    {
        $ad = $this->makeAd();

        $this->actingAsAdmin()
            ->post(route('uploadPhotoInAdminPanel', $ad->id), ['photo' => [
                // A JPEG with the '.jfif' extension browsers often give saved images.
                $this->upload('download.jfif', $this->imageBytes('imagejpeg')),
                $this->upload('photo.webp', $this->imageBytes('imagewebp')),
                // A PNG with no extension at all.
                $this->upload('PHOTO', $this->imageBytes('imagepng')),
            ]])
            ->assertSessionHasNoErrors()
            ->assertRedirect(route('showAdsListInAdminPanel'));

        $files = DB::table('ads_photo')->where('ads_id', $ad->id)->pluck('file_name')->all();
        $this->assertEqualsCanonicalizing(
            ['jpg', 'webp', 'png'],
            array_map(fn ($file) => pathinfo($file, PATHINFO_EXTENSION), $files)
        );
        foreach ($files as $file) {
            $this->assertFileExists($this->publicPath . '/ads_photo/' . $file);
        }
    }

    public function test_unsupported_photo_is_rejected_instead_of_crashing(): void
    {
        $ad = $this->makeAd();

        $this->actingAsAdmin()
            ->from(route('showAdsPhotoById', $ad->id))
            ->post(route('uploadPhotoInAdminPanel', $ad->id), ['photo' => [
                $this->upload('photo.jpg', 'not an image GD can read'),
            ]])
            ->assertSessionHasErrors('photo.0');

        $this->assertSame(0, DB::table('ads_photo')->where('ads_id', $ad->id)->count());
    }

    public function test_photos_beyond_the_plan_limit_are_not_stored(): void
    {
        $ad = $this->makeAd();

        $this->actingAsAdmin()->post(route('uploadPhotoInAdminPanel', $ad->id), [
            'photo' => array_map(
                fn ($i) => $this->upload("photo$i.jpg", $this->imageBytes('imagejpeg')),
                range(1, 4)
            ),
        ]);

        $this->assertSame(3, DB::table('ads_photo')->where('ads_id', $ad->id)->count());
    }
}
