<?php

namespace Tests\Unit\Admin;

use App\Admin;
use App\Http\Controllers\NewsController;
use App\Libraries\PaginationTrait;
use App\News;
use App\NewsPhoto;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;

class NewsTest extends TestCase
{
    use DatabaseTransactions, PaginationTrait;
    public $authUser;
    private $updateAndStoreValidationRules;

    public function setUp()
    {
        parent::setUp();
        $this->authUser = factory(Admin::class)->create();
        $this->be($this->authUser, 'admin');
        $this->updateAndStoreValidationRules = [
            'title' => [
                'required' => null,
                'max' => str_random(500),
            ],
            'news_text' => [
                'required' => null,
            ]
        ];
        $this->app->make('db')->beginTransaction();
        $this->beforeApplicationDestroyed(function () {
            $this->app->make('db')->rollBack();
        });
        $this->startSession();
    }

    public function tearDown()
    {
        parent::tearDown();
        $this->authUser = null;
        $this->updateAndStoreValidationRules = null;
    }

    public function testIndex()
    {
        factory(News::class, 100)->create();
        $news = DB::table('news')->orderBy('created_at', 'desc')->get();
        $this->get('/admin/news')
            ->assertStatus(200)
            ->assertViewHas('news', $news)
            ->assertViewIs('admin.news_list');
    }

    public function testEdit()
    {
        $news = factory(News::class)->create();
        $news->wasRecentlyCreated = false;
        $this->get('/admin/news/' . $news->id . '/update')
            ->assertStatus(200)
            ->assertViewIs('admin.news_update')
            ->assertViewHas('news', $news);
    }

    public function testUpdate()
    {
        $this->withoutEvents();
        $news1 = factory(News::class)->create();
        $news2 = factory(News::class)->create();
        $this->put('/admin/news/' . $news1->id . '/update', [
            '_token' => csrf_token(),
            '_method' => 'PUT',
            'title' => $news2->title,
            'news_text' => $news2->passage
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testCreate(){
        $this->get('/admin/news/create')
            ->assertViewIs('admin.add_news');
    }

    public function testStore(){
        $this->withoutEvents();
        $news = factory(News::class)->create();
        $this->post('/admin/news/create' , [
            'title' => $news->title,
            'news_text' => $news->passage,
            'send_notification' => 1
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testDestroy(){
//       If cascading delete is enable is news_photo table of database, you should uncomment these lines
        /*$news = factory(News::class)->create();

        $photos = factory(NewsPhoto::class , 10)->create([
            'news_id' => $news->id
        ]);
        $this->get('/admin/news/'. $news->id .'/delete')
            ->assertStatus(302)
            ->assertSessionHas('error_msg');*/

        $news2 = factory(News::class)->create();
        $this->get('/admin/news/'. $news2->id .'/delete')
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testStoreValidationRules(){
        foreach ($this->updateAndStoreValidationRules as $fieldName => $fieldsArr) {
            foreach ($fieldsArr as $rules) {
                $this->post('/admin/news/create' , [
                    $fieldName => $rules
                ])
                    ->assertStatus(302)
                    ->assertSessionHasErrors($fieldName);
            }
        }
    }

    public function testUpdateValidationRules()
    {
        $news = factory(News::class)->create();
        foreach ($this->updateAndStoreValidationRules as $fieldName => $fieldsArr) {
            foreach ($fieldsArr as $rules) {
                $this->put('/admin/news/' . $news->id . '/update', [
                    $fieldName => $rules
                ])
                    ->assertStatus(302)
                    ->assertSessionHasErrors($fieldName);
            }
        }
    }
}
