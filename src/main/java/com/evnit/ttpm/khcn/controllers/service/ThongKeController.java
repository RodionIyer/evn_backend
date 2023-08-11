package com.evnit.ttpm.khcn.controllers.service;

import antlr.StringUtils;
import com.evnit.ttpm.khcn.models.detai.DanhSachChung;
import com.evnit.ttpm.khcn.models.kehoach.DanhSachMau;
import com.evnit.ttpm.khcn.models.kehoach.DonVi;
import com.evnit.ttpm.khcn.models.service.Api_Service_Input;
import com.evnit.ttpm.khcn.models.thongke.ListData;
import com.evnit.ttpm.khcn.models.thongke.ThongKeReq;
import com.evnit.ttpm.khcn.models.thongke.ThongKeResp;
import com.evnit.ttpm.khcn.models.tracuu.TraCuuReq;
import com.evnit.ttpm.khcn.models.tracuu.TraCuuResp;
import com.evnit.ttpm.khcn.payload.request.service.ExecServiceRequest;
import com.evnit.ttpm.khcn.payload.response.service.ExecServiceResponse;
import com.evnit.ttpm.khcn.security.services.SecurityUtils;
import com.evnit.ttpm.khcn.services.thongke.ThongKeService;
import com.evnit.ttpm.khcn.services.tracuu.TraCuuService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.Year;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ThongKeController {
    @Autowired
    ThongKeService thongKeService;
    @Autowired
    ExcelController excelController;

    public ExecServiceResponse ListDanhSach(ExecServiceRequest execServiceRequest) {
        try {

            String orgId = SecurityUtils.getPrincipal().getORGID();
            String userId = SecurityUtils.getPrincipal().getUserId();

            String loaiTimKiem = "";
            String page = "";
            String pagezise = "";
            ThongKeReq thongKeReq = new ThongKeReq();
            for (Api_Service_Input obj : execServiceRequest.getParameters()) {
                if ("TIM_KIEM".equals(obj.getName())) {
                    Gson gsons = new GsonBuilder().serializeNulls().create();
                    thongKeReq = gsons.fromJson(obj.getValue().toString(), ThongKeReq.class);
                    //break;
                } else if ("LOAI_TIM_KIEM".equals(obj.getName())) {
                    loaiTimKiem = obj.getValue().toString();
                    //break;
                } else if ("PAGE_NUM".equals(obj.getName())) {
                    page = obj.getValue().toString();
                    //break;
                } else if ("PAGE_ROW_NUM".equals(obj.getName())) {
                    pagezise = obj.getValue().toString();
                    //break;
                }
            }
            List<ThongKeResp> listObj = ListData(thongKeReq, loaiTimKiem, page, pagezise, userId, orgId, false);


            return new ExecServiceResponse(listObj, 1, "Danh sách thành công.");

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return new ExecServiceResponse(-1, "Thực hiện thất bại");
    }

    public List<ThongKeResp> ListData(ThongKeReq thongKeReq, String loaiTimKiem, String page, String pagezise, String userId, String orgId, boolean export) throws Exception {
        List<ThongKeResp> listObj = new ArrayList<>();
        if (loaiTimKiem != null && loaiTimKiem.equals("DETAI")) {
            if (thongKeReq.getLoaiThongKe() != null && thongKeReq.getLoaiThongKe().equals("KHOAHOC")) {
                List<ListData> listDeTai = thongKeService.ListThongKeDeTaiKH(thongKeReq, page, pagezise, userId, orgId, export);
                List<DanhSachChung> listLinhVucNC = thongKeService.ListLinhVucNC();
                if (listLinhVucNC != null && listLinhVucNC.size() > 0) {
                    for (DanhSachChung item : listLinhVucNC) {
                        ThongKeResp thongKe = new ThongKeResp();
                        List<ListData> listDeTaiKH = listDeTai.stream().filter(c -> c.getMaNghienCuu() != null && c.getMaNghienCuu().equals(item.getId())).collect(Collectors.toList());
                        if (listDeTaiKH != null && listDeTaiKH.size() > 0) {
                            thongKe.setTenLinhVuc(item.getName());
                            thongKe.setListData(listDeTaiKH);
                            listObj.add(thongKe);
                        }
                    }
                }
            } else if (thongKeReq.getLoaiThongKe() != null && thongKeReq.getLoaiThongKe().equals("CAPDETAI")) {
                List<ListData> listDeTai = thongKeService.ListThongKeCapDo(thongKeReq, page, pagezise, userId, orgId, export);
                List<DanhSachChung> listCapDo = thongKeService.ListCapDeTai();
                if (listCapDo != null && listCapDo.size() > 0) {
                    for (DanhSachChung item : listCapDo) {
                        ThongKeResp thongKe = new ThongKeResp();
                        List<ListData> listDeTaiKH = listDeTai.stream().filter(c -> c.getMaCapQuanLy() != null && c.getMaCapQuanLy().equals(item.getId())).collect(Collectors.toList());
                        if (listDeTaiKH != null && listDeTaiKH.size() > 0) {
                            thongKe.setTenLinhVuc(item.getName());
                            thongKe.setListData(listDeTaiKH);
                            listObj.add(thongKe);
                        }
                    }
                }
            } else if (thongKeReq.getLoaiThongKe() != null && thongKeReq.getLoaiThongKe().equals("CAPDONVI")) {
                List<ListData> listDeTai = thongKeService.ListThongKeDonVi(thongKeReq, page, pagezise, userId, orgId, export);
                List<DanhSachChung> listCapDonVi = thongKeService.ListCapDonVi();
                if (listCapDonVi != null && listCapDonVi.size() > 0) {
                    for (DanhSachChung item : listCapDonVi) {
                        ThongKeResp thongKe = new ThongKeResp();
                        List<ListData> listDeTaiKH = listDeTai.stream().filter(c -> c.getMaDonViChuTri() != null && c.getMaDonViChuTri().equals(item.getId())).collect(Collectors.toList());
                        if (listDeTaiKH != null && listDeTaiKH.size() > 0) {
                            thongKe.setTenLinhVuc(item.getName());
                            thongKe.setListData(listDeTaiKH);
                            listObj.add(thongKe);
                        }
                    }
                }
            }

        } else if (loaiTimKiem != null && loaiTimKiem.equals("SANGKIEN")) {
            if (thongKeReq.getLoaiThongKe() != null && thongKeReq.getLoaiThongKe().equals("KHOAHOC")) {
                List<ListData> listDeTai = thongKeService.ListThongKeSangKienKH(thongKeReq, page, pagezise, userId, orgId, export);
                List<DanhSachChung> listLinhVucNC = thongKeService.ListLinhVucNC();
                if (listLinhVucNC != null && listLinhVucNC.size() > 0) {
                    for (DanhSachChung item : listLinhVucNC) {
                        ThongKeResp thongKe = new ThongKeResp();
                        List<ListData> listDeTaiKH = listDeTai.stream().filter(c -> c.getMaNghienCuu() != null && c.getMaNghienCuu().equals(item.getId())).collect(Collectors.toList());
                        if (listDeTaiKH != null && listDeTaiKH.size() > 0) {
                            thongKe.setTenLinhVuc(item.getName());
                            thongKe.setListData(listDeTaiKH);
                            listObj.add(thongKe);
                        }
                    }
                }
            } else if (thongKeReq.getLoaiThongKe() != null && thongKeReq.getLoaiThongKe().equals("CAPDETAI")) {
                List<ListData> listDeTai = thongKeService.ListThongKeSangKienCapDo(thongKeReq, page, pagezise, userId, orgId, export);
                List<DanhSachChung> listCapDo = thongKeService.ListCapDeTai();
                if (listCapDo != null && listCapDo.size() > 0) {
                    for (DanhSachChung item : listCapDo) {
                        ThongKeResp thongKe = new ThongKeResp();
                        List<ListData> listDeTaiKH = listDeTai.stream().filter(c -> c.getMaCapQuanLy() != null && c.getMaCapQuanLy().equals(item.getId())).collect(Collectors.toList());
                        if (listDeTaiKH != null && listDeTaiKH.size() > 0) {
                            thongKe.setTenLinhVuc(item.getName());
                            thongKe.setListData(listDeTaiKH);
                            listObj.add(thongKe);
                        }
                    }
                }
            } else if (thongKeReq.getLoaiThongKe() != null && thongKeReq.getLoaiThongKe().equals("CAPDONVI")) {
                List<ListData> listDeTai = thongKeService.ListThongKeSangKienCapDonVi(thongKeReq, page, pagezise, userId, orgId, export);
                List<DanhSachChung> listCapDonVi = thongKeService.ListCapDonVi();
                if (listCapDonVi != null && listCapDonVi.size() > 0) {
                    for (DanhSachChung item : listCapDonVi) {
                        ThongKeResp thongKe = new ThongKeResp();
                        List<ListData> listDeTaiKH = listDeTai.stream().filter(c -> c.getMaDonViChuTri() != null && c.getMaDonViChuTri().equals(item.getId())).collect(Collectors.toList());
                        if (listDeTaiKH != null && listDeTaiKH.size() > 0) {
                            thongKe.setTenLinhVuc(item.getName());
                            thongKe.setListData(listDeTaiKH);
                            listObj.add(thongKe);
                        }
                    }
                }
            }
        }
        switch (Objects.requireNonNull(loaiTimKiem)){
            case "DETAI":
                addTotalRowDeTai(listObj);
                break;
            case "SANGKIEN":
                addTotalRowSangKien(listObj);
                break;
        }
        return listObj;
    }
    public void addTotalRowSangKien(List<ThongKeResp> listObj) {
        List<ListData> lstRowTotal = new ArrayList<>();
        Integer totalItem = 0;
        for (ThongKeResp item : listObj) {
            if (item.getListData().size() > 0) {
                List<ListData> lstData = item.getListData();
                BigDecimal sumSoTienLamLoi = BigDecimal.ZERO;
                BigDecimal sumThuLaoTacGia = BigDecimal.ZERO;
                BigDecimal sumThuLaoNguoiApDung = BigDecimal.ZERO;
                BigDecimal sumTongKinhPhi = BigDecimal.ZERO;
                for (ListData it : lstData) {
                    if (it.getSoTienLamLoi() != null && !it.getSoTienLamLoi().isEmpty()) {
                        sumSoTienLamLoi = sumSoTienLamLoi.add(new BigDecimal(it.getSoTienLamLoi()));
                    }
                    if (it.getThuLaoTacGia() != null && !it.getThuLaoTacGia().isEmpty()) {
                        sumThuLaoTacGia = sumThuLaoTacGia.add(new BigDecimal(it.getThuLaoTacGia()));
                    }
                    if (it.getThuLaoNguoiApDung() != null && !it.getThuLaoNguoiApDung().isEmpty()) {
                        sumThuLaoNguoiApDung = sumThuLaoNguoiApDung.add(new BigDecimal(it.getThuLaoNguoiApDung()));
                    }
                    if (it.getTongKinhPhi() != null && !it.getTongKinhPhi().isEmpty()) {
                        sumTongKinhPhi = sumTongKinhPhi.add(new BigDecimal(it.getTongKinhPhi()));
                    }
                }
                ListData rowTotal = new ListData();
                rowTotal.setDonViChuTri("Tổng cộng theo lĩnh vực: ");
                rowTotal.setSoTienLamLoi(sumSoTienLamLoi.toString());
                rowTotal.setThuLaoTacGia(sumThuLaoTacGia.toString());
                rowTotal.setThuLaoNguoiApDung(sumThuLaoNguoiApDung.toString());
                rowTotal.setTongKinhPhi(sumTongKinhPhi.toString());
                lstData.add(rowTotal);
                lstRowTotal.add(rowTotal);
                if(item.getTenLinhVuc().equals(listObj.get(listObj.size() - 1).getTenLinhVuc())){
                    if (lstRowTotal.size() > 0) {
                        BigDecimal lastSumSoTienLamLoi = BigDecimal.ZERO;
                        BigDecimal lastSumThuLaoTacGia = BigDecimal.ZERO;
                        BigDecimal lastSumThuLaoNguoiApDung = BigDecimal.ZERO;
                        BigDecimal lastSumTongKinhPhi = BigDecimal.ZERO;
                        for(ListData rowSum : lstRowTotal){
                            if(rowSum.getSoTienLamLoi() != null && !rowSum.getSoTienLamLoi().isEmpty()){
                                lastSumSoTienLamLoi = lastSumSoTienLamLoi.add( new BigDecimal(rowSum.getSoTienLamLoi()));
                            }
                            if(rowSum.getThuLaoTacGia() != null && !rowSum.getThuLaoTacGia().isEmpty()){
                                lastSumThuLaoTacGia = lastSumThuLaoTacGia.add( new BigDecimal(rowSum.getThuLaoTacGia()));
                            }
                            if(rowSum.getThuLaoNguoiApDung() != null && !rowSum.getThuLaoNguoiApDung().isEmpty()){
                                lastSumThuLaoNguoiApDung = lastSumThuLaoNguoiApDung.add( new BigDecimal(rowSum.getThuLaoNguoiApDung()));
                            }
                            if(rowSum.getTongKinhPhi() != null && !rowSum.getTongKinhPhi().isEmpty()){
                                lastSumTongKinhPhi = lastSumTongKinhPhi.add( new BigDecimal(rowSum.getTongKinhPhi()));
                            }
                        }
                        ListData lastRowTotal = new ListData();
                        lastRowTotal.setDonViChuTri("Tổng cộng: ");
                        lastRowTotal.setSoTienLamLoi(lastSumSoTienLamLoi.toString());
                        lastRowTotal.setThuLaoTacGia(lastSumThuLaoTacGia.toString());
                        lastRowTotal.setThuLaoNguoiApDung(lastSumThuLaoNguoiApDung.toString());
                        lastRowTotal.setTongKinhPhi(lastSumTongKinhPhi.toString());
                        lstData.add(lastRowTotal);
                    }
                }
                totalItem += lstData.size();
                item.setListData(lstData);
            }
            item.setTotalItem(totalItem);
        }
    }
    public void addTotalRowDeTai(List<ThongKeResp> listObj) {
        List<ListData> lstRowTotal = new ArrayList<>();
        Integer totalItem = 0;
        for (ThongKeResp item : listObj) {
            if (item.getListData().size() > 0) {
                List<ListData> lstData = item.getListData();
                BigDecimal sumKinhPhi = BigDecimal.ZERO;
                BigDecimal sumTongKinhPhi = BigDecimal.ZERO;
                for (ListData it : lstData) {
                    if (it.getKinhPhi() != null && !it.getKinhPhi().isEmpty()) {
                        sumKinhPhi = sumKinhPhi.add(new BigDecimal(it.getKinhPhi()));
                    }
                    if (it.getTongKinhPhi() != null && !it.getTongKinhPhi().isEmpty()) {
                        sumTongKinhPhi = sumTongKinhPhi.add(new BigDecimal(it.getTongKinhPhi()));
                    }
                }
                ListData rowTotal = new ListData();
                rowTotal.setDonViChuTri("Tổng cộng theo lĩnh vực: ");
                rowTotal.setKinhPhi(sumKinhPhi.toString());
                rowTotal.setTongKinhPhi(sumTongKinhPhi.toString());
                lstData.add(rowTotal);
                lstRowTotal.add(rowTotal);
                if(item.getTenLinhVuc().equals(listObj.get(listObj.size() - 1).getTenLinhVuc())){
                    if (lstRowTotal.size() > 0) {
                        BigDecimal lastSumKinhPhi = BigDecimal.ZERO;
                        BigDecimal lastSumTongKinhPhi = BigDecimal.ZERO;
                        for(ListData rowSum : lstRowTotal){
                            if(rowSum.getKinhPhi() != null && !rowSum.getKinhPhi().isEmpty()){
                                lastSumKinhPhi = lastSumKinhPhi.add( new BigDecimal(rowSum.getKinhPhi()));
                            }
                            if(rowSum.getTongKinhPhi() != null && !rowSum.getTongKinhPhi().isEmpty()){
                                lastSumTongKinhPhi = lastSumTongKinhPhi.add( new BigDecimal(rowSum.getTongKinhPhi()));
                            }
                        }
                        ListData lastRowTotal = new ListData();
                        lastRowTotal.setDonViChuTri("Tổng cộng: ");
                        lastRowTotal.setKinhPhi(lastSumKinhPhi.toString());
                        lastRowTotal.setTongKinhPhi(lastSumTongKinhPhi.toString());
                        lstData.add(lastRowTotal);
                    }
                }
                totalItem += lstData.size();
                item.setListData(lstData);
            }
            item.setTotalItem(totalItem);
        }
    }


    public ExecServiceResponse Export(ExecServiceRequest execServiceRequest) {
        try {

            String orgId = SecurityUtils.getPrincipal().getORGID();
            String userId = SecurityUtils.getPrincipal().getUserId();

            String loaiTimKiem = "";
            String page = "";
            String pagezise = "";
            ThongKeReq thongKeReq = new ThongKeReq();
            for (Api_Service_Input obj : execServiceRequest.getParameters()) {
                if ("TIM_KIEM".equals(obj.getName())) {
                    Gson gsons = new GsonBuilder().serializeNulls().create();
                    thongKeReq = gsons.fromJson(obj.getValue().toString(), ThongKeReq.class);
                    //break;
                } else if ("LOAI_TIM_KIEM".equals(obj.getName())) {
                    loaiTimKiem = obj.getValue().toString();
                    //break;
                }
            }
            List<ThongKeResp> listObj = ListData(thongKeReq, loaiTimKiem, page, pagezise, userId, orgId, true);
            String tieuDe = "Báo cáo - Thống kê hoạt động sáng kiến";
            if (loaiTimKiem != null && loaiTimKiem.equals("DETAI")) {
                tieuDe = "Báo cáo - Thống kê Đề tài/ nhiệm vụ";
            } else if (loaiTimKiem != null && loaiTimKiem.equals("SANGKIEN")) {
                tieuDe = "Báo cáo - Thống kê hoạt động sáng kiến";
            }


            return excelController.exportThongKe(listObj, tieuDe, loaiTimKiem, thongKeReq.getLoaiThongKe());

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return new ExecServiceResponse(-1, "Thực hiện thất bại");
    }


}
