<?php

namespace App\Http\Middleware;

use Closure;
use Illuminate\Support\Facades\Input;

class ConvertArabicLettersToPersian
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request  $request
     * @param  \Closure  $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        $requestContent = $request->all();
        foreach($requestContent as $index => $row){
            if(is_string($row) && (strpos($row , 'ي') !==  false || strpos($row , 'ك') !== false) ){
                $row = str_replace('ي' , 'ی' , $row);
                $row = str_replace('ك' , 'ک' , $row);
                $request->merge([$index => $row]);
            }
        }
        return $next($request);
    }
}
