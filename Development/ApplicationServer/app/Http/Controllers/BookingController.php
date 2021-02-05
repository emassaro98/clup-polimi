<?php

namespace App\Http\Controllers;

use App\Models\Booking;
use App\Models\Shop;
use App\Models\TimeSlot;
use App\Models\User;
use Illuminate\Http\Request;

class BookingController extends Controller
{

    //method for update the single Booking by specific id
    public function updateBooking(Request $request, $id)
    {
        //Validate
        $request->validate([
            'status' => 'required'
        ]);

        //find the Booking by id that we wanto to update
        $booking = Booking::find($id);

        if(!$booking){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //check if the new status is enter (1) or exit (2)
        if($request->status == 2 && $booking->status != 2){
            //update capacity, increment it
            TimeSlot::whereBetween('id', [$booking->time_slot_down_bound_id, $booking->time_slot_up_bound_id])->increment('capacity');
        }

        //update the Booking
        $booking->update($request->all());

        return $this->successWithMessage('Booking updated correctly!', 200);
    }

    public function createBooking(Request $request)
    {
        //Validate
        $request->validate([
            'expected_duration' => 'required',
            'time_slot_up_bound_id' => 'required',
            'time_slot_down_bound_id' => 'required'
        ]);
        
        //get user and shop by ids that are associate to the new Booking
        $user = User::find($request->user_id);
        $shop = Shop::find($request->shop_id);

        if(!$user || !$shop){
            return $this->error('Ops... Something went wrong!', 200);
        } elseif($user->booking()->where('shop_id', $request->shop_id)->where('status', 0)->count() > 0){
            return $this->error('You have already an active booking on this shop!', 200);
        }

        //cerate the Booking by params in the request
        $booking = new Booking($request->all());
        
        //associate the ids of shop and user
        $booking->withUser($user)->withShop($shop)->save();

        //update capacity
        TimeSlot::whereBetween('id', [$request->time_slot_down_bound_id, $request->time_slot_up_bound_id])->decrement('capacity');

        return $this->successWithMessage('Booking created correctly!', 200);
    }

    public function deleteBooking(Request $request, $id)
    {
        //find the booking by id that we want delete
        $booking = Booking::find($id);

        if(!$booking){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //update capacity
        TimeSlot::whereBetween('id', [$booking->time_slot_down_bound_id, $booking->time_slot_up_bound_id])->increment('capacity');

        //delete it
        $booking->delete();

        return $this->successWithMessage('Booking deleted correctly!', 200);
    }

}
