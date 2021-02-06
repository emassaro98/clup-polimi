<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;
use Illuminate\Database\Eloquent\Factories\HasFactory;

class TimeSlot extends Model
{
    use HasFactory;

    //these methods are used for map the foreing keys in laravel
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

    //these methods are used for associate the foreign keys in laravel
    public function withShop($shop)
    {
        $this->shop()->associate($shop);
        return $this;
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
