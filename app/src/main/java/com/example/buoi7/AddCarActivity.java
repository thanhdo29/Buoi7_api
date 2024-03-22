package com.example.buoi7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buoi7.model.Car;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddCarActivity extends AppCompatActivity {

    EditText edtNameCar, edtYearCar, edtCompanyCar, edtPriceCar;
    TextView tvChoiceCar;
    MaterialButton btnAddCar;
    CarAdapter carAdapter;
    List<Car>carList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        edtNameCar=findViewById(R.id.edtNameCarAdd);
        edtYearCar=findViewById(R.id.edtYearCarAdd);
        edtCompanyCar=findViewById(R.id.edtCompanyCarAdd);
        edtPriceCar=findViewById(R.id.edtPriceCarAdd);
        tvChoiceCar=findViewById(R.id.tvChoiceImg);
        btnAddCar=findViewById(R.id.btnAddCar);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                // Set custom timeout values (in seconds)
                .client(new OkHttpClient.Builder()
                        .connectTimeout(30, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .writeTimeout(30, TimeUnit.SECONDS)
                        .build())
                .build();


        APIService apiService = retrofit.create(APIService.class);

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nameCar=edtNameCar.getText().toString().trim();
                String yearCar=edtYearCar.getText().toString().trim();
                String companyCar=edtCompanyCar.getText().toString().trim();
                String priceCar=edtPriceCar.getText().toString().trim();

                if (nameCar.isEmpty() || yearCar.isEmpty()|| companyCar.isEmpty()|| priceCar.isEmpty()){
                    Toast.makeText(AddCarActivity.this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                Car car=new Car();
                car.setTen(nameCar);
                car.setNamSx(Integer.parseInt(yearCar));
                car.setHang(companyCar);
                car.setGia(Double.parseDouble(priceCar));
                apiService.postCar(car).enqueue(new Callback<Car>() {
                    @Override
                    public void onResponse(Call<Car> call, Response<Car> response) {
                        if (response.isSuccessful()){
                            Car newCar=response.body();
                            carList.add(newCar);
                            carAdapter.notifyDataSetChanged();

                            Toast.makeText(AddCarActivity.this, "Thên thành công", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));

                        }
                    }

                    @Override
                    public void onFailure(Call<Car> call, Throwable t) {
                        Log.e("Add", t.getMessage());
                        Toast.makeText(AddCarActivity.this, "Thêm thất bại"+t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    }
}