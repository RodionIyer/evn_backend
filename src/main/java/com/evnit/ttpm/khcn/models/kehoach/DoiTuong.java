package com.evnit.ttpm.khcn.models.kehoach;


public class DoiTuong {
    private String stt;
    private String hoatDong;
    private String nguonKinhPhi;
    private String kinhPhiDuKien;
    private String caNhanThucHien;
    private String noiDungHoatDong;
    private String thoiGianDuKien;

    public DoiTuong(String stt, String hoatDong, String nguonKinhPhi, String kinhPhiDuKien, String caNhanThucHien, String noiDungHoatDong, String thoiGianDuKien) {
        this.stt = stt;
        this.hoatDong = hoatDong;
        this.nguonKinhPhi = nguonKinhPhi;
        this.kinhPhiDuKien = kinhPhiDuKien;
        this.caNhanThucHien = caNhanThucHien;
        this.noiDungHoatDong = noiDungHoatDong;
        this.thoiGianDuKien = thoiGianDuKien;
    }

    public String getStt() {
        return stt;
    }

    public void setStt(String stt) {
        this.stt = stt;
    }

    public String getHoatDong() {
        return hoatDong;
    }

    public void setHoatDong(String hoatDong) {
        this.hoatDong = hoatDong;
    }

    public String getNguonKinhPhi() {
        return nguonKinhPhi;
    }

    public void setNguonKinhPhi(String nguonKinhPhi) {
        this.nguonKinhPhi = nguonKinhPhi;
    }

    public String getKinhPhiDuKien() {
        return kinhPhiDuKien;
    }

    public void setKinhPhiDuKien(String kinhPhiDuKien) {
        this.kinhPhiDuKien = kinhPhiDuKien;
    }

    public String getCaNhanThucHien() {
        return caNhanThucHien;
    }

    public void setCaNhanThucHien(String caNhanThucHien) {
        this.caNhanThucHien = caNhanThucHien;
    }

    public String getNoiDungHoatDong() {
        return noiDungHoatDong;
    }

    public void setNoiDungHoatDong(String noiDungHoatDong) {
        this.noiDungHoatDong = noiDungHoatDong;
    }

    public String getThoiGianDuKien() {
        return thoiGianDuKien;
    }

    public void setThoiGianDuKien(String thoiGianDuKien) {
        this.thoiGianDuKien = thoiGianDuKien;
    }
}
