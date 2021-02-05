<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Holiday extends Model
{

    //this function is used for map the foreing keys
    function shop()
    {
        return $this->belongsTo('App\Models\Shop', 'shop_id');
    }

    /**
     * The attributes that are mass assignable.
     *
     * @var array
     */
    protected $fillable = [
        'date',
    ];
}
