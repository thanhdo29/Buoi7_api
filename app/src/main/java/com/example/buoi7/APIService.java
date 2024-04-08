package com.example.buoi7;

import com.example.buoi7.model.Car;
import com.example.buoi7.model.User;

import java.util.List;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface APIService {

    String DOMAIN="http://192.168.53.9:3000";

    @GET("/api/list")
    Call<List<Car>> getCar();

    @Multipart
    @POST("/api/add_car")
    Call<Car> postCarWithImage(@Part("ten") RequestBody ten,
                               @Part("namSx") RequestBody namSx,
                               @Part("hang") RequestBody hang,
                               @Part("gia") RequestBody gia,
                               @Part MultipartBody.Part image);


    @PUT("/api/update_xe/{id}")
    Call<Car> updateCar(@Path("id") String carId, @Body Car car);

    @DELETE("/api/delete_xe/{id}")
    Call<Car> deleteCar(@Path("id") String id);

    @GET("/api/search-car")
    Call<List<Car>> searchCar(@Query("key") String key);

    @GET("/api/sort-car")
    Call<List<Car>> sortCar(@Query("sortBy") String sortBy);

    @Multipart
    @POST("/user/register")
    Call<User> registerAccount(@Part(Const.KEY_USERNAME) RequestBody username,
                               @Part(Const.KEY_PASSWORD) RequestBody password,
                               @Part MultipartBody.Part avatar);

    @POST("/user/login")
    Call<User> loginAccount(@Body User user);

}
