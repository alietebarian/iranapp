<?php

namespace App\Http\Controllers;

use App\Order;
use App\Product;
use Illuminate\Http\Request;

class HomeController extends Controller
{
    public function index()
    {
        $products = Product::all();
        return view('home.index' , compact('products'));
    }
    public function profileShow(){
        $user = auth()->user();
        $orders = Order::where('user_id' , $user->id)
            ->join('products' , 'products.id' , '=' , 'orders.product_id')
            ->select('orders.paid_price' , 'orders.user_name' , 'orders.password' , 'products.disk_space' ,
                'products.title' , 'products.bandwidth_amount' , 'orders.created_at')
            ->orderBy('orders.created_at' , 'asc')
            ->get();
        return view('home.my-account' , compact('orders'));
    }
}