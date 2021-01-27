<?php

namespace Database\Factories;

use App\Models\Shop;
use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Str;

class ShopFactory extends Factory
{
    /**
     * The name of the factory's corresponding model.
     *
     * @var string
     */
    protected $model = Shop::class;

    /**
     * Define the model's default state.
     *
     * @return array
     */
    public function definition()
    {
        return [
            'name' => $this->faker->name,
            'position' => $this->faker->address,
            'capacity' => $this->faker->numberBetween($min = 1, $max = 50),
            'email' => $this->faker->unique()->safeEmail,
            'phone' => $this->faker->randomNumber(),
            'img_filename' => $this->faker->name
        ];
    }
}
