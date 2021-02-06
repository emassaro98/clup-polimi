<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Factories\HasFactory;
use Illuminate\Database\Eloquent\Model;

class Lineup extends Model
{
    use HasFactory;

    //these methods are used for map the foreing keys in laravel
    function user()
    {
        return $this->belongsTo('App\Models\User', 'user_id');
    }

    function shop()
    {
        return $this->belongsTo('App\Models\Shop', 'shop_id');
    }

    function timeSlotUp()
    {
        return $this->belongsTo('App\Models\TimeSlot', 'time_slot_up_bound_id');
    }

    function timeSlotDown()
    {
        return $this->belongsTo('App\Models\TimeSlot', 'time_slot_down_bound_id');
    }

    //these methods are used for associate the foreign keys in laravel
    public function withUser($user)
    {
        $this->user()->associate($user);
        return $this;
    }

    public function withShop($shop)
    {
        $this->shop()->associate($shop);
        return $this;
    }

    public function withTimeSlotUp($time_slot)
    {
        $this->timeSlotUp()->associate($time_slot);
        return $this;
    }

    public function withTimeSlotDown($time_slot)
    {
        $this->timeSlotDown()->associate($time_slot);
        return $this;
    }

    //this function is used for all lineup of the user with a specific status
    public static function getAllWithStatus($user_id, $status)
    {
        return Lineup::where('user_id', $user_id)->where('status', $status);
    }

    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'expected_time',
        'status',
        'expected_duration',
        'time_slot_up_bound_id',
        'time_slot_down_bound_id'
    ];
}
