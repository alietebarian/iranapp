<?php

namespace Tests\Feature;

use App\Models\Admin;
use Illuminate\Foundation\Testing\DatabaseTruncation;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Hash;
use Tests\TestCase;

/**
 * The user picker on the admin "new ad" page. InnoDB only adds rows to a FULLTEXT index when
 * their transaction commits, so this class truncates instead of wrapping each test in a
 * transaction (RefreshDatabase) — otherwise MATCH ... AGAINST would never see the test users.
 */
class AdminUserSearchTest extends TestCase
{
    use DatabaseTruncation;

    private Admin $admin;

    protected function setUp(): void
    {
        parent::setUp();

        $this->admin = new Admin();
        $this->admin->first_name = 'مدیر';
        $this->admin->last_name = 'تست';
        $this->admin->mobile = '09121234567';
        $this->admin->email = 'admin@test.local';
        $this->admin->password = Hash::make('admin12345');
        $this->admin->save();

        DB::table('users')->insert([
            ['first_name' => 'علی', 'last_name' => 'رضایی', 'mobile' => '09121111111', 'password' => 'x', 'created_at' => now(), 'updated_at' => now()],
            ['first_name' => 'مریم', 'last_name' => 'احمدی', 'mobile' => '09352222222', 'password' => 'x', 'created_at' => now(), 'updated_at' => now()],
        ]);
    }

    /**
     * DatabaseTruncation only clears tables before each test, so the committed rows of the last
     * test would otherwise leak into the RefreshDatabase classes that run after this one.
     */
    protected function tearDown(): void
    {
        $this->truncateTablesForAllConnections();

        parent::tearDown();
    }

    private function search(string $q)
    {
        return $this->actingAs($this->admin, 'admin')->getJson('/admin/ajax/users?q=' . urlencode($q));
    }

    public function test_a_partially_typed_name_matches_and_only_display_fields_are_returned(): void
    {
        $this->search('رضا')
            ->assertOk()
            ->assertExactJson([
                ['id' => DB::table('users')->where('mobile', '09121111111')->value('id'), 'first_name' => 'علی', 'last_name' => 'رضایی'],
            ]);
    }

    public function test_every_typed_word_must_match(): void
    {
        $this->search('مریم احم')->assertOk()->assertJsonCount(1)->assertJsonPath('0.first_name', 'مریم');
        $this->search('مریم رضایی')->assertOk()->assertJsonCount(0);
    }

    public function test_sql_and_full_text_operators_in_the_query_are_inert(): void
    {
        $this->search('") OR 1=1 -- ')->assertOk()->assertJsonCount(0);
        $this->search('***')->assertOk()->assertJsonCount(0);
    }

    public function test_queries_shorter_than_three_characters_are_rejected(): void
    {
        $this->search('عل')->assertUnprocessable();
    }

    public function test_search_is_closed_to_guests(): void
    {
        $this->get('/admin/ajax/users?q=' . urlencode('علی'))->assertRedirect();
    }
}
