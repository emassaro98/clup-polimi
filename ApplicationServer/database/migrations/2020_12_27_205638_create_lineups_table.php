<?php

use Illuminate\Database\Migrations\Migration;
use Illuminate\Database\Schema\Blueprint;
use Illuminate\Support\Facades\Schema;

class CreateLineupsTable extends Migration
{
    /**
     * Run the migrations.
     *
     * @return void
     */
    public function up()
    {
        Schema::create('lineups', function (Blueprint $table) {
            $table->id();
            $table->bigInteger('user_id')->unsigned();
            $table->bigInteger('shop_id')->unsigned();
            $table->bigInteger('time_slot_up_bound_id')->unsigned();
            $table->bigInteger('time_slot_down_bound_id')->unsigned();
            $table->foreign('user_id')
                  ->references('id')->on('users')->onDelete('cascade');
            $table->foreign('shop_id')
                  ->references('id')->on('shops')->onDelete('cascade');
            $table->foreign('time_slot_up_bound_id')
                  ->references('id')->on('time_slots');
            $table->foreign('time_slot_down_bound_id')
                  ->references('id')->on('time_slots');
            $table->time('expected_time');
            $table->time('expected_duration');
            $table->smallInteger('status')->unsigned()->default(0);  
            $table->timestamps();
        });
    }

    /**
     * Reverse the migrations.
     *
     * @return void
     */
    public function down()
    {
        Schema::dropIfExists('lineups');
    }
}
