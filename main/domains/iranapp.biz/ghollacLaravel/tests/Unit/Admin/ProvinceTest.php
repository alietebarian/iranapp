<?php

namespace Tests\Unit\Admin;

use App\Admin;
use App\Libraries\PaginationTrait;
use App\Province;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\Session;
use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;

class ProvinceTest extends TestCase
{
    use DatabaseTransactions, PaginationTrait;
    public $admin;

    public function setUp()
    {
        parent::setUp();
        $this->startSession();
        $this->app->make('db')->beginTransaction();
        $this->beforeApplicationDestroyed(function () {
            $this->app->make('db')->rollBack();
        });
        $this->admin = factory(Admin::class)->create();
        $this->be($this->admin , 'admin');
    }

    public function tearDown()
    {
        parent::tearDown();
        $this->admin = null;
    }

    public function testShowAll()
    {
        $provinceCount = Province::count();
        $pagesCount = (int)ceil($provinceCount / $this->rowsCount1);
        for ($i = 1; $i <= $pagesCount; $i++) {
            $provinces = DB::table('province')->orderBy('name', 'asc')->paginate($this->rowsCount1, ['*'], 'page', $i)
                ->withPath(route('showAllProvincesInAdminPanel'));
            $this->get('/admin/provinces?page=' . $i)
                ->assertStatus(200)
                ->assertViewHas('provinces', $provinces);
        }
    }

//    public function testUpdate()
//    {
//
//    }

    /**
     * @param $baseUrl
     * @param $method
     * @dataProvider updateAndStoreNameValidationDataProvider
     */
    public function testNameIsRequiredInUpdate($baseUrl, $method)
    {
        $province = factory(Province::class)->create();
        if (strtoupper($method) == 'POST') {
            $url = $baseUrl;
            $response = $this->post($url, [
                'name' => null,
                '_token' => csrf_token(),
                '_method' => $method
            ]);
        } else {
            $url = $baseUrl . $province->id;
            $response = $this->put($url, [
                'name' => null,
                '_token' => csrf_token(),
                '_method' => $method
            ]);
        }
        $response
            ->assertStatus(302)
            ->assertSessionHas('error_msg')
            ->assertSessionHas('validation_error', true);
    }

    public function updateAndStoreNameValidationDataProvider()
    {
        return [
            [
                'url' => '/admin/provinces/',
                'method' => 'PUT'
            ],
            [
                'url' => '/admin/provinces/',
                'method' => 'POST'
            ]
        ];
    }


    /**
     * @dataProvider updateAndStoreNameValidationDataProvider
     */
    public function testNameShouldNotGreaterThan100Characters($baseUrl, $method){
        $province = factory(Province::class)->create();

        if (strtoupper($method) == 'POST') {
            $url = $baseUrl;
            $response = $this->post($url, [
                'name' => str_random(200),
                '_token' => csrf_token(),
                '_method' => $method
            ]);
        } else {
            $url = $baseUrl . $province->id;
            $response = $this->put($url, [
                'name' => str_random(200),
                '_token' => csrf_token(),
                '_method' => $method
            ]);
        }
        $response->assertStatus(302)
            ->assertSessionHas('error_msg')
            ->assertSessionHas('validation_error' , true);

    }

    public function testStore(){
        $province = factory(Province::class)->create();
        $this->post('/admin/provinces'  , [
            'name' => $province->name,
            '_token' => csrf_token()
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testUpdate(){
        $province1 = factory(Province::class)->create();
        $province2 = factory(Province::class)->create();
        $this->put('/admin/provinces/' . $province1->id , [
            'name' => $province2->name
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testDestroy(){
        $province = factory(Province::class)->create();
        $queryString = http_build_query([
            '_token' => csrf_token()
        ]);
        $this->get('/admin/province/'. $province->id .'/delete?' . $queryString)
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }
}
