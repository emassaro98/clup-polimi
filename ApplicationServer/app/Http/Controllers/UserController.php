<?php

namespace App\Http\Controllers;

use App\Models\User;
use App\Models\Lineup;
use App\Models\Booking;
use Illuminate\Http\Request;
use Illuminate\Support\Facades\Log;
use Illuminate\Support\Facades\Hash;

class UserController extends Controller
{
    
    //login method
    public function login(Request $request)
    {
        //check that there are all fields required, and that they are right (ex the email is an email)

        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
            'device_name' => 'required'
        ]);

        //get the user with that email
        $user = User::where('email', $request->email)->first();
        
        //check credentials
        if (! $user || ! Hash::check($request->password, $user->password)) {
            //return result error
            return $this->error('The credentials are incorrect, try again!', 200);
        }
        
        //create the token for the user that want to login
        $token = $user->createToken($request->device_name)->plainTextToken;

        return $this->success(['id_user' => $user->id, 'token' => $token], 200);
    }

    //get lineups and bookings active
    public function getActiveReservations(Request $request, $id)
    {
        //get user model in which we want to get lineups and bookings
        $user = User::find($id);
        
        if(!$user){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //do query that get lineup and booking of the user with a status active
        $lineups = Lineup::getAllWithStatus($id, 0)->join('shops', 'lineups.shop_id', '=', 'shops.id')->get(['lineups.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename']);
        $bookings = Booking::getAllWithStatus($id, 0)->join('shops', 'bookings.shop_id', '=', 'shops.id')->join('time_slots', 'bookings.time_slot_down_bound_id', '=', 'time_slots.id')->get(['bookings.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename', 'time_slots.time_slot as time_slot']);

        //Format in correct format for the application the expected_time
        $lineups->map(function ($item, $request) {
            $item['expected_time'] = substr($item->expected_time, 0, 5);
            return $item;
        });

        //Format in correct format for the application the date
        $bookings->map(function ($item, $request) {
            $item['date'] = substr($item->created_at, 0, 9);
            $item['time_slot'] = substr($item->time_slot, 0, 5);
            return $item;
        });

        return $this->success(['lineups' => $lineups, 'bookings' => $bookings], 200);
    }

    //get all lineups and bookings 
    public function getAllReservations(Request $request, $id)
    {   
        //get user model in which we want to get lineups and bookings
        $user = User::find($id);

        if(!$user){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //do query that get all lineup and booking and for get information of that store
        $lineups = Lineup::getAllWithStatus($id, 2)->join('shops', 'lineups.shop_id', '=', 'shops.id')->get(['lineups.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename']);
        $bookings = Booking::getAllWithStatus($id, 2)->join('shops', 'bookings.shop_id', '=', 'shops.id')->get(['bookings.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename']);

        //Format in correct format for the application the date
        $bookings->map(function ($item, $request) {
            $item['date'] = substr($item->created_at, 0, 9);
            return $item;
        });

        //Format in correct format for the application the date
        $lineups->map(function ($item, $request) {
            $item['date'] = substr($item->created_at, 0, 9);
            return $item;
        });
    
        return $this->success(['lineups' => $lineups, 'bookings' => $bookings], 200);
    }

}