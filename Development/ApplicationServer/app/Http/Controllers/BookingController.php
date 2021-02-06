<?php

namespace App\Http\Controllers;

use App\Models\Booking;
use App\Models\Shop;
use App\Models\TimeSlot;
use App\Models\User;
use Illuminate\Http\Request;

class BookingController extends Controller
{

    //Method for update the single booking by specific id
    public function updateBooking(Request $request, $id)
    {
        //Check that there are all fields required
        $request->validate([
            'status' => 'required'
        ]);

        //Find the booking by id that we want to update
        $booking = Booking::find($id);

        //Check if the booking exist
        if(!$booking){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Check if the new status is exit (2) in order to update the time slot capacity
        if($request->status == 2 && $booking->status != 2){
            //Update capacity, increment it
            TimeSlot::whereBetween('id', [$booking->time_slot_down_bound_id, $booking->time_slot_up_bound_id])->increment('capacity');
        }

        //Update the booking
        $booking->update($request->all());

        return $this->successWithMessage('Booking updated correctly!', 200);
    }

    public function createBooking(Request $request)
    {
        //Check that there are all fields required
        $request->validate([
            'expected_duration' => 'required',
            'time_slot_up_bound_id' => 'required',
            'time_slot_down_bound_id' => 'required'
        ]);
        
        //Get user and shop by id that are associate to the new booking
        $user = User::find($request->user_id);
        $shop = Shop::find($request->shop_id);

        //Check if the user and the shop exits
        if(!$user || !$shop){
            return $this->error('Ops... Something went wrong!', 200);
        } elseif($user->booking()->where('shop_id', $request->shop_id)->where('status', 0)->count() > 0){
            return $this->error('You have already an active booking on this shop!', 200);
        }

        //Create the booking by using params in the request
        $booking = new Booking($request->all());
        
        //Associate the id of shop and user
        $booking->withUser($user)->withShop($shop)->save();

        //Update capacity
        TimeSlot::whereBetween('id', [$request->time_slot_down_bound_id, $request->time_slot_up_bound_id])->decrement('capacity');

        return $this->successWithMessage('Booking created correctly!', 200);
    }

    public function deleteBooking(Request $request, $id)
    {
        //Find the booking by id that we want delete
        $booking = Booking::find($id);

        //Check if the booking exist
        if(!$booking){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Update capacity
        TimeSlot::whereBetween('id', [$booking->time_slot_down_bound_id, $booking->time_slot_up_bound_id])->increment('capacity');

        //Delete it
        $booking->delete();

        return $this->successWithMessage('Booking deleted correctly!', 200);
    }

}
