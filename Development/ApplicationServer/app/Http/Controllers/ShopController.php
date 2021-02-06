<?php

namespace App\Http\Controllers;

use App\Models\Shop;
use App\Models\TimeSlot;
use App\Models\Lineup;
use App\Models\Booking;
use App\Models\Holiday;
use Illuminate\Http\Request;
use Carbon\Carbon;
use Illuminate\Support\Facades\Log;

class ShopController extends Controller
{
    //Get all the shops
    public function getAllShops(Request $request)
    {
        //Do query that get all the shops
        $shops = Shop::all();

        return $this->success(['shops' => $shops], 200);
    }

    //Get available time slot
    public function getAvailableTimeSlots(Request $request, $id)
    {
        //Find the shop
        $shop = Shop::find($id);

        //Check if the shop exist
        if(!$shop){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Get current time
        $time = Carbon::now('Europe/Rome');
        $today = Carbon::today()->format('Y-m-d');

        //Check if the shop in that date is open
        if (Shop::checkDate($id, $request->date)){
            //Query for the time slot available, we need to check that if is today, we need to exclude past timeslots
            if($today == $request->date){
                $time_slots = $shop->timeSlot()->where('capacity', '>', 0)->where('date', $request->date)->where('time_slot', '>', $time)->get();
            }else{
                $time_slots = $shop->timeSlot()->where('capacity', '>', 0)->where('date', $request->date)->get();
            }
            
            //Get number of times that we need to consider for the duration that the user could be in the shop
            //The var expected_duration are the minutes specify by the user, and is the time could be in the shop
            $n_duration = ($request->expected_duration)/15; 
            //We get how much timeslot we have
            $n_time_slots = $time_slots->count();

            //We add new fields of the obj in order to retrive this in the json
            $time_slots->map(function ($item, $request) {
                //Represent the id of bounds of timeslot that the user should use based on the expected time
                $item['time_slot_up_bound_id'] = $item->id;
                $item['time_slot_down_bound_id'] = $item->id;
                $item['expected_duration'] =  "";
                return $item;
            });

            //Now we consider each time slot and see if are compatible with the expected duration
            for ($i = 0; $i < $n_time_slots; $i++) {
                //We set to 1 the var neighbours, that is used for understand if the time slot is available considered the expected_duration
                $neighbours = 1;
                $time_slot = Carbon::createFromFormat('H:i:s', $time_slots[$i]->time_slot);

                //Check if there are sufficent time slots (after the one that we have) considered the expected_duration
                if($i + $n_duration <=  $n_time_slots){
                    //We iterate for each time slot after the one considered
                    for($j = $i + 1; $j <= $n_duration + $i - 1; $j++){
                        $time_slot_next = Carbon::createFromFormat('H:i:s', $time_slots[$j]->time_slot);
                        //We check if the timeslot j, is after i ex. 8.15, 8.30 ecc, than are continuos time slot
                        if($time_slot_next->equalTo($time_slot->addMinutes(15))){
                            $neighbours++;
                        }
                    }
                }

                //If the time slot i have a sufficent numbers of neighbours sequential after it, then is a valid time slot
                if($neighbours >= $n_duration){
                    $time_slots[$i]->time_slot_up_bound_id = $time_slots[$i]->id + $n_duration - 1;
                    if($n_duration == 4){
                        $time_slots[$i]->expected_duration = $request->expected_duration.":00:00";
                    }else{
                        $time_slots[$i]->expected_duration = "00:".$request->expected_duration.":00";
                    }
                    
                    $time_slots[$i]->time_slot = substr($time_slots[$i]->time_slot, 0, 5);  
                } 
                else{
                    //Remove time slot form the json, that doesn't satisfy the expected_duration
                    $time_slots->forget($i);
                }
            }
            return $this->success(['time_slots' => $time_slots], 200);
        } else{

            return $this->error('No timeslots avaiable!', 200);
        }
    }

    public function createTimeSlots(Request $request){

        //Get user and shop by id that are associate to the new timeslot
        $shop = Shop::find($request->shop_id);
        
        //Check if the shop exist
        if(!$shop){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Create the time slots for the days specified
        for($i = 0; $i < $request->quantity_days; $i++){

            $date = Carbon::createFromFormat('Y-m-d',  $request->starting_date); 
            $time = Carbon::createFromFormat('H:i:s',  $request->starting_time); 
            $date = $date->addDays($i);

            for($j = 0; $j < $request->quantity_slots; $j++){
                //Create the time slot
                $time_slot = new TimeSlot();
                $time_slot->time_slot = $time->addMinute(15)->format('H:i:s');
                $time_slot->date = $date;
                $time_slot->capacity = $request->capacity;
                $time_slot->withShop($shop)->save();

            }

        }

        return $this->successWithMessage('Time slots created!', 200);
    }

    //This method is used in order to check the reservation in which the customer did not show up
    public function checkStatus(Request $request){

        //Get the current date and time
        $time = Carbon::now('Europe/Rome')->subMinute(15);
        $date = Carbon::today('Europe/Rome')->format('Y-m-d');

        //Update the status of these reservations
        $lineups = Lineup::join('time_slots', 'lineups.time_slot_down_bound_id', '=', 'time_slots.id')->where('status', '0')->where('time_slot', '<', $time)->where('date', $date)->update(array('status' => '2', 'expected_duration' => '0'));
        $bookings = Booking::join('time_slots', 'bookings.time_slot_down_bound_id', '=', 'time_slots.id')->where('status', '0')->where('time_slot', '<', $time)->where('date', $date)->update(array('status' => '2', 'expected_duration' => '0'));

        return $this->successWithMessage('Reservations checked!', 200);
    }

}