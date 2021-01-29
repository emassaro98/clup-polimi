<?php

namespace App\Http\Controllers;

use App\Models\Lineup;
use App\Models\Shop;
use App\Models\User;
use App\Models\TimeSlot;
use Carbon\Carbon;
use Illuminate\Support\Facades\Log;
use Illuminate\Http\Request;

class LineupController extends Controller
{

    public function getAllLineups($id_user)
    {
        $user = User::findOrFail($id_user);

        $lineups = $user->lineup()->get();

        return $lineups;
    }


    //method for update the single lineup by specific id
    public function updateLineup(Request $request, $id)
    {
        //Validate
        $request->validate([
            'status' => 'required'
        ]);

        //find the lineup by id that we wanto to update
        $lineup = Lineup::find($id);

        if(!$lineup){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //check if the new status is enter (1) or exit (2)
        if($request->status == 1 || $request->status == 2){
            //update capacity decrement it
            TimeSlot::updateCapacity($lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id, false);
        } 

        //update the lineup
        $lineup->update($request->all());

        return $this->successWithMessage('Lineup updated correctly!', 200);
    }

    public function createLineup(Request $request)
    {
        //Validate
        $request->validate([
            'expected_duration' => 'required'
        ]);
        
        //get user and shop by ids that are associate to the new lineup
        $user = User::find($request->user_id);
        $shop = Shop::find($request->shop_id);

        if(!$user || !$shop){
            return $this->error('Ops... Something went wrong!', 200);
        }

        $date = Carbon::today()->format('Y-m-d');
        $time = Carbon::now('Europe/Rome');
        $n_duration = ($request->expected_duration)/15;
        
        //find the timeslots for the lineup in order to get the expected time
        $time_slots = $shop->timeSlot()->where('capacity', '>', 0)->where('date', $date)->where('time_slot', '>=', $time)->get();
        $n_time_slots = $time_slots->count();

        //We add due field of the obj in order to retrive this in the json
        $time_slots->map(function ($item, $request) {
            //represent the id of bounds of timeslot that the user should use based on the expected time
            $item['time_slot_up_bound_id'] = $item->id;
            $item['time_slot_down_bound_id'] = $item->id;
            return $item;
        });

        for ($i = 0; $i < $n_time_slots; $i++) {
            //we set to 1 the var neighbours, that is used for understand if the time slot is aviable, considered the expected_duration
            $neighbours = 1;
            $time_slot = Carbon::createFromFormat('H:i:s', $time_slots[$i]->time_slot);

            //check if there are sufficent time slots (after the one that we have) considered the expected_duration
            if($i + ($n_duration-1) <=  ($n_time_slots-1)){
                //we iterate for each time slot after the one considered
                for($j = $i + 1; $j < $n_duration; $j++){
                    $time_slot_next = Carbon::createFromFormat('H:i:s', $time_slots[$j]->time_slot);
                    //Log::info('In cycle of id: '.$time_slots[$j]->id.' with j: '.$j.' with i: '.$i.' with value of neighbours: '.$neighbours);
                    //we check if the timeslot j, is after i ex. 8.15, 8.30 ecc, than are continuos time slot
                    if($time_slot_next->equalTo($time_slot->addMinutes(15))){
                        $neighbours++;
                    }
                }
            }

            //if the time slot i have a sufficent numbers of neighbours sequential after it, then is a valid time slot
            if($neighbours >= $n_duration){
                $time_slots[$i]->time_slot_up_bound_id = $time_slots[$i]->id + $n_duration - 1;
                $time_slots[$i]->time_slot = substr($time_slots[$i]->time_slot, 0, 5);
                break;  
            } 
            else{
                //remove time slot form the json, that doesn't satisfy the expected_duration
                $time_slots->forget($i);
            }
        }

        if($n_time_slots == 0){
            return $this->error('No time slot available for today!', 200);
        }
        else{
            //create the lineup by params in the request
            $lineup = new Lineup();
            $lineup->user_id = $request->user_id;
            $lineup->shop_id = $request->shop_id;
            $lineup->expected_duration = "00:".$request->expected_duration.":00";
            $lineup->time_slot_down_bound_id = $time_slots[0]->time_slot_down_bound_id;
            $lineup->time_slot_up_bound_id =  $time_slots[0]->time_slot_up_bound_id;
            $lineup->expected_time = $time_slots[0]->time_slot;

            //associate the ids of shop and user
            $lineup->withUser($user)->withShop($shop)->save();

            //update capacity
            TimeSlot::whereBetween('id', [$lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id])->decrement('capacity');

            return $this->success(['date' => $date, 'expected_time' => $time_slots[0]->time_slot], 200);
        }
    }

    public function deleteLineup(Request $request, $id)
    {
        //find the linup by id that we want delete
        $lineup = Lineup::find($id);

        if(!$lineup){
            return $this->error('Ops... Something went wrong!', 200);
        }

        //update capacity
        TimeSlot::whereBetween('id', [$lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id])->increment('capacity');

        //delete it
        $lineup->delete();

        return $this->successWithMessage('Lineup deleted correctly!', 200);
    }

}
