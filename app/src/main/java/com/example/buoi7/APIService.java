package com.example.buoi7;

import com.example.buoi7.model.Car;

import java.util.List;


import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface APIService {

    String DOMAIN="http://192.168.1.103:3000/";

    @GET("/api/list")
    Call<List<Car>> getCar();

    @POST("/api/add_xe")
    Call<Car> postCar(@Body Car car);

    @PUT("/api/update_xe/{id}")
    Call<Car> updateCar(@Path("id") String carId, @Body Car car);

    @DELETE("/api/delete_xe/{id}")
    Call<Car> deleteCar(@Path("id") String id);
}
