package com.example.buoi7;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buoi7.model.Car;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class UpdateCarActivity extends AppCompatActivity {

    EditText edtNameCar, edtYearCar, edtCompanyCar, edtPriceCar;
    TextView tvChoiceCar;
    MaterialButton btnUpdateCar;
    List<Car> carList = new ArrayList<>();
    CarAdapter carAdapter;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(APIService.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .build())
            .build();


    APIService apiService = retrofit.create(APIService.class);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_car);
        carAdapter = new CarAdapter(carList, getApplicationContext());

        edtNameCar=findViewById(R.id.edtNameCarUpdate);
        edtYearCar=findViewById(R.id.edtYearCarUpdate);
        edtCompanyCar=findViewById(R.id.edtCompanyCarUpdate);
        edtPriceCar=findViewById(R.id.edtPriceCarUpdate);
        tvChoiceCar=findViewById(R.id.tvChoiceImgUpdate);
        btnUpdateCar=findViewById(R.id.btnUpdateCar);


        Car carToUpdate = (Car) getIntent().getSerializableExtra("carToUpdate");

        if (carToUpdate != null) {
            edtNameCar.setText(carToUpdate.getTen());
            edtYearCar.setText(String.valueOf(carToUpdate.getNamSx()));
            edtCompanyCar.setText(carToUpdate.getHang());
            edtPriceCar.setText(String.valueOf(carToUpdate.getGia()));
        }


        btnUpdateCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String carId = getIntent().getStringExtra("carId");



                String nameCar=edtNameCar.getText().toString();
                String yearCar=edtYearCar.getText().toString();
                String companyCar=edtCompanyCar.getText().toString();
                String priceCar=edtPriceCar.getText().toString();

                if (nameCar.isEmpty() || yearCar.isEmpty()|| companyCar.isEmpty()|| priceCar.isEmpty()){
                    Toast.makeText(UpdateCarActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }
                Car car = new Car();
                car.setTen(nameCar);
                car.setNamSx(Integer.parseInt(yearCar));
                car.setHang(companyCar);
                car.setGia(Double.parseDouble(priceCar));

                apiService.updateCar(carId,car).enqueue(new Callback<Car>() {
                    @Override
                    public void onResponse(Call<Car> call, Response<Car> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(UpdateCarActivity.this, "Cập nhật thành công", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onFailure(Call<Car> call, Throwable t) {
                        Log.e("Update", "Lỗi update: " + t.getMessage());
                        Toast.makeText(UpdateCarActivity.this, "Cập nhật thất bại: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });



            }
        });


    }

}