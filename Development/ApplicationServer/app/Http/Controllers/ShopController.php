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
    //get lineups and bookings active
    public function getAllShops(Request $request)
    {
        //do query that get lineup and booking of the user with a status active
        $shops = Shop::all();

        return $this->success(['shops' => $shops], 200);
    }

    //get available time slot
    public function getAvailableTimeSlots(Request $request, $id)
    {
        //find the shop
        $shop = Shop::find($id);

        if(!$shop){
            return $this->error('Ops... Something went wrong!', 200);
        }

        $time = Carbon::now('Europe/Rome');
        $today = Carbon::today()->format('Y-m-d');

        if (Shop::checkDate($id, $request->date)){
            //query for the time slot aviable, we need to check that if is today, we need to exclude past timeslots
            if($today == $request->date){
                $time_slots = $shop->timeSlot()->where('capacity', '>', 0)->where('date', $request->date)->where('time_slot', '>', $time)->get();
            }else{
                $time_slots = $shop->timeSlot()->where('capacity', '>', 0)->where('date', $request->date)->get();
            }
            
            //get number of times that we need to consider for the duration that the user could be in the shop
            //the var expected_duration are the minutes specify by the user, and is the time could be in the shop
            $n_duration = ($request->expected_duration)/15; 
            //we get how much timeslot we have
            $n_time_slots = $time_slots->count();

            //We add due field of the obj in order to retrive this in the json
            $time_slots->map(function ($item, $request) {
                //represent the id of bounds of timeslot that the user should use based on the expected time
                $item['time_slot_up_bound_id'] = $item->id;
                $item['time_slot_down_bound_id'] = $item->id;
                $item['expected_duration'] =  "";
                return $item;
            });

            //now we consider each time slot and see if are compatible with the expected duration
            for ($i = 0; $i < $n_time_slots; $i++) {
                //we set to 1 the var neighbours, that is used for understand if the time slot is aviable, considered the expected_duration
                $neighbours = 1;
                $time_slot = Carbon::createFromFormat('H:i:s', $time_slots[$i]->time_slot);


                //check if there are sufficent time slots (after the one that we have) considered the expected_duration
                if($i + $n_duration <=  $n_time_slots){
                    Log::info('In if of id: '.$time_slots[$i]->id.' with i: '.$i);
                    //we iterate for each time slot after the one considered
                    for($j = $i + 1; $j <= $n_duration + $i - 1; $j++){
                        $time_slot_next = Carbon::createFromFormat('H:i:s', $time_slots[$j]->time_slot);
                        //we check if the timeslot j, is after i ex. 8.15, 8.30 ecc, than are continuos time slot
                        //Log::info('In cycle of id: '.$time_slots[$j]->id.' with j: '.$j.' with i: '.$i.' with time_slot_next: '.$time_slot_next.' with time_slot: '.$time_slot);
                        if($time_slot_next->equalTo($time_slot->addMinutes(15))){
                            $neighbours++;
                        }
                        //Log::info('In cycle of id: '.$time_slots[$j]->id.' with j: '.$j.' with i: '.$i.' with value of neighbours: '.$neighbours);
                    }
                }

                //Log::info('In of id: '.$time_slots[$i]->id.' with i: '.$i.' with value of neighbours: '.$neighbours);
                //Log::info('In of id: '.' with i: '.$i);

                //if the time slot i have a sufficent numbers of neighbours sequential after it, then is a valid time slot
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
                    //remove time slot form the json, that doesn't satisfy the expected_duration
                    //Log::info('In cycle forget of id: '.$time_slots[$i]->id.' with i: '.$i.' with value of neighbours: '.$neighbours);
                    $time_slots->forget($i);
                }
            }
            return $this->success(['time_slots' => $time_slots], 200);
        } else{

            return $this->error('No timeslots avaiable!', 200);
        }
    }

    public function createTimeSlots(Request $request){

        //get user and shop by ids that are associate to the new timeslot
        $shop = Shop::find($request->shop_id);
        
        if(!$shop){
            return $this->error('Ops... Something went wrong!', 200);
        }


        for($i = 0; $i < $request->quantity_days; $i++){

            $date = Carbon::createFromFormat('Y-m-d',  $request->starting_date); 
            $time = Carbon::createFromFormat('H:i:s',  $request->starting_time); 
            $date = $date->addDays($i);

            for($j = 0; $j < $request->quantity_slots; $j++){
                //create the time slot
                $time_slot = new TimeSlot();
                $time_slot->time_slot = $time->addMinute(15)->format('H:i:s');
                $time_slot->date = $date;
                $time_slot->capacity = $request->capacity;
                $time_slot->withShop($shop)->save();

            }

        }

        return $this->successWithMessage('Time slots created!', 200);
    }

    public function checkStatus(Request $request){

        $time = Carbon::now('Europe/Rome')->subMinute(15);
        $date = Carbon::today('Europe/Rome')->format('Y-m-d');

        $lineups = Lineup::join('time_slots', 'lineups.time_slot_down_bound_id', '=', 'time_slots.id')->where('status', '0')->where('time_slot', '<', $time)->where('date', $date)->update(array('status' => '2', 'expected_duration' => '0'));
        $bookings = Booking::join('time_slots', 'bookings.time_slot_down_bound_id', '=', 'time_slots.id')->where('status', '0')->where('time_slot', '<', $time)->where('date', $date)->update(array('status' => '2', 'expected_duration' => '0'));

        return $this->successWithMessage('Reservations checked!', 200);
    }

}