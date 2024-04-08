package com.example.buoi7;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.buoi7.model.Car;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CarAdapter extends BaseAdapter {
    List<Car> carList;

    Context context;

    public CarAdapter(List<Car> carList, Context context) {
        this.carList = carList;
        this.context = context;
    }

    @Override
    public int getCount() {
        return carList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.item_car, parent, false);

        TextView tvName = (TextView) rowView.findViewById(R.id.tvTen);
        ImageView imgAvatar = (ImageView) rowView.findViewById(R.id.imgAvatatr);
        TextView tvNamsx = (TextView) rowView.findViewById(R.id.tvNamSx);

        TextView tvHang = (TextView) rowView.findViewById(R.id.tvHang);

        TextView tvGia= (TextView) rowView.findViewById(R.id.tvGia);

        Button btnUpdate=rowView.findViewById(R.id.btnUpdate);
        Button btnDelete=rowView.findViewById(R.id.btnDelete);

//        String imageUrl = mList.get(position).getThumbnailUrl();
//        Picasso.get().load(imageUrl).into(imgAvatar);
////        imgAvatar.setImageResource(imageId[position]);
        tvName.setText(String.valueOf(carList.get(position).getTen()));
        tvNamsx.setText(String.valueOf(carList.get(position).getNamSx()));

        tvHang.setText(String.valueOf(carList.get(position).getHang()));

        tvGia.setText(String.valueOf(carList.get(position).getGia()));

        String imageUrl = carList.get(position).getImageUrl();
        Picasso.get().load(imageUrl).into(imgAvatar);

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Car carToUpdate = carList.get(position);
                String carId = carList.get(position).get_id();
                int position = carList.indexOf(carToUpdate);


                Intent intent = new Intent(context, UpdateCarActivity.class);

                intent.putExtra("carToUpdate", carToUpdate);
                intent.putExtra("carId", carId);
                intent.putExtra("position", position);


                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                context.startActivity(intent);
            }
        });
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIService.DOMAIN)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        APIService apiService = retrofit.create(APIService.class);


        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                apiService.deleteCar(carList.get(position).get_id()).enqueue(new Callback<Car>() {
                    @Override
                    public void onResponse(Call<Car> call, Response<Car> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(context, "Xóa thành công", Toast.LENGTH_SHORT).show();
                            carList.remove(position);
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onFailure(Call<Car> call, Throwable t) {
                        Toast.makeText(context, "Xóa thất bại", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        return rowView;
    }
}
