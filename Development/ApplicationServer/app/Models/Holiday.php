<?php

namespace App\Models;

use Illuminate\Database\Eloquent\Model;

class Holiday extends Model
{

    //these methods are used for map the foreing keys in laravel
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
