<?php

namespace App\Http\Controllers;

use App\Models\EstateAdsCategories;
use Illuminate\Http\Request;

class EstateAdsCategoriesController extends Controller
{
    public function getByParentId(Request $request , EstateAdsCategories $category){
        $list = EstateAdsCategories::where('parent_id' , $category->id)->get();
        return response()->json(['status' => 200 , 'list' => $list]);
    }
}
