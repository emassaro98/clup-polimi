<?php

namespace Database\Factories;

use App\Models\User;
use Illuminate\Database\Eloquent\Factories\Factory;
use Illuminate\Support\Str;

class UserFactory extends Factory
{
    /**
     * The name of the factory's corresponding model.
     *
     * @var string
     */
    protected $model = User::class;

    /**
     * Define the model's default state.
     *
     * @return array
     */
    public function definition()
    {
        return [
            'name' => 'prova',
            'email' => 'prova@prova.com',
            'email_verified_at' => now(),
            'device_name' => 'paolo',
            'password' => '$2y$10$E4uNyC51qcvYGBlHNIHhi.RaOShCt7d5baqpWfIxVq6xAK6uzBdK6',
            'remember_token' => Str::random(10),
        ];
    }
}
