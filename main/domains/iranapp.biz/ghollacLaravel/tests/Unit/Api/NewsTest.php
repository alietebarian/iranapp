<?php

namespace Tests\Unit\Api;

use App\Libraries\jdf;
use App\News;
use App\NewsPhoto;
use App\User;
use Carbon\Carbon;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\DB;
use Illuminate\Support\Facades\URL;
use Tests\TestCase;
use Illuminate\Foundation\Testing\DatabaseMigrations;
use Illuminate\Foundation\Testing\DatabaseTransactions;
use Tymon\JWTAuth\Facades\JWTAuth;

class NewsTest extends TestCase
{
    use DatabaseTransactions;

    public function setUp()
    {
        parent::setUp();
    }

    public function tearDown()
    {
        parent::tearDown();
    }

    public function generateFakerNews()
    {
        $newsList = factory(News::class, 3)->create()->pluck('id')->toArray();
        $newsList = DB::table('news')->whereIn('id' , $newsList)->get();
        foreach ($newsList as $nIndex => $nRow) {
            $photos = factory(NewsPhoto::class, 2)->create([
                'news_id' => $nRow->id
            ]);
            foreach ($photos as $pIndex => $pRow) {
                $photos[$pIndex]->file_name = URL::to('/news_photo') . '/' . $pRow->file_name;
            }
            $newsList[$nIndex]->created_at = jdf::jdate('j F Y H:i:s' , Carbon::createFromFormat('Y-m-d H:i:s' , $nRow->created_at)->getTimestamp());
            $newsList[$nIndex]->updated_at = jdf::jdate('j F Y H:i:s' , Carbon::createFromFormat('Y-m-d H:i:s' , $nRow->updated_at)->getTimestamp());

        }
        return $newsList;
    }

    public function testIndex()
    {
        $fakeNews = $this->generateFakerNews();
        $newsCount = count($fakeNews);
        $step = 3;
        for ($offset = 1; $offset < $newsCount; $offset += 3) {
            $news = DB::table('news')->orderBy('created_at' , 'desc')->offset($offset)->limit($step)->get();
            foreach ($news as $nIndex => $nRow) {
                $photos = factory(NewsPhoto::class, 2)->create([
                    'news_id' => $nRow->id
                ]);
                foreach ($photos as $pIndex => $pRow) {
                    $photos[$pIndex]->file_name = URL::to('/news_photo') . '/' . $pRow->file_name;
                }
                $news[$nIndex]->created_at = jdf::jdate('j F Y H:i:s' , Carbon::createFromFormat('Y-m-d H:i:s' , $nRow->created_at)->getTimestamp());
                $news[$nIndex]->updated_at = jdf::jdate('j F Y H:i:s' , Carbon::createFromFormat('Y-m-d H:i:s' , $nRow->updated_at)->getTimestamp());
            }

            $this->get('/api/news?' . http_build_query([
                    'offset' => $offset,
                    'limit' => $step
                ]))->assertJsonStructure([
                'status' => 200,
                'list' => $news->toArray()
            ])
                ->assertStatus(200);
        }
//        for ($i = 0; $i < count($fakeNews); $i++) {
//            for ($j = 1; $j < count($fakeNews) - 1; $j++) {
//                $news = DB::table('news')->orderBy('created_at', 'desc')->offset($i)->limit($j)->get();
//                $this->get('/api/news?' . http_build_query([
//                        'offset' => $i,
//                        'limit' => $j
//                    ]))
//                    ->assertJson([
//                        'status' => 200,
//                        'list' => $news
//                    ])
//                    ->assertStatus(200);
//            }
//        }
    }

    public function testShow()
    {
        $news = factory(News::class)->create();
        $news->save();
        $createdAtTimestamp = Carbon::createFromFormat('Y-m-d H:i:s', $news->created_at)->getTimestamp();
        $updatedAtTimestamp = Carbon::createFromFormat('Y-m-d H:i:s', $news->updated_at)->getTimestamp();
        $news->created_at_fa = jdf::jdate('j F Y', $createdAtTimestamp);
        $news->updated_at_fa = jdf::jdate('j F Y', $updatedAtTimestamp);
        $photos = NewsPhoto::where('news_id', $news->id)->get();
        $photos->each(function ($item, $index) use ($photos) {
            $photos[$index]->file_name = URL::to('/news_photo') . '/' . $item->file_name;
        });
        $news->photos = $photos;
        $this->get('/api/news/' . $news->id)
            ->assertStatus(200)
            ->assertExactJson([
                'status' => 200,
                'news' => $news->toArray()
            ]);
    }

}
