<?php

namespace App\Http\Middleware;

use Closure;
use function GuzzleHttp\Psr7\str;
use Illuminate\Support\Facades\Input;

class convertFarsiNumbers
{
    /**
     * Handle an incoming request.
     *
     * @param  \Illuminate\Http\Request $request
     * @param  \Closure $next
     * @return mixed
     */
    public function handle($request, Closure $next)
    {
        $farsiNumbers = ['۱', '۲', '۳', '۴', '۵', '۶', '۷', '۸', '۹', '۰'];
        $ennumbers = ['1', '2', '3', '4', '5', '6', '7', '8', '9', '0'];
        $requestContent = $request->all();
        foreach ($requestContent as $index => $row) {
            if (!is_array($row)) {
                $initString = (string)$row;
                foreach ($farsiNumbers as $faIndex => $faValue) {
                    $initString = str_replace($faValue, $ennumbers[$faIndex], $initString);
                }
                if (intval($initString) == $initString) {
                    $requestContent[$index] = $initString;
                } else if (doubleval($initString) == $initString) {
                    $requestContent[$index] = $initString;
                } else if (floatval($initString) == $initString) {
                    $requestContent[$index] = $initString;
                }
                Input::replace($requestContent);
            }
//            } else {
//                for ($j = 0; $j < count($row); $j++) {
//                    $rowJ = $row[$j];
//                    for ($k = 0; $k < count($row[$j]); $k++) {
//                        $rowK = $rowJ[$k];
//                        for ($l = 0; $l < count($row[$j][$k]); $l++) {
//                            foreach ($farsiNumbers as $faNumIndex => $faNumValue) {
//                                $row[$j][$k][$l] = str_replace($faNumValue, $ennumbers[$faNumIndex], $row[$j][$k][$l]);
//                            }
////                            $rowL = $rowK[$l];
////                            $row[$j][$k][$l] = $rowL;
////                            if (intval($row[$j][$k][$l]) == $rowL) {
////                                $row[$j][$k][$l] = $rowL;
////                            } else if (doubleval($rowL) == $rowL) {
////                                $row[$j][$k][$l] = $rowL;
////                            } else if (floatval($rowL) == $rowL) {
////                                $row[$j][$k][$l] = $rowL;
////                            }
//                            Input::replace([$requestContent[$index][$j][$k][$l] => $row[$j][$k][$l]]);
//
//                        }
//                        Input::replace([$requestContent[$index][$j][$k] => $row[$j][$k]]);
//                    }
//                    Input::replace([$requestContent[$index][$j] => $row[$j]]);
//                }
//            }

        }
        return $next($request);
    }
}