<?php

namespace Tests\Unit\Admin;

use App\Admin;
use App\City;
use App\Http\Controllers\CityController;
use App\Libraries\PaginationTrait;
use App\Province;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;

class CityTest extends TestCase
{
    use DatabaseTransactions, PaginationTrait;
    public $authUser;

    public function setUp()
    {
        parent::setUp();
        $this->authUser = factory(Admin::class)->create();
        $this->be($this->authUser, 'admin');
        $this->startSession();
        $this->app->make('db')->beginTransaction();
        $this->beforeApplicationDestroyed(function () {
            $this->app->make('db')->rollBack();
        });
    }

    public function tearDown()
    {
        parent::tearDown();
        $this->authUser = null;
    }

    public function testIndex()
    {
        $province = factory(Province::class)->create();
        $province->wasRecentlyCreated = false;
        factory(City::class, 10)->create([
            'province_id' => $province->id
        ]);
        $citiesCount = $province->city()->count();
        $pagesCount = (int)ceil($citiesCount / $this->rowsCount1);
        for ($page = 1; $page <= $pagesCount; $page++) {
            $response = $this->get('/admin/provinces/' . $province->id . '/cities?page=' . $page);
            $cities = DB::table('city')->where('province_id', $province->id)->orderBy('name', 'asc')
                ->paginate($this->rowsCount1, ['*'], 'page', $page)
                ->withPath(route('showAllCitiesInAdminPanel', $province->id));
            $response
                ->assertViewHas('cities', $cities)
                ->assertViewHas('province', $province)
                ->assertViewIs('admin.cities')
                ->assertStatus(200);
        }

    }

    public function testStore()
    {
        $province = factory(Province::class)->create();
        $city = factory(City::class)->create([
            'province_id' => $province->id
        ]);
        $this->post('/admin/provinces/' . $province->id . '/cities' , [
            'name' => $city->name
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testUpdate(){
        $city = factory(City::class)->create();
        $city2 = factory(City::class)->create();
        $this->put('/admin/cities/' . $city->id , [
            'name' => $city2->name
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testUpdateNameValidation(){
        $city = factory(City::class)->create();
        $names = [
            'required' => null,
            'max' => str_random(300)
        ];
        foreach($names as $name){
            $this->put('/admin/cities/' . $city->id , [
                'name' => $name
            ])
                ->assertSessionHas('validation_error' , true)
                ->assertSessionHas('error_msg');
        }
    }
    public function testStoreNameValidation()
    {
        $province = factory(Province::class)->create();
        $province->wasRecentlyCreated = false;
        $names = [
            'required' => null,
            'max' => str_random(300)
        ];
        foreach ($names as $name) {
            $this->post('/admin/provinces/' . $province->id . '/cities', [
                'name' => $name,
                '_token' => csrf_token(),
                '_method' => 'POST'
            ])
                ->assertSessionHas('validation_error' , true)
                ->assertSessionHas('error_msg');
        }
    }

    public function testDestroy(){
        $city = factory(City::class)->create();
        $this->get('/admin/cities/'. $city->id .'/delete')
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }
}
