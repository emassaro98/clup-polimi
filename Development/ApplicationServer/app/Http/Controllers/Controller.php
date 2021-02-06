<?php

namespace App\Http\Controllers;

use Illuminate\Foundation\Auth\Access\AuthorizesRequests;
use Illuminate\Foundation\Bus\DispatchesJobs;
use Illuminate\Foundation\Validation\ValidatesRequests;
use Illuminate\Routing\Controller as BaseController;

class Controller extends BaseController
{
    use AuthorizesRequests, DispatchesJobs, ValidatesRequests;

    //Method for create json of a success request
    public function success($data, $code){
        return response()->json(['state' => true, 'data' => $data], $code);
    }

    //Method for create json of a success request but without data
    public function successWithMessage($message, $code){
        return response()->json(['state' => true, 'message' => $message], $code);
    }

    //Method for create json of a error request
    public function error($message, $code){
        return response()->json(['state' => false, 'message' => $message], $code);
    }
}
