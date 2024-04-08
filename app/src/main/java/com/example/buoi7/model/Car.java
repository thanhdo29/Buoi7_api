package com.example.buoi7.model;

import java.io.Serializable;

public class Car implements Serializable {
    private String _id;
    private String ten;
    private int namSx;
    private String hang;
    private double gia;
    private String imageUrl; // Thêm thuộc tính để lưu URL của ảnh

    public Car() {
    }

    public Car(String _id, String ten, int namSx, String hang, double gia, String imageUrl) {
        this._id = _id;
        this.ten = ten;
        this.namSx = namSx;
        this.hang = hang;
        this.gia = gia;
        this.imageUrl = imageUrl;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getTen() {
        return ten;
    }

    public void setTen(String ten) {
        this.ten = ten;
    }

    public int getNamSx() {
        return namSx;
    }

    public void setNamSx(int namSx) {
        this.namSx = namSx;
    }

    public String getHang() {
        return hang;
    }

    public void setHang(String hang) {
        this.hang = hang;
    }

    public double getGia() {
        return gia;
    }

    public void setGia(double gia) {
        this.gia = gia;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
