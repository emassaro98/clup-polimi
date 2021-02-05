<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class TimeSlot extends Model
{
    use HasFactory;

    //this function is used for map the foreing jeys
    function lineup()
    {
        return $this->hasMany('App\Models\Lineup');
    }

    function booking()
    {
        return $this->hasMany('App\Models\Booking');
    }

    function shop()
    {
        return $this->belongsTo('App\Models\Shop', 'shop_id');
    }

    //this function is use for update the capacity, for increment o decrement of one unit
    public static function updateCapacity($time_slot_up_id, $time_slot_down_id, $increment)
    {
        if($increment){
            TimeSlot::whereBetween('id', [$time_slot_down_id, $time_slot_up_id])->increment('capacity');
        } else{
            TimeSlot::whereBetween('id', [$time_slot_down_id, $time_slot_up_id])->decrement('capacity');
        }
    }

    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'time_slot',
        'date',
        'capacity'
    ];
}
