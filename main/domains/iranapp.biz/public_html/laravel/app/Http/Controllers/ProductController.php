<?php

namespace App\Http\Controllers;

use App\Product;
use Illuminate\Http\Request;

class ProductController extends Controller
{
    public function purchase(Product $product)
    {
        return view('home.purchase' , compact('product'));
    }
}
