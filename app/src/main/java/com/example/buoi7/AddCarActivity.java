package com.example.buoi7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buoi7.model.Car;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddCarActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;

    private EditText edtNameCar, edtYearCar, edtCompanyCar, edtPriceCar;
    private Button btnAddCar;
    private ImageView imgFromGallery;
    private TextView tvChooseImg;

    private Uri mUri;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(APIService.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()
                    .connectTimeout(30, TimeUnit.SECONDS)
                    .readTimeout(30, TimeUnit.SECONDS)
                    .writeTimeout(30, TimeUnit.SECONDS)
                    .build())
            .build();

    APIService apiService = retrofit.create(APIService.class);

    private ActivityResultLauncher<Intent> mGetContentLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data=result.getData();
                        if (data==null){
                            return;
                        }
                        Uri uri = data.getData();
                        mUri = uri;
                        try {
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgFromGallery.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_car);

        edtNameCar = findViewById(R.id.edtNameCarAdd);
        edtYearCar = findViewById(R.id.edtYearCarAdd);
        edtCompanyCar = findViewById(R.id.edtCompanyCarAdd);
        edtPriceCar = findViewById(R.id.edtPriceCarAdd);
        btnAddCar = findViewById(R.id.btnAddCar);
        imgFromGallery = findViewById(R.id.img_from_gallery);
        tvChooseImg=findViewById(R.id.tvChoiceImgAdd);

        tvChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });

        btnAddCar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUri!= null){
                    addCar();
                }
            }
        });


    }

    private void addCar() {
        String nameCar = edtNameCar.getText().toString().trim();
        String yearCar = edtYearCar.getText().toString().trim();
        String companyCar = edtCompanyCar.getText().toString().trim();
        String priceCar = edtPriceCar.getText().toString().trim();

        if (nameCar.isEmpty() || yearCar.isEmpty() || companyCar.isEmpty() || priceCar.isEmpty()) {
            Toast.makeText(AddCarActivity.this, "Please enter all information", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mUri == null) {
            Toast.makeText(AddCarActivity.this, "Please select an image", Toast.LENGTH_SHORT).show();
            return;
        }

        // Convert URI to file
        String realPath = RealPathUtil.getRealPath(this, mUri);
        File file = new File(realPath);

        // Create request body from file
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // Create request body from other fields
        RequestBody nameRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), nameCar);
        RequestBody yearRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), yearCar);
        RequestBody companyRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), companyCar);
        RequestBody priceRequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), priceCar);

        // Send request to server
        apiService.postCarWithImage(nameRequestBody, yearRequestBody, companyRequestBody, priceRequestBody, imagePart)
                .enqueue(new Callback<Car>() {
                    @Override
                    public void onResponse(Call<Car> call, Response<Car> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AddCarActivity.this, "Car added successfully", Toast.LENGTH_SHORT).show();
                            updateCarListAndReturnToHome();
                        } else {
                            Toast.makeText(AddCarActivity.this, "Failed to add car 1", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Car> call, Throwable t) {
                        Log.e("AddCarActivity", "Error adding car: " + t.getMessage());
                        Toast.makeText(AddCarActivity.this, "Failed to add car 2", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mGetContentLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openGallery();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onClickRequestPermission() {
        if (Build.VERSION.SDK_INT<Build.VERSION_CODES.M){
            openGallery();
            return;
        }
        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)== PackageManager.PERMISSION_GRANTED){
            openGallery();
        }else {
            String [] permission={Manifest.permission.READ_EXTERNAL_STORAGE};
            requestPermissions(permission, 10);
        }

    }
    private void updateCarListAndReturnToHome() {
        apiService.getCar();
        Intent intent = new Intent(AddCarActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
