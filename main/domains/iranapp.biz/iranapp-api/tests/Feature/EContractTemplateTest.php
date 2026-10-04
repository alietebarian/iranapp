<?php

namespace Tests\Feature;

use App\Models\Admin;
use App\Models\EContract;
use App\Models\EContractTemplateVersion;
use App\Models\User;
use App\Support\EContractTemplate;
use App\Support\JalaliDate;
use Illuminate\Foundation\Testing\RefreshDatabase;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * Admins can edit the fixed wording of the electronic contract (not what users enter). Each save
 * is a new version; signed contracts keep the wording they were signed under, and a user who
 * accepted an older wording is asked to read the new one before their submission is stored.
 */
class EContractTemplateTest extends TestCase
{
    use RefreshDatabase;

    protected function setUp(): void
    {
        parent::setUp();
        EContractTemplate::forgetCurrent();
        // AdminUserSearchTest truncates every table, taking the 1.0 row the migration inserted.
        EContractTemplate::ensureDefaultStored();
    }

    private function admin(): Admin
    {
        $admin = new Admin();
        $admin->first_name = 'مدیر';
        $admin->last_name = 'تست';
        $admin->mobile = '09121234567';
        $admin->email = 'admin@test.local';
        $admin->password = Hash::make('admin12345');
        $admin->save();

        return $admin;
    }

    private function user(): User
    {
        return User::create([
            'first_name' => 'تست',
            'last_name' => 'کاربر',
            'mobile' => '09120000000',
            'password' => Hash::make('secret123'),
            'is_mobile_verified' => 1,
            'type' => 'user',
        ]);
    }

    /** The current wording as the editor form posts it, with $change applied to the fields. */
    private function form(?callable $change = null): array
    {
        $template = EContractTemplate::current();
        $form = [
            'title' => $template['title'],
            'headings' => array_map(fn ($s) => (string) $s['heading'], $template['sections']),
            'texts' => array_map(fn ($s) => $s['text'], $template['sections']),
        ];

        return $change ? $change($form) : $form;
    }

    private function save(Admin $admin, array $form)
    {
        EContractTemplate::forgetCurrent();

        return $this->actingAs($admin, 'admin')->put(route('saveEContractTemplate'), $form);
    }

    private function submitContract(User $user, array $extra = [])
    {
        EContractTemplate::forgetCurrent();
        $this->app['auth']->forgetGuards();
        $token = $user->createToken('mobile')->plainTextToken;

        return $this->postJson('/api/e-contracts?token=' . $token, $extra + [
            'business_type' => 'store',
            'business_name' => 'پوشاک آریا',
            'manager_title' => 'mr',
            'manager_name' => 'علی رضایی',
            'national_code' => '0012345679',
            'phone' => '03132123456',
            'mobile' => '09131234567',
            'address' => 'اصفهان، خیابان چهارباغ، پلاک 12',
            'subject' => 'پوشاک مردانه',
            'start_date' => JalaliDate::today(),
            'duration_months' => 12,
            'discount_percent' => 15,
            'terms_accepted' => '1',
        ]);
    }

    public function test_the_migration_stores_the_original_wording_as_version_1(): void
    {
        $this->assertSame(EContractTemplate::defaultTemplate(), EContractTemplate::current());
        $this->assertSame(1, EContractTemplateVersion::count());
    }

    public function test_editor_renders_the_current_wording(): void
    {
        $this->actingAs($this->admin(), 'admin')
            ->get(route('showEContractTemplateEditor'))
            ->assertOk()
            ->assertSee('ویرایش متن قرارداد الکترونیک')
            ->assertSee('موضوع قرارداد')
            ->assertSee('{business_name}', false);
    }

    public function test_editor_is_closed_to_guests(): void
    {
        $this->get(route('showEContractTemplateEditor'))->assertRedirect(route('showAdminLoginForm'));
        $this->put(route('saveEContractTemplate'), $this->form())->assertRedirect(route('showAdminLoginForm'));
        $this->assertSame(1, EContractTemplateVersion::count());
    }

