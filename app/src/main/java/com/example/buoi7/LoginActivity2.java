package com.example.buoi7;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.buoi7.model.User;
import com.google.android.material.button.MaterialButton;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity2 extends AppCompatActivity {
    EditText edtUserLoginNode, edtPassLoginNode;
    MaterialButton btnLoginNode, btnSignUpLoginNode;
    Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(APIService.DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .client(new OkHttpClient.Builder()

                    .build())
            .build();


    APIService apiService = retrofit.create(APIService.class);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        edtUserLoginNode = findViewById(R.id.edtUserLoginNode);
        edtPassLoginNode = findViewById(R.id.edtPassLoginNode);
        btnLoginNode = findViewById(R.id.btnLoginNode);
        btnSignUpLoginNode = findViewById(R.id.btnSignUpLoginNode);

        btnSignUpLoginNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getApplicationContext(), SignupActivity2.class);
                startActivity(intent);
            }
        });

        btnLoginNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                handleLogin();
            }
        });
    }

    private void handleLogin() {
        String userData=edtUserLoginNode.getText().toString().trim();
        String passData=edtPassLoginNode.getText().toString().trim();

        if (userData.isEmpty() || passData.isEmpty()){
            Toast.makeText(this, "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show();
            return;
        }else {
            User user=new User(userData, passData);
            Call<User>call=apiService.loginAccount(user);
            call.enqueue(new Callback<User>() {
                @Override
                public void onResponse(Call<User> call, Response<User> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(LoginActivity2.this, "Đăng nhập thành công", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(getApplicationContext(), HomeActivity.class);
                        startActivity(intent);
                    }else {
                        Toast.makeText(LoginActivity2.this, "Đăng nhập thất bại", Toast.LENGTH_SHORT).show();
                    }

                }

                @Override
                public void onFailure(Call<User> call, Throwable throwable) {
                    
                }
            });
        }
    }
}