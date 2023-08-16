package com.evnit.ttpm.khcn.models.kehoach;

import lombok.Data;

import java.util.Date;

@Data
public class KeHoach {
    private String maKeHoach;
    private String tenKeHoach;
    private Integer NAM;
    private String maDonVi;
    private String maTrangThai;
    private String nguoiTao;
    private Date ngayTao;
    private String nguoiSua;
    private Date ngaySua;
    private Boolean tongHop;
    private String  yKienNguoiPheDuyet;
    private String tenBangTongHop;
    private Integer kyTongHop;
    private String capTao;
}
