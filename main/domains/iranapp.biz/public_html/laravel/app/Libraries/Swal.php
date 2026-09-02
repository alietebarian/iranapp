<?php
namespace App\Libraries;
class Swal{

    public static function success($title , $description){
        $swal = new \stdClass();
        $swal->title = $title;
        $swal->description = $description;
        request()->session()->flash('success_swal' , $swal);
    }

    public static function error($title , $description){
        $swal = new \stdClass();
        $swal->title = $title;
        $swal->description = $description;
        request()->session()->flash('error_swal' , $swal);
    }
}