<?php

namespace Database\Factories;

use App\Models\Booking;
use App\Models\User;
use App\Models\Shop;
use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Str;

class BookingFactory extends Factory
{
    /**
     * The name of the factory's corresponding model.
     *
     * @var string
     */
    protected $model = Booking::class;

    /**
     * Define the model's default state.
     *
     * @return array
     */
    public function definition()
    {
        return [
            'user_id' => User::inRandomOrder()->value('id'),
            'shop_id' => Shop::inRandomOrder()->value('id'),
            'time_slot' => $this->faker->time,
            'status' => $this->faker->numberBetween($min = 0, $max = 3),
        ];
    }
}
