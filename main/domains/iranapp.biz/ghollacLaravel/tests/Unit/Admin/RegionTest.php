<?php

namespace Tests\Unit\Admin;

use App\Admin;
use App\City;
use App\Libraries\PaginationTrait;
use App\Region;
use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;

class RegionTest extends TestCase
{
    use DatabaseTransactions, PaginationTrait;
    public $admin;
    private $updateAndStoreeValidationRules ;

    public function setUp()
    {
        parent::setUp();
        $this->admin = factory(Admin::class)->create();
        $this->be($this->admin, 'admin');
        $this->updateAndStoreeValidationRules = [
            'name' => [
                'required' => null,
                'max' => str_random(300)
            ],
        ];
    }

    public function tearDown()
    {
        parent::tearDown();
        $this->admin = null;
        $this->updateAndStoreeValidationRules = null;
    }

    public function testIndex()
    {
        $city = factory(City::class)->create();
        $city->wasRecentlyCreated = false;
        $total = 35;
        factory(Region::class, $total)->create([
            'city_id' => $city->id
        ]);
        $pagesCount = (int)ceil($total / $this->rowsCount1);
        for ($i = 1; $i <= $pagesCount; $i++) {
            $list = $city->region()->orderBy('name', 'asc')->paginate($this->rowsCount1, ['*'], 'page', $i)
                ->withPath(route('showRegionsInAdminPanel', $city->id));
            $this->get('/admin/provinces/cities/' . $city->id . '/regions?page=' . $i)
                ->assertViewIs('admin.regions')
                ->assertViewHas('list', $list)
                ->assertViewHas('city', $city)
                ->assertStatus(200);

        }
    }

    public function testStore()
    {
        $city = factory(City::class)->create();
        $region = factory(Region::class)->create([
            'city_id' => $city->id
        ]);
        $this->post('/admin/provinces/cities/' . $city->id . '/regions' , [
            'name' => $region->name
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testUpdate(){
        $region = factory(Region::class)->create();
        $region2 = factory(Region::class)->create();
        $this->put('/admin/provinces/cities/regions/' . $region->id , [
            'name' => $region2->name
        ])
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }

    public function testStoreValidationRules()
    {
        $city = factory(City::class)->create();
        foreach ($this->updateAndStoreeValidationRules as $fieldName =>  $ruleSet) {
            foreach ($ruleSet as $ruleName => $ruleValue) {
                $this->post('/admin/provinces/cities/' . $city->id . '/regions', [
                    $fieldName => $ruleValue
                ])
                    ->assertStatus(302)
                    ->assertSessionHasErrors($fieldName);
            }
        }
    }

    public function testUpdateStoreValidationRules(){
        $region = factory(Region::class)->create();
        foreach ($this->updateAndStoreeValidationRules as $fieldName =>  $ruleSet) {
            foreach ($ruleSet as $ruleName => $ruleValue) {
                $this->put('/admin/provinces/cities/regions/' . $region->id, [
                    $fieldName => $ruleValue
                ])
                    ->assertStatus(302)
                    ->assertSessionHasErrors($fieldName);
            }
        }
    }

    public function testDestroy(){
        $region2 = factory(Region::class)->create();
        $this->get('/admin/provinces/cities/regions/'. $region2->id .'/delete')
            ->assertStatus(302)
            ->assertSessionHas('success_msg');
    }
}