    public function test_saving_an_edit_creates_a_new_version_that_new_contracts_use(): void
    {
        $admin = $this->admin();
        $user = $this->user();

        // A contract signed under 1.0 before the edit.
        $this->submitContract($user)->assertJsonPath('status', 201);
        $old = EContract::sole();

        $this->save($admin, $this->form(function ($form) {
            $form['title'] = 'قرارداد همکاری ایران آپ';
            $form['texts'][2] = 'معرفی مشترکین طرف اول به طرف دوم جهت دریافت {subject} با تخفیف ویژه.';
            $form['headings'][] = 'بند جدید';
            $form['texts'][] = 'این بند به تازگی اضافه شده است.';

            return $form;
        }))->assertRedirect(route('showEContractTemplateEditor'))->assertSessionHas('success_msg');

        EContractTemplate::forgetCurrent();
        $current = EContractTemplate::current();
        $this->assertSame('2.0', $current['version']);
        $this->assertSame('قرارداد همکاری ایران آپ', $current['title']);
        $this->assertSame($admin->id, EContractTemplateVersion::where('version', '2.0')->value('created_by'));

        // The app now gets the new wording…
        $this->app['auth']->forgetGuards();
        $token = $user->createToken('mobile')->plainTextToken;
        $this->getJson('/api/e-contracts/current?token=' . $token)
            ->assertJsonPath('template.version', '2.0')
            ->assertJsonPath('template.title', 'قرارداد همکاری ایران آپ')
            ->assertJsonPath('template.sections.5.text', 'این بند به تازگی اضافه شده است.');

        // …and a new contract is stored with it.
        $this->actingAs($admin, 'admin')->put(route('rejectEContract', $old->id), ['rejection_reason' => 'لطفا دوباره ارسال کنید.']);
        $this->submitContract($user, ['template_version' => '2.0'])->assertJsonPath('status', 201);
        $new = EContract::latest('id')->first();
        $this->assertSame('2.0', $new->template_version);
        $this->assertStringContainsString('قرارداد همکاری ایران آپ', $new->contract_text);
        $this->assertStringContainsString('جهت دریافت پوشاک مردانه با تخفیف ویژه', $new->contract_text);

        // The contract signed before the edit keeps its wording, in the database and in the panel.
        $this->assertSame('1.0', $old->fresh()->template_version);
        $this->assertStringNotContainsString('تخفیف ویژه', $old->fresh()->contract_text);
        $this->actingAs($admin, 'admin')
            ->get(route('showEContractInAdminPanel', $old->id))
            ->assertOk()
            ->assertSee(EContractTemplate::DEFAULT_TITLE)
            ->assertSee('جهت دریافت خدمات/محصولات')
            ->assertDontSee('تخفیف ویژه');
    }

    public function test_removing_what_the_user_enters_is_refused(): void
    {
        $this->save($this->admin(), $this->form(function ($form) {
            // Drop the discount percentage from obligation 8.
            $form['texts'][4] = str_replace('{discount_percent}', 'ده', $form['texts'][4]);

            return $form;
        }))->assertSessionHasErrors();

        $this->assertSame(1, EContractTemplateVersion::count());
        $this->assertStringContainsString('درصد تخفیف', implode(' ', session('errors')->all()));
    }

    public function test_unknown_placeholders_are_refused(): void
    {
        $this->save($this->admin(), $this->form(function ($form) {
            $form['texts'][0] .= ' {user_password}';

            return $form;
        }))->assertSessionHasErrors();

        $this->assertSame(1, EContractTemplateVersion::count());
    }

    public function test_saving_without_changes_does_not_create_a_version(): void
    {
        $this->save($this->admin(), $this->form())->assertSessionHas('success_msg');

        $this->assertSame(1, EContractTemplateVersion::count());
    }

    public function test_a_user_who_accepted_an_older_wording_is_asked_to_read_the_new_one(): void
    {
        $user = $this->user();
        $this->save($this->admin(), $this->form(function ($form) {
            $form['texts'][0] = 'متن تازه مقدمه قرارداد.';

            return $form;
        }));

        $this->submitContract($user, ['template_version' => '1.0'])
            ->assertJsonPath('status', 409)
            ->assertJsonPath('error', 'template_changed')
            ->assertJsonPath('template.version', '2.0')
            ->assertJsonPath('template.sections.0.text', 'متن تازه مقدمه قرارداد.');
        $this->assertSame(0, EContract::count());
    }

    public function test_an_old_version_can_be_viewed(): void
    {
        $admin = $this->admin();
        $this->save($admin, $this->form(fn ($form) => ['title' => 'عنوان تازه'] + $form));

        $this->actingAs($admin, 'admin')
            ->get(route('showEContractTemplateVersion', '1.0'))
            ->assertOk()
            ->assertSee(EContractTemplate::DEFAULT_TITLE)
            ->assertSee('«نام کسب و کار»');

        $this->actingAs($admin, 'admin')->get(route('showEContractTemplateVersion', '9.0'))->assertNotFound();
    }
}
