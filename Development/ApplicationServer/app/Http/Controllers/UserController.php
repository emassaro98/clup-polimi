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
    
    //This method is used in order to perform the login
    public function login(Request $request)
    {
        //Check that there are all fields required, and that they are right (ex the email is an email)
        $request->validate([
            'email' => 'required|email',
            'password' => 'required',
            'device_name' => 'required'
        ]);

        //Get the user with that email
        $user = User::where('email', $request->email)->first();
        
        //Check credentials
        if (! $user || ! Hash::check($request->password, $user->password)) {
            //return result error
            return $this->error('The credentials are incorrect, try again!', 200);
        }
        
        //Create the token for the user that want to login
        $token = $user->createToken($request->device_name)->plainTextToken;

        return $this->success(['id_user' => $user->id, 'token' => $token], 200);
    }

    public function signUp(Request $request){

        //Check that there are all fields required
        $request->validate([
            'email' => 'required',
            'password' => 'required',
            'device_name' => 'required',
            'name' => 'required'
        ]);
        
        //Check that all fields are ok, if not send the related error message (we don't use the validate function of laravel, for other purpose)
        if(strlen($request->password) < 6 || strlen($request->password) > 15){
            return $this->error('The password must have a length between 6 and 15!', 200);
        } elseif (User::where('email', $request->email)->count() > 0){
            return $this->error('The email already exits!', 200);
        } elseif (!filter_var($request->email, FILTER_VALIDATE_EMAIL)){
            return $this->error('The email is not valid!', 200);
        }

        //Create the user
        $user = new User();
        $user->name = $request->name;
        $user->password = Hash::make($request->password);
        $user->email = $request->email;
        $user->device_name = $request->device_name;
        $user->save();

        return $this->successWithMessage('Your sign up was successful!', 200);
    }

    //Get lineups and bookings active
    public function getActiveReservations(Request $request, $id)
    {
        //Get user in which we want to get lineups and bookings
        $user = User::find($id);

        //Check if the user exist
        if(!$user){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Do query that get lineup and booking of the user with a status active
        $lineups = Lineup::getAllWithStatus($id, 0)->join('shops', 'lineups.shop_id', '=', 'shops.id')->join('time_slots', 'time_slots.id', '=', 'lineups.time_slot_down_bound_id')->get(['lineups.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename', 'time_slots.date as date']);
        $bookings = Booking::getAllWithStatus($id, 0)->join('shops', 'bookings.shop_id', '=', 'shops.id')->join('time_slots', 'bookings.time_slot_down_bound_id', '=', 'time_slots.id')->get(['bookings.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename', 'time_slots.time_slot as time_slot', 'time_slots.date as date']);

        //Format in the correct way for the application the expected_time
        $lineups->map(function ($item, $request) {
            $item['expected_time'] = substr($item->expected_time, 0, 5);
            return $item;
        });

        //Format in the correct way for the application the date
        $bookings->map(function ($item, $request) {
            $item['time_slot'] = substr($item->time_slot, 0, 5);
            return $item;
        });

        return $this->success(['lineups' => $lineups, 'bookings' => $bookings], 200);
    }

    //Get all lineups and bookings 
    public function getAllReservations(Request $request, $id)
    {   
        //Get user model in which we want to get lineups and bookings
        $user = User::find($id);

        //Check if the user exist
        if(!$user){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Do query that get all lineup and booking and for get informations of the relative store
        $lineups = Lineup::getAllWithStatus($id, 2)->join('shops', 'lineups.shop_id', '=', 'shops.id')->join('time_slots', 'time_slots.id', '=', 'lineups.time_slot_down_bound_id')->get(['lineups.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename', 'time_slots.date as date']);
        $bookings = Booking::getAllWithStatus($id, 2)->join('shops', 'bookings.shop_id', '=', 'shops.id')->join('time_slots', 'time_slots.id', '=', 'bookings.time_slot_down_bound_id')->get(['bookings.*', 'shops.position as shop_position', 'shops.name as shop_name', 'shops.img_filename as shop_img_filename', 'time_slots.date as date']);
    
        return $this->success(['lineups' => $lineups, 'bookings' => $bookings], 200);
    }

}