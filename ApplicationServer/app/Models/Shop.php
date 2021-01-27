<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class Shop extends Model
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

    function weeklySchedule()
    {
        return $this->hasMany('App\Models\WeeklySchedule');
    }

    function holiday()
    {
        return $this->hasMany('App\Models\Holiday');
    }

    function timeSlot()
    {
        return $this->hasMany('App\Models\TimeSlot');
    }
    
    //this function is used for check if in the date the shop is open
    public static function checkDate($id, $date)
    {
        $shop = Shop::find($id);
        
        //check if the shop is open for that date
        if($shop->holiday()->where('date', $date)->count() > 0){
            return false;
        } else{
            return true;
        }
    }


    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'name',
        'position',
        'capacity',
        'email',
        'phone',
        'img_filename',
        'open'
    ];
}
