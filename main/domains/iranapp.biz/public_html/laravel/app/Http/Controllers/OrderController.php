<?php

namespace App\Http\Controllers;

use App\Libraries\Swal;
use App\Product;
use Illuminate\Http\Request;

class OrderController extends Controller
{
    public function store(Request $request){
        $this->validate($request , [
            'product_id' => 'required|numeric|exists:products,id',
            'agreements' => 'required'
        ]);
        $product = Product::find($request->product_id);
        $user = auth()->user();
        $user->orders()->create([
            'product_id' => $request->product_id,
            'paid_price' => $product->price_per_month,
            'user_name' => uniqid(),
            'password' => rand(111111 , 999999)
        ]);
        Swal::success('خرید محصول' , 'محصول مورد نظر با موفقیت خریداری شد.');
        return redirect()->to('/my-account');

    }
}
