<?php

namespace Database\Factories;

use App\Models\Lineup;
use App\Models\User;
use App\Models\Shop;
use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Str;

class LineupFactory extends Factory
{
    /**
     * The name of the factory's corresponding model.
     *
     * @var string
     */
    protected $model = Lineup::class;

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
            'expected_time' => $this->faker->time,
            'status' => $this->faker->numberBetween($min = 0, $max = 3),
        ];
    }
}
