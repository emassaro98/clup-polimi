<?php

use App\Http\Controllers\BookingController;
use App\Http\Controllers\LineupController;
use App\Http\Controllers\ShopController;
use App\Http\Controllers\UserController;
use Illuminate\Support\Facades\Route;

/*
|--------------------------------------------------------------------------
| API Routes
|--------------------------------------------------------------------------
|
| Here is where you can register API routes for your application. These
| routes are loaded by the RouteServiceProvider within a group which
| is assigned the "api" middleware group. Enjoy building your API!
|
 */

//Routes for user not logged
Route::middleware('guest')->group(
    function () {
        //Route for the login
        Route::post('/login', [UserController::class, 'login']);
        //Route for the singUp
        Route::post('/signUp', [UserController::class, 'signUp']);
        //Route for the cronJobs
        Route::get('/checkStatus', [ShopController::class, 'checkStatus']);
        //Route::post('/createTimeSlots', [ShopController::class, 'createTimeSlots']);
        //Route when the token is not valid
        Route::get('/login', function () {return response()->json(['state' => false, 'message' => 'The token is not valid!'], 200);})->name('login');
    }
);

//Routes for user logged
Route::middleware('auth:sanctum')->group(
    function () {
        //Route for userServices
        Route::get('users/getActiveReservations/{id}', [UserController::class, 'getActiveReservations']);
        Route::get('users/getAllReservations/{id}', [UserController::class, 'getAllReservations']);

        //Route for lineupServices
        Route::post('lineups', [LineupController::class, 'createLineup']);
        Route::put('lineups/{id}', [LineupController::class, 'updateLineup']);
        Route::delete('lineups/{id}', [LineupController::class, 'deleteLineup']);

        //Route for bookingServices
        Route::post('bookings', [BookingController::class, 'createBooking']);
        Route::put('bookings/{id}', [BookingController::class, 'updateBooking']);
        Route::delete('bookings/{id}', [BookingController::class, 'deleteBooking']);

        //Route for shopServices
        Route::get('shops', [ShopController::class, 'getAllShops']);
        Route::post('shops/getAvailableTimeSlots/{id}', [ShopController::class, 'getAvailableTimeSlots']);
    }
);

//If the route is not found print this error
Route::fallback(function () {
    return response()->json(['state' => false, 'message' => 'Not found!'], 404);
});
