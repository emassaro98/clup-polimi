<?php

namespace Database\Seeders;

use Illuminate\Database\Seeder;

class DatabaseSeeder extends Seeder
{
    /**
     * Seed the application's database.
     *
     * @return void
     */
    public function run()
    {
        //\App\Models\User::factory(1)->create();
        \App\Models\Shop::factory(2)->create();
        \App\Models\TimeSlot::factory(5)->create();
        //\App\Models\Lineup::factory(2)->create();
        //\App\Models\Booking::factory(2)->create();
    }
}
