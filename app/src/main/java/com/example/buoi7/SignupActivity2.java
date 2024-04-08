package com.example.buoi7;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buoi7.model.User;
import com.google.android.material.button.MaterialButton;

import java.io.File;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SignupActivity2 extends AppCompatActivity {

    private static final int MY_REQUEST_CODE = 10;
    private EditText edtUsername, edtPass,edtRePass;
    private TextView tvChooseImg;
    private ImageView imgFromGallery;
    private MaterialButton btnSignup;

    private Uri mUri;

    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(APIService.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()

                    .build())
            .build();


    APIService apiService = retrofit.create(APIService.class);
    private ActivityResultLauncher<Intent> mActivityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    Log.e("App", "onActivityRsult");
                    if (o.getResultCode()== Activity.RESULT_OK){
                        Intent data=o.getData();
                        if (data==null){
                            return;
                        }
                        Uri uri=data.getData();
                        mUri=uri;
                        try {
                            Bitmap bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            imgFromGallery.setImageBitmap(bitmap);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }
            }
    );
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup2);
        edtUsername=findViewById(R.id.edtEmailNode);
        edtPass=findViewById(R.id.edtPassNode);
        edtRePass=findViewById(R.id.edtRePassNode);
        btnSignup=findViewById(R.id.btnSignupNode);
        imgFromGallery=findViewById(R.id.img_from_gallery);
        tvChooseImg=findViewById(R.id.tvChooseImg);
        btnSignup=findViewById(R.id.btnSignupNode);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUri!= null){
                    registerAccount();
                }
            }
        });



        tvChooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickRequestPermission();
            }
        });
    }

    private void registerAccount() {
        String userData=edtUsername.getText().toString().trim();
        String passData=edtPass.getText().toString().trim();
        String rePassData=edtRePass.getText().toString().trim();

        if (userData.isEmpty()|| passData.isEmpty() || rePassData.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
        }else {
            if (!rePassData.equals(passData)){
                Log.e("Trung", "Sai");
                Toast.makeText(this, "Mật khẩu không trùng nhau", Toast.LENGTH_SHORT).show();
                return;
            }
        }


        RequestBody requestBodyUsername=RequestBody.create(MediaType.parse("multipart/form-data"),userData);
        RequestBody requestBodyPass=RequestBody.create(MediaType.parse("multipart/form-data"),passData);


        String strRealPath=RealPathUtil.getRealPath(this, mUri);
        File file=new File(strRealPath);
        RequestBody requestBody=RequestBody.create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part multipathBody=MultipartBody.Part.createFormData(Const.KEY_AVATAR,file.getName(),requestBody);
        apiService.registerAccount(requestBodyUsername,requestBodyPass,multipathBody).enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                if (response.isSuccessful()){
                    Toast.makeText(SignupActivity2.this, "Đăng ki thành công", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<User> call, Throwable throwable) {
                Toast.makeText(SignupActivity2.this, "Đăng kí không thành công", Toast.LENGTH_SHORT).show();
            }
        });
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
            requestPermissions(permission, MY_REQUEST_CODE);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode==MY_REQUEST_CODE){
            if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent=new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        mActivityResultLauncher.launch(Intent.createChooser(intent, "Select Picture"));
    }
}