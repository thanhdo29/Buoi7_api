package com.example.buoi7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buoi7.model.Car;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class HomeActivity extends AppCompatActivity {

    ListView lvCar;

    List<Car> list;
    CarAdapter carAdapter;
    FloatingActionButton btnAdd;
    EditText edtSearch;

    Spinner spinnerSort;
    Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(APIService.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    APIService apiService = retrofit.create(APIService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        lvCar=findViewById(R.id.lvCar);
        btnAdd=findViewById(R.id.btnAddCar);
        edtSearch=findViewById(R.id.edtSearch);
        spinnerSort=findViewById(R.id.spinnerSort);
        list=new ArrayList<>();

        getData();



        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.sort_options, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSort.setAdapter(adapter);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                String sortBy;
                switch (position) {
                    case 0:
                        sortBy = "asc";
                        break;
                    case 1:
                        sortBy = "desc";
                        break;
                    default:
                        sortBy = "";
                }
                if (!sortBy.isEmpty()) {
                    apiService.sortCar(sortBy).enqueue(new Callback<List<Car>>() {
                        @Override
                        public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                            if (response.isSuccessful()) {
                                list.clear();
                                List<Car> sortedCars = response.body();
                                if (sortedCars != null) {
                                    list.addAll(sortedCars);
                                    carAdapter.notifyDataSetChanged();

                                }
                            } else {
                                Toast.makeText(HomeActivity.this, "Không thể sắp xếp", Toast.LENGTH_SHORT).show();
                                Log.e("Sort", "Unsuccessful response: " + response.code());
                            }
                        }

                        @Override
                        public void onFailure(Call<List<Car>> call, Throwable throwable) {
                            Toast.makeText(HomeActivity.this, "Lỗi khi sắp xếp", Toast.LENGTH_SHORT).show();
                            Log.e("Sort", "Sort request failed: " + throwable.getMessage());
                        }
                    });

                }
                getData();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });



        edtSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = edtSearch.getText().toString().trim();

                    if (!key.isEmpty()) {
                        apiService.searchCar(key).enqueue(new Callback<List<Car>>() {
                            @Override
                            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                                if (response.isSuccessful()) {
                                    list.clear();

                                    List<Car> searchResults = response.body();

                                    if (searchResults != null && !searchResults.isEmpty()) {
                                        list.addAll(searchResults);
                                        carAdapter.notifyDataSetChanged();
                                        getData();

                                    } else {
                                        Toast.makeText(HomeActivity.this, "Không tìm thấy kết quả", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Toast.makeText(HomeActivity.this, "Không thể tìm kiếm", Toast.LENGTH_SHORT).show();
                                    Log.e("Search", "Unsuccessful response: " + response.code());
                                }
                            }

                            @Override
                            public void onFailure(Call<List<Car>> call, Throwable t) {
                                Toast.makeText(HomeActivity.this, "Lỗi khi tìm kiếm", Toast.LENGTH_SHORT).show();
                                Log.e("Search", "Search request failed: " + t.getMessage());
                            }
                        });
                        return true;
                    }
                }
                return false;
            }
        });


        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddCarActivity.class));
            }
        });



    }
    private void getData(){
        Call<List<Car>> call = apiService.getCar();

        call.enqueue(new Callback<List<Car>>() {
            @Override
            public void onResponse(Call<List<Car>> call, Response<List<Car>> response) {
                if (response.isSuccessful()){
                    list=response.body();

                    carAdapter=new CarAdapter(list,getApplicationContext());

                    lvCar.setAdapter(carAdapter);
                }
            }

            @Override
            public void onFailure(Call<List<Car>> call, Throwable t) {
                Log.e("Main", t.getMessage());
            }
        });
    }

}