<?php

namespace App\Http\Controllers;

use App\Models\Lineup;
use App\Models\Shop;
use App\Models\TimeSlot;
use App\Models\User;
use Carbon\Carbon;
use Illuminate\Http\Request;

class LineupController extends Controller
{

    //Method for update the single lineup by specific id
    public function updateLineup(Request $request, $id)
    {
        //Check that there are all fields required
        $request->validate([
            'status' => 'required',
        ]);

        //Find the lineup by id that we want to update
        $lineup = Lineup::find($id);

        //Check if the lineup exist
        if (!$lineup) {
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Check if the new status is exit (2) in order to update the time slot capacity
        if ($request->status == 1 && $lineup == 0) {
            //Update capacity decrement it
            TimeSlot::whereBetween('id', [$lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id])->decrement('capacity');
        } else if ($request->status == 2 && $lineup != 2) {
            //Update capacity increment it
            TimeSlot::whereBetween('id', [$lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id])->increment('capacity');
        }

        //Update the lineup
        $lineup->update($request->all());

        return $this->successWithMessage('Lineup updated correctly!', 200);
    }

    public function createLineup(Request $request)
    {
        //Check that there are all fields required
        $request->validate([
            'expected_duration' => 'required',
        ]);

        //Get user and shop by ids that are associate to the new lineup
        $user = User::find($request->user_id);
        $shop = Shop::find($request->shop_id);

        //Check if the user and the shop exits
        if (!$user || !$shop) {
            return $this->error('Ops... Something went wrong!', 200);
        } elseif ($user->lineup()->where('shop_id', $request->shop_id)->where('status', 0)->count() > 0) {
            return $this->error('You have already an active lineup on this shop!', 200);
        }

        //Get the current time and date
        $date = Carbon::today()->format('Y-m-d');
        $time = Carbon::now('Europe/Rome');
        $n_duration = ($request->expected_duration) / 15;

        //Check if the shop in that date is open
        if (Shop::checkDate($request->shop_id, $request->date)) {

            //Find the timeslots for the lineup in order to get the expected time
            $time_slots = $shop->timeSlot()->where('capacity', '>', 0)->where('date', $date)->where('time_slot', '>=', $time)->get();
            $n_time_slots = $time_slots->count();

            //We add due field of the obj in order to retrive this in the json
            $time_slots->map(function ($item, $request) {
                //Represent the id of bounds of time slot that the user should use based on the expected time
                $item['time_slot_up_bound_id'] = $item->id;
                $item['time_slot_down_bound_id'] = $item->id;
                return $item;
            });

            for ($i = 0; $i < $n_time_slots; $i++) {
                //We set to 1 the var neighbours, that is used for understand if the time slot is available considered the expected_duration
                $neighbours = 1;
                $time_slot = Carbon::createFromFormat('H:i:s', $time_slots[$i]->time_slot);

                //Check if there are sufficent time slots (after the one that we have) considered the expected_duration
                if ($i + $n_duration <= $n_time_slots) {
                    //We iterate for each time slot after the one considered
                    for ($j = $i + 1; $j <= $n_duration + $i - 1; $j++) {
                        $time_slot_next = Carbon::createFromFormat('H:i:s', $time_slots[$j]->time_slot);
                        //We check if the timeslot j, is after i ex. 8.15, 8.30 ecc, than are continuos time slot
                        if ($time_slot_next->equalTo($time_slot->addMinutes(15))) {
                            $neighbours++;
                        }
                    }
                }

                //If the time slot i have a sufficent numbers of neighbours sequential after it, then is a valid time slot
                if ($neighbours >= $n_duration) {
                    $time_slots[$i]->time_slot_up_bound_id = $time_slots[$i]->id + $n_duration - 1;
                    $time_slots[$i]->time_slot = substr($time_slots[$i]->time_slot, 0, 5);
                    break;
                } else {
                    //Remove time slot form the json, that doesn't satisfy the expected_duration
                    $time_slots->forget($i);
                }
            }

            //Check if we have time slot
            if ($n_time_slots == 0) {
                return $this->error('No available time slot for today!', 200);
            } else {
                //Create the lineup by params in the request
                $lineup = new Lineup();
                $lineup->user_id = $request->user_id;
                $lineup->shop_id = $request->shop_id;
                $lineup->expected_duration = "00:" . $request->expected_duration . ":00";
                $lineup->time_slot_down_bound_id = $time_slots[0]->time_slot_down_bound_id;
                $lineup->time_slot_up_bound_id = $time_slots[0]->time_slot_up_bound_id;
                $lineup->expected_time = $time_slots[0]->time_slot;

                //Associate the id of shop and user
                $lineup->withUser($user)->withShop($shop)->save();

                //Update capacity
                TimeSlot::whereBetween('id', [$lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id])->decrement('capacity');

                return $this->success(['date' => $date, 'expected_time' => $time_slots[0]->time_slot], 200);
            }
        } else {
            return $this->error('No avaiable timeslots!', 200);
        }
    }

    public function deleteLineup(Request $request, $id)
    {
        //Find the lineup by id that we want delete
        $lineup = Lineup::find($id);

        //Check if the lineup exist
        if (!$lineup) {
            return $this->error('Ops... Something went wrong!', 200);
        }

        //Update capacity
        TimeSlot::whereBetween('id', [$lineup->time_slot_down_bound_id, $lineup->time_slot_up_bound_id])->increment('capacity');

        //Delete it
        $lineup->delete();

        return $this->successWithMessage('Lineup deleted correctly!', 200);
    }

}
