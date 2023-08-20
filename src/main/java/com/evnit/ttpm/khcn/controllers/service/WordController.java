package com.evnit.ttpm.khcn.controllers.service;


import com.aspose.words.*;
import com.aspose.words.Font;
import com.evnit.ttpm.khcn.models.detai.DanhSachThanhVien;
import com.evnit.ttpm.khcn.models.detai.Folder;
import com.evnit.ttpm.khcn.models.kehoach.*;
import com.evnit.ttpm.khcn.models.sangkien.SangKienModel;
import com.evnit.ttpm.khcn.models.sangkien.SangKienResp;
import com.evnit.ttpm.khcn.models.service.Api_Service_Input;
import com.evnit.ttpm.khcn.payload.request.service.ExecServiceRequest;
import com.evnit.ttpm.khcn.payload.response.service.ExecServiceResponse;
import com.evnit.ttpm.khcn.security.services.SecurityUtils;
import com.evnit.ttpm.khcn.services.kehoach.ExcelService;
import com.evnit.ttpm.khcn.services.sangkien.SangKienService;
import com.evnit.ttpm.khcn.util.Util;
import com.microsoft.sqlserver.jdbc.StringUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.text.NumberFormat;
import java.time.Year;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class WordController {

    @Autowired
    ExcelService excelService;
    @Autowired
    SangKienController sangKienController;



    public String KeHoach(String maKeHoach,String orgId) {
        try {
            List<DanhSachMau> listDanhSachMau = excelService.getListDanhsachMau();
            List<KeHoachChiTiet> listKeHoachChiTiet = new ArrayList<>();
            List<NguonKinhPhi> listNguonKinhPhi = new ArrayList<>();
            DonVi donVi = new DonVi();
            int nam= Year.now().getValue();
            if (!StringUtils.isEmpty(maKeHoach)) {
                KeHoach kehoach = excelService.getFirstMaKeHoach(maKeHoach);
                //List<DonVi> listDonVi = excelService.getListDonVi(kehoach.getMA_DON_VI());
                nam = kehoach.getNAM();
                donVi = excelService.getFirstDonVi(kehoach.getMaDonVi());
                listKeHoachChiTiet = excelService.getListKeHoachChiTiet(maKeHoach);
                listNguonKinhPhi = excelService.getListNguonKinhPhi();
            }else{
                donVi = excelService.getFirstDonVi(orgId);
            }
            Double tongDuToan = 0D;
            if(listKeHoachChiTiet != null && listKeHoachChiTiet.size() >0){
                try{
                List<KeHoachChiTiet> listKeHoachChiTietTotal = listKeHoachChiTiet.stream().filter(c ->  Util.isNotEmpty(c.getDU_TOAN())).collect(Collectors.toList());
                if(listKeHoachChiTietTotal != null && listKeHoachChiTietTotal.size() >0){
                    List<String> listTotal = listKeHoachChiTietTotal.stream().map(KeHoachChiTiet::getDU_TOAN).collect(Collectors.toList());
                    List<Double>  listTotalInt = listTotal.stream().map(Double::parseDouble).collect(Collectors.toList());
                    tongDuToan = listTotalInt.stream().mapToDouble(Double::doubleValue).sum();
                }
                }catch (Exception ex){}
            }
            // Tạo một tài liệu mới
            Document doc = new Document();

            // Tạo đối tượng DocumentBuilder
            DocumentBuilder builder = new DocumentBuilder(doc);

            PageSetup pageSetup = builder.getPageSetup();
            pageSetup.setOrientation(Orientation.LANDSCAPE);

//            Paragraph centeredParagraph = new Paragraph(doc);
////            centeredParagraph.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
////            Run centeredRun = new Run(doc, "BIỂU MẪU ĐĂNG KÝ ĐỊNH HƯỚNG HOẠT ĐỘNG KHCN");
////            Font centeredFont = centeredRun.getFont();
////            centeredFont.setName("Times New Roman");
////            centeredFont.setSize(14);
////            centeredFont.setBold(true);
////            centeredFont.setColor(Color.BLACK);
////            centeredParagraph.appendChild(centeredRun);
////            doc.getFirstSection().getBody().appendChild(centeredParagraph);
////
////            doc.getFirstSection().getBody().appendChild(new Paragraph(doc));
////
////            Paragraph leftAlignedParagraph = new Paragraph(doc);
////            leftAlignedParagraph.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
////            Run leftRun = new Run(doc, "Tên Đơn vị: " + tenDonVi + "                 Đăng ký định hướng hoạt động KHCN năm: " + nam);
////            Font leftFont = leftRun.getFont();
////            leftFont.setName("Times New Roman");
////            leftFont.setSize(12);
////            leftFont.setBold(true);
////            leftFont.setColor(Color.BLACK);
////            leftAlignedParagraph.appendChild(leftRun);
////            doc.getFirstSection().getBody().appendChild(leftAlignedParagraph);


            // Thêm đoạn văn bản
            builder.getParagraphFormat().setLineSpacingRule(LineSpacingRule.MULTIPLE);
            builder.getParagraphFormat().setLineSpacing(14.15);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
            Font font = builder.getFont();
            font.setSize(14);
            font.setBold(true);
            font.setColor(Color.BLACK);
            font.setName("Times New Roman");
            builder.writeln("BIỂU MẪU ĐĂNG KÝ ĐỊNH HƯỚNG HOẠT ĐỘNG KHCN");
            builder.writeln();
            font.setSize(12);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
            builder.writeln("Tên Đơn vị: " + donVi.getTenNhom() + "                Đăng ký định hướng hoạt động KHCN năm: " + nam);
            builder.writeln("Tổng dự toán (đồng): "+Util.formatNumberVn(tongDuToan) +" đồng");
            builder.writeln();
            builder.writeln();

            Table table = builder.startTable();
            builder.insertCell();
            table.autoFit(AutoFitBehavior.AUTO_FIT_TO_CONTENTS);
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
            builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
            builder.write("STT");
            builder.insertCell();
            builder.write("Hoạt động");
            builder.insertCell();
            builder.write("Nguồn kinh phí (EVN hoặc Đơn vị)");
            builder.insertCell();
           // builder.write("Kinh phí dự kiến (đồng)");
            builder.write("Dự toán (Triệu đồng)");
            builder.insertCell();
            builder.write("Đơn vị chủ trì");
            builder.insertCell();
            builder.write("Chủ nhiệm nhiệm vụ");
            builder.insertCell();
            builder.write("Nội dung hoạt động");
            builder.insertCell();
            builder.write("Thời gian dự kiến thực hiện (Từ tháng/năm đến tháng/năm)");
            builder.endRow();
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
            int i=1;
            List<DanhSachMau>  listDanhSachMauCha = listDanhSachMau.stream().filter(c -> c.getMA_NHOM_CHA()==null).collect(Collectors.toList());
            for(DanhSachMau item :listDanhSachMauCha){
                List<DanhSachMau>  listDanhSachMauChild =listDanhSachMau.stream().filter(c -> c.getMA_NHOM_CHA() != null && c.getMA_NHOM_CHA().equals(item.getMA_NHOM())).collect(Collectors.toList());
                Double tong =0D;
                List<String>  listDanhSachMauChildMa = listDanhSachMauChild.stream().map(DanhSachMau::getMA_NHOM).collect(Collectors.toList());
                List<KeHoachChiTiet>  listKeHoachChiTietView = listKeHoachChiTiet.stream().filter(c -> listDanhSachMauChildMa.contains(c.getMA_NHOM())).collect(Collectors.toList());
                if(listKeHoachChiTietView != null && listKeHoachChiTietView.size() >0){
                    try{
                        List<KeHoachChiTiet> listKeHoachChiTietTotal = listKeHoachChiTietView.stream().filter(c -> Util.isNotEmpty(c.getDU_TOAN())).collect(Collectors.toList());
                        if(listKeHoachChiTietTotal != null && listKeHoachChiTietTotal.size() >0){
                            List<String> listTotal = listKeHoachChiTietTotal.stream().map(KeHoachChiTiet::getDU_TOAN).collect(Collectors.toList());
                            List<Double>  listTotalInt = listTotal.stream().map(Double::parseDouble).collect(Collectors.toList());
                            tong = listTotalInt.stream().mapToDouble(Double::doubleValue).sum();
                        }
                    }catch (Exception ex){}
                }
                builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
                font.setBold(true);
                builder.insertCell();
                builder.write(i+"");
                builder.insertCell();
                builder.write(item.getTEN_NHOM());
                builder.insertCell();
                builder.write("");
                builder.insertCell();
               // builder.write(Util.formatNumber(tong));
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.endRow();
                if(listDanhSachMauChild != null && listDanhSachMauChild.size() >0){
                    int j =1;

                    for(DanhSachMau itemChild:listDanhSachMauChild){
                        builder.setItalic(true);
                        font.setBold(true);
                        builder.insertCell();
                        builder.write(i+"."+j);
                        builder.insertCell();
                        builder.write(itemChild.getTEN_NHOM());
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.endRow();
                        //String s = NumberFormat.getInstance(Locale.GERMANY).format(tong);
                        //builder.write(tongs);
                        j++;
                        if(listKeHoachChiTiet != null && listKeHoachChiTiet.size() >0){
                            listKeHoachChiTietView = listKeHoachChiTiet.stream().filter(c -> c.getMA_NHOM().equals(itemChild.getMA_NHOM())).collect(Collectors.toList());
                            if(listKeHoachChiTietView != null && listKeHoachChiTietView.size() >0){
                                for (KeHoachChiTiet chiTiet : listKeHoachChiTietView){
                                    String tenNguonKinhPhi="";
                                    if(Util.isNotEmpty(chiTiet.getMA_NGUON_KINH_PHI())){
                                      List<NguonKinhPhi> listNguonKp =  listNguonKinhPhi.stream().filter(c -> c.getMA_NGUON_KINH_PHI().equals(chiTiet.getMA_NGUON_KINH_PHI())).collect(Collectors.toList());
                                      if(listNguonKp != null && listNguonKp.size() >0){
                                          tenNguonKinhPhi = listNguonKp.get(0).getTEN_NGUON_KINH_PHI();
                                      }
                                    }
                                    builder.setItalic(false);
                                    font.setBold(false);
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write(chiTiet.getNOI_DUNG_DANG_KY());
                                    builder.insertCell();
                                    builder.write(tenNguonKinhPhi);
                                    builder.insertCell();
                                    builder.write(Util.formatNumberVn(chiTiet.getDU_TOAN()));
                                    builder.insertCell();
                                    builder.write(chiTiet.getDON_VI_CHU_TRI());
                                    builder.insertCell();
                                    builder.write(chiTiet.getCHU_NHIEM_NHIEM_VU());
                                    builder.insertCell();
                                    builder.write(chiTiet.getNOI_DUNG());
                                    builder.insertCell();
                                    builder.write(chiTiet.getTHOI_GIAN_THUC_HIEN());
                                    builder.endRow();
                                }
                            }

                        }
                    }
                }
                i++;
            }
//            for (DoiTuong doiTuong : list) {
//                if (Util.isNotEmpty(doiTuong.getStt())) {
//                    if (doiTuong.getStt().contains(".")){
//                        builder.setItalic(true);
//                    }
//                    builder.setBold(true);
//                } else {
//                    builder.setBold(false);
//                }
//                builder.insertCell();
//                builder.write(doiTuong.getStt());
//                builder.insertCell();
//                builder.write(doiTuong.getHoatDong());
//                builder.setItalic(false);
//                builder.insertCell();
//                builder.write(doiTuong.getNguonKinhPhi());
//                builder.insertCell();
//                builder.write(doiTuong.getKinhPhiDuKien());
//                builder.insertCell();
//                builder.write(doiTuong.getCaNhanThucHien());
//                builder.insertCell();
//                builder.write(doiTuong.getNoiDungHoatDong());
//                builder.insertCell();
//                builder.write(doiTuong.getThoiGianDuKien());
//                builder.endRow();
//            }
            builder.endTable();

            // Loại bỏ đoạn văn bản trắng đầu tiên
            if (doc.getFirstSection().getBody().getFirstParagraph().getText().trim().isEmpty()) {
                doc.getFirstSection().getBody().getFirstParagraph().remove();
            }

            // Lưu tài liệu thành tệp Word
            String path = System.getProperty("user.dir");
            UUID uuid = UUID.randomUUID();
            String fileName = "xuat_bieu_mau_" + uuid + ".docx";
             String pathSave = path + "/" + fileName;
            doc.save(pathSave);
           return pathSave;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }
    public String KeHoachTongHop(String maKeHoach,String orgId) {
        try {
            List<DanhSachMau> listDanhSachMau = excelService.getListDanhsachMau();
            List<KeHoachChiTiet> listKeHoachChiTiet = new ArrayList<>();
            List<NguonKinhPhi> listNguonKinhPhi = new ArrayList<>();

            DonVi donVi = new DonVi();
            int nam= Year.now().getValue();
            if (!StringUtils.isEmpty(maKeHoach)) {
                KeHoach kehoach = excelService.getFirstMaKeHoach(maKeHoach);
                //List<DonVi> listDonVi = excelService.getListDonVi(kehoach.getMA_DON_VI());
                nam = kehoach.getNAM();
                donVi = excelService.getFirstDonVi(kehoach.getMaDonVi());
                listKeHoachChiTiet = excelService.getListKeHoachChiTiet(maKeHoach);
                listNguonKinhPhi = excelService.getListNguonKinhPhi();
            }else{
                donVi = excelService.getFirstDonVi(orgId);
            }
            Double tongDuToan = 0D;
            if(listKeHoachChiTiet != null && listKeHoachChiTiet.size() >0){
                try{
                    List<KeHoachChiTiet> listKeHoachChiTietTotal = listKeHoachChiTiet.stream().filter(c -> Util.isNotEmpty(c.getDU_TOAN())).collect(Collectors.toList());
                    if(listKeHoachChiTietTotal != null && listKeHoachChiTietTotal.size() >0){
                        List<String> listTotal = listKeHoachChiTietTotal.stream().map(KeHoachChiTiet::getDU_TOAN).collect(Collectors.toList());
                        List<Double>  listTotalInt = listTotal.stream().map(Double::parseDouble).collect(Collectors.toList());
                        tongDuToan = listTotalInt.stream().mapToDouble(Double::doubleValue).sum();
                    }
                }catch (Exception ex){}
            }
            // Tạo một tài liệu mới
            Document doc = new Document();

            // Tạo đối tượng DocumentBuilder
            DocumentBuilder builder = new DocumentBuilder(doc);

            PageSetup pageSetup = builder.getPageSetup();
            pageSetup.setOrientation(Orientation.LANDSCAPE);

            // Thêm đoạn văn bản
            builder.getParagraphFormat().setLineSpacingRule(LineSpacingRule.MULTIPLE);
            builder.getParagraphFormat().setLineSpacing(14.15);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.CENTER);
            Font font = builder.getFont();
            font.setSize(14);
            font.setBold(true);
            font.setColor(Color.BLACK);
            font.setName("Times New Roman");
            builder.writeln("BIỂU MẪU ĐĂNG KÝ ĐỊNH HƯỚNG HOẠT ĐỘNG KHCN");
            builder.writeln();
            font.setSize(12);
            builder.getParagraphFormat().setAlignment(ParagraphAlignment.LEFT);
            builder.writeln("Tên Đơn vị: " + donVi.getTenNhom() + "                Đăng ký định hướng hoạt động KHCN năm: " + nam);
            builder.writeln("Tổng dự toán (đồng): "+Util.formatNumberVn(tongDuToan) +" đồng");
            builder.writeln();
            builder.writeln();

            Table table = builder.startTable();
            builder.insertCell();
            table.autoFit(AutoFitBehavior.AUTO_FIT_TO_CONTENTS);
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
            builder.getCellFormat().getShading().setBackgroundPatternColor(Color.LIGHT_GRAY);
            builder.write("STT");
            builder.insertCell();
            builder.write("Hoạt động");
            builder.insertCell();
            builder.write("Nguồn kinh phí (EVN hoặc Đơn vị)");
            builder.insertCell();
           // builder.write("Kinh phí dự kiến (đồng)");
            builder.write("Dự toán (Triệu đồng)");
            builder.insertCell();
            builder.write("Đơn vị chủ trì");
            builder.insertCell();
            builder.write("Chủ nhiệm nhiệm vụ");
            builder.insertCell();
            builder.write("Nội dung hoạt động");
            builder.insertCell();
            builder.write("Thời gian dự kiến thực hiện (Từ tháng/năm đến tháng/năm)");
            builder.endRow();
            builder.getCellFormat().getShading().setBackgroundPatternColor(Color.WHITE);
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.TOP);
            int i=1;
            List<DanhSachMau>  listDanhSachMauCha = listDanhSachMau.stream().filter(c -> c.getMA_NHOM_CHA()==null).collect(Collectors.toList());
            for(DanhSachMau item :listDanhSachMauCha){
                List<DanhSachMau>  listDanhSachMauChild =listDanhSachMau.stream().filter(c ->c.getMA_NHOM_CHA() != null && c.getMA_NHOM_CHA().equals(item.getMA_NHOM())).collect(Collectors.toList());
                Double tong =0D;
                List<String>  listDanhSachMauChildMa = listDanhSachMauChild.stream().map(DanhSachMau::getMA_NHOM).collect(Collectors.toList());
                List<KeHoachChiTiet>  listKeHoachChiTietView = listKeHoachChiTiet.stream().filter(c -> listDanhSachMauChildMa.contains(c.getMA_NHOM())).collect(Collectors.toList());
                if(listKeHoachChiTietView != null && listKeHoachChiTietView.size() >0){
                    try{
                        List<KeHoachChiTiet> listKeHoachChiTietTotal = listKeHoachChiTietView.stream().filter(c -> c.getDU_TOAN() != null).collect(Collectors.toList());
                        if(listKeHoachChiTietTotal != null && listKeHoachChiTietTotal.size() >0){
                            List<String> listTotal = listKeHoachChiTietTotal.stream().map(KeHoachChiTiet::getDU_TOAN).collect(Collectors.toList());
                            List<Double>  listTotalInt = listTotal.stream().map(Double::parseDouble).collect(Collectors.toList());
                            tong = listTotalInt.stream().mapToDouble(Double::doubleValue).sum();
                        }
                    }catch (Exception ex){}
                }
                font.setBold(true);
                builder.insertCell();
                builder.write(i+"");
                builder.insertCell();
                builder.write(item.getTEN_NHOM());
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
               // builder.write(Util.formatNumberVn(tong));
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write("");
                builder.endRow();
                if(listDanhSachMauChild != null && listDanhSachMauChild.size() >0){
                    int j =1;

                    for(DanhSachMau itemChild:listDanhSachMauChild){

                        builder.setItalic(true);
                        font.setBold(true);
                        builder.insertCell();
                        builder.write(i+"."+j);
                        builder.insertCell();
                        builder.write(itemChild.getTEN_NHOM());
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.insertCell();
                        builder.write("");
                        builder.endRow();
                        //String s = NumberFormat.getInstance(Locale.GERMANY).format(tong);
                        //builder.write(tongs);
                        j++;
                        if(listKeHoachChiTiet != null && listKeHoachChiTiet.size() >0){
                            builder.setItalic(false);
                            builder.setBold(false);
                            List<KeHoachChiTiet> listKeHoachChiTietNotDonVi = listKeHoachChiTiet.stream().filter(c -> c.getMA_NHOM().equals(itemChild.getMA_NHOM()) && (c.getMA_DON_VI()==null || c.getMA_DON_VI().equals(orgId))).collect(Collectors.toList());
                            List<KeHoachChiTiet> listKeHoachChiTietDonVi = listKeHoachChiTiet.stream().filter(c -> c.getMA_NHOM().equals(itemChild.getMA_NHOM()) && c.getMA_DON_VI() !=null && !c.getMA_DON_VI().equals(orgId)).collect(Collectors.toList());

                            //listKeHoachChiTietView.sort(Comparator.comparing(KeHoachChiTiet::getMA_DON_VI));
                            List<String> listDonVi = listKeHoachChiTietDonVi.stream().map(KeHoachChiTiet::getMA_DON_VI).collect(Collectors.toList());

                          List<DonVi> listDonViView =  excelService.getListDonViByListMaDonVi(listDonVi);
                            if(listKeHoachChiTietNotDonVi != null && listKeHoachChiTietNotDonVi.size() >0){
                                for (KeHoachChiTiet chiTiet : listKeHoachChiTietNotDonVi){
                                    String tenNguonKinhPhi="";
                                    if(Util.isNotEmpty(chiTiet.getMA_NGUON_KINH_PHI())){
                                        List<NguonKinhPhi> listNguonKp =  listNguonKinhPhi.stream().filter(c -> c.getMA_NGUON_KINH_PHI().equals(chiTiet.getMA_NGUON_KINH_PHI())).collect(Collectors.toList());
                                        if(listNguonKp != null && listNguonKp.size() >0){
                                            tenNguonKinhPhi = listNguonKp.get(0).getTEN_NGUON_KINH_PHI();
                                        }
                                    }
                                    builder.setItalic(false);
                                    font.setBold(false);
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write(chiTiet.getNOI_DUNG_DANG_KY());
                                    builder.insertCell();
                                    builder.write(tenNguonKinhPhi);
                                    builder.insertCell();
                                    builder.write(Util.formatNumberVn(chiTiet.getDU_TOAN()));
                                    builder.insertCell();
                                    builder.write(chiTiet.getDON_VI_CHU_TRI());
                                    builder.insertCell();
                                    builder.write(chiTiet.getCHU_NHIEM_NHIEM_VU());
                                    builder.insertCell();
                                    builder.write(chiTiet.getNOI_DUNG());
                                    builder.insertCell();
                                    builder.write(chiTiet.getTHOI_GIAN_THUC_HIEN());
                                    builder.endRow();
                                }
                            }
                            int k=1;
                            if(listDonViView != null && listDonViView.size() >0){
                                builder.setItalic(true);
                                font.setBold(true);
                                for(DonVi dv :listDonViView){
                                    builder.insertCell();
                                    builder.write(i+"."+j+"."+k);
                                    builder.insertCell();
                                    builder.write(dv.getTenNhom());
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write("");
                                    builder.insertCell();
                                    builder.write("");
                                    builder.endRow();
                                    k++;
                                    List<KeHoachChiTiet>   listKeHoachChiTietDonVi2 =listKeHoachChiTietDonVi.stream().filter(c -> c.getMA_DON_VI().equals(dv.getMaNhom())).collect(Collectors.toList());
                                    if(listKeHoachChiTietDonVi2 != null && listKeHoachChiTietDonVi2.size() >0){
                                        for (KeHoachChiTiet chiTiet : listKeHoachChiTietDonVi2){
                                            String tenNguonKinhPhi="";
                                            if(Util.isNotEmpty(chiTiet.getMA_NGUON_KINH_PHI())){
                                                List<NguonKinhPhi> listNguonKp =  listNguonKinhPhi.stream().filter(c -> c.getMA_NGUON_KINH_PHI().equals(chiTiet.getMA_NGUON_KINH_PHI())).collect(Collectors.toList());
                                                if(listNguonKp != null && listNguonKp.size() >0){
                                                    tenNguonKinhPhi = listNguonKp.get(0).getTEN_NGUON_KINH_PHI();
                                                }
                                            }
                                            builder.setItalic(false);
                                            font.setBold(false);
                                            builder.insertCell();
                                            builder.write("");
                                            builder.insertCell();
                                            builder.write(chiTiet.getNOI_DUNG_DANG_KY());
                                            builder.insertCell();
                                            builder.write(tenNguonKinhPhi);
                                            builder.insertCell();
                                            builder.write(Util.formatNumberVn(chiTiet.getDU_TOAN()));
                                            builder.insertCell();
                                            builder.write(chiTiet.getDON_VI_CHU_TRI());
                                            builder.insertCell();
                                            builder.write(chiTiet.getCHU_NHIEM_NHIEM_VU());
                                            builder.insertCell();
                                            builder.write(chiTiet.getNOI_DUNG());
                                            builder.insertCell();
                                            builder.write(chiTiet.getTHOI_GIAN_THUC_HIEN());
                                            builder.endRow();
                                        }
                                    }
                                }
                            }

                        }
                    }
                }
                i++;
            }
            builder.endTable();

            // Loại bỏ đoạn văn bản trắng đầu tiên
            if (doc.getFirstSection().getBody().getFirstParagraph().getText().trim().isEmpty()) {
                doc.getFirstSection().getBody().getFirstParagraph().remove();
            }

            // Lưu tài liệu thành tệp Word
            String path = System.getProperty("user.dir");
            UUID uuid = UUID.randomUUID();
            String fileName = "xuat_bieu_mau_" + uuid + ".docx";
            String pathSave = path + "/" + fileName;
            doc.save(pathSave);
            return pathSave;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }


    public ExecServiceResponse XuatMau(ExecServiceRequest execServiceRequest){
        String msg = "Xuất thành công";
        try{
            String userId = SecurityUtils.getPrincipal().getUserId();
            String orgId = SecurityUtils.getPrincipal().getORGID();
            String maSK ="";
            for (Api_Service_Input obj : execServiceRequest.getParameters()) {
                if ("MA_SANGKIEN".equals(obj.getName())) {
                    maSK = obj.getValue().toString();
                }
            }

            String pathSave =  SangKien(maSK,orgId);
            if (Util.isNotEmpty(pathSave)) {
                File file = new File(pathSave);
                byte[] fileContent = Files.readAllBytes(file.toPath());
                String fileBase64 = Base64.getEncoder().encodeToString(fileContent);
                boolean result2 = Files.deleteIfExists(file.toPath());
                return new ExecServiceResponse(fileBase64, 1, "Thành công.");
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return new ExecServiceResponse(-1, "Thực hiện thất bại");
    }

    public String SangKien(String maSk,String orgId) {
        try {
            SangKienResp sangKienResp = sangKienController.chiTietSua(maSk);

            Document doc = new Document();
// Create a DocumentBuilder object
            DocumentBuilder builder = new DocumentBuilder(doc);
// Specify font formatting
            Font font = builder.getFont();
            font.setSize(13);
            font.setBold(true);
            font.setColor(Color.BLACK);
            font.setName("Times New Roman");
            ParagraphFormat paragraphFormat = builder.getParagraphFormat();
            paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
            builder.writeln("CỘNG HOÀ XÃ HỘI CHỦ NGHĨA VIỆT NAM");
            builder.writeln("Độc lập - Tự do - Hạnh phúc");
            builder.insertBreak(BreakType.LINE_BREAK);
            builder.writeln("ĐƠN YÊU CẦU CÔNG NHẬN SÁNG KIẾN");
            builder.insertBreak(BreakType.LINE_BREAK);


            //paragraphFormat.setSpaceBefore(1);
            // paragraphFormat = builder.getParagraphFormat();
            // paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
            builder.write("Kính gửi:");
            font.setBold(false);
            builder.writeln(" Tập đoàn Điện lực Việt Nam/Tổng công ty...../Công ty.......");
            builder.writeln();
            //  builder.insertBreak(BreakType.LINE_BREAK);
            // paragraphFormat.setLeftIndent(10);
            paragraphFormat = builder.getParagraphFormat();
            paragraphFormat.setAlignment(ParagraphAlignment.LEFT);
            builder.writeln("\tCăn cứ Quy chế Quản lý hoạt động khoa học và công nghệ trong Tập đoàn Điện lực Việt Nam ban hành kèm theo Quyết định số..../QĐ-EVN ngày .... tháng .... năm .... của Hội đồng Thành viên Tập đoàn Điện lực Việt Nam");
            builder.writeln("\tTôi (chúng tôi) có tên dưới đây:");
            builder.writeln();

           // FormatDefault(builder);
            Table table = builder.startTable();
            builder.insertCell();
            table.autoFit(AutoFitBehavior.AUTO_FIT_TO_WINDOW);
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
            font.setBold(true);
            builder.write("STT");
            builder.insertCell();
            builder.write("Họ và tên");
            builder.insertCell();
            builder.write("Vai trò");
            builder.insertCell();
            builder.write("Năm sinh");
            builder.insertCell();
            builder.write("Nơi công tác");
            builder.insertCell();
            builder.write("Trình độ chuyên môn");
            builder.insertCell();
            builder.write("Nội dung tham gia");
// End row
            builder.endRow();
            font.setBold(false);
            if(sangKienResp != null && sangKienResp.getDanhSachThanhVien() != null && sangKienResp.getDanhSachThanhVien().size() >0){
                int i=1;
                    for(DanhSachThanhVien item :sangKienResp.getDanhSachThanhVien()){
                        builder.insertCell();
                        builder.write(i+"");
                        builder.insertCell();
                        builder.write(item.getTen());
                        builder.insertCell();
                        builder.write(item.getTenChucDanh());
                        builder.insertCell();
                        builder.write(item.getNamSinh()+"");
                        builder.insertCell();
                        builder.write(item.getDiaChiNoiLamViec());
                        builder.insertCell();
                        builder.write(item.getThanhTuu());
                        builder.insertCell();
                        builder.write(item.getNoiDungThamGia());
                        builder.endRow();
                        i++;
                    }
            }else{
                List<SangKienModel> list = new ArrayList<>();
                SangKienModel obj = new SangKienModel();
                obj.setHoTen("");
                obj.setVaiTro("");
                obj.setNam("");
                obj.setDonViCongTac("");
                obj.setTrinhDoChuyenMon("");
                obj.setNoiDung("");
                list.add(obj);

                obj = new SangKienModel();
                obj.setHoTen("");
                obj.setVaiTro("");
                obj.setNam("");
                obj.setDonViCongTac("");
                obj.setTrinhDoChuyenMon("");
                obj.setNoiDung("");
                list.add(obj);
                for(SangKienModel item :list){
                    // builder.getRowFormat().setHeight(100);
//                builder.getRowFormat().setHeightRule(HeightRule.EXACTLY);
                    builder.insertCell();
                    builder.write("");
                    builder.insertCell();
                    builder.write(item.getHoTen());
                    builder.insertCell();
                    builder.write(item.getVaiTro());
                    builder.insertCell();
                    builder.write(item.getNam());
                    builder.insertCell();
                    builder.write(item.getDonViCongTac());
                    builder.insertCell();
                    builder.write(item.getTrinhDoChuyenMon());
                    builder.insertCell();
                    builder.write(item.getNoiDung());
                    builder.endRow();
                }
            }
// start a next row and set its properties
//            List<SangKienModel> list = new ArrayList<>();
//            SangKienModel obj = new SangKienModel();
//            obj.setHoTen("Nguyễn Văn Tuấn");
//            obj.setNam("2023");
//            obj.setDonViCongTac("Bảo Việt");
//            obj.setTrinhDoChuyenMon("Đại học");
//            obj.setNoiDung("100%");
//            list.add(obj);
//
//            obj = new SangKienModel();
//            obj.setHoTen("Nguyễn Văn B");
//            obj.setNam("2024");
//            obj.setDonViCongTac("Bảo Việt");
//            obj.setTrinhDoChuyenMon("Đại học");
//            obj.setNoiDung("100%");
//            list.add(obj);
//
//            int i=1;
//            for(SangKienModel item :list){
//                // builder.getRowFormat().setHeight(100);
////                builder.getRowFormat().setHeightRule(HeightRule.EXACTLY);
//                builder.insertCell();
//                builder.write(i+"");
//                builder.insertCell();
//                builder.write(item.getHoTen());
//                builder.insertCell();
//                builder.write(item.getNam());
//                builder.insertCell();
//                builder.write(item.getDonViCongTac());
//                builder.insertCell();
//                builder.write(item.getTrinhDoChuyenMon());
//                builder.insertCell();
//                builder.write(item.getNoiDung());
//                builder.endRow();
//                i++;
//            }

// End table
            builder.endTable();
            builder.writeln();
            builder.writeln("\tĐề nghị xét công nhận giải pháp sau đây là sáng kiến:........");
            builder.writeln("\tChủ đầu tư tạo ra sáng kiến:"+sangKienResp.getTenCapDoSangKien());
            font.setBold(true);
            builder.writeln("\tA. Mô tả giải pháp:");
            builder.writeln("\t1. Tình trạng kỹ thuật hoặc tổ chức sản xuất hiện tại, chỉ rõ ưu khuyết điểm của giải pháp kỹ thuật, giải pháp tổ chức sản xuất hiện đang được áp dụng tại EVN / Đơn vị");
            font.setBold(false);
            builder.writeln(sangKienResp.getUuNhuocDiem());
            font.setBold(true);
            builder.writeln("\t2. Nội dung giải pháp đề nghị công nhận là sáng kiến:");
            font.setBold(false);
            builder.writeln("\t2.1. Mục đích của giải pháp");
            builder.writeln("\t........");
            builder.writeln("\t2.2. Bản chất chi tiết của giải pháp");
            builder.writeln("\t........");
            builder.writeln("\t2.3. Các bước thực hiện giải pháp");
            builder.writeln("\t........");
            builder.writeln("\t2.4. Những nội dung cải tiến, khắc phục được nhược điểm");
            builder.writeln("\t........");
            font.setBold(true);
            builder.writeln("\t3. Quá trình áp dụng giải pháp trên thực tiễn hoặc áp dụng thử:");
            font.setBold(false);
            builder.writeln("\t"+sangKienResp.getQuaTrinhApDung());
            font.setBold(true);
            builder.writeln("\t4. Hiệu quả thực tế thu được khi áp dụng giải pháp:");
            font.setBold(false);
            builder.writeln("\t"+sangKienResp.getHieuQuaThucTe());
            font.setBold(true);
            builder.writeln("\tB. Số tiền làm lợi trong năm đầu tiên áp dụng giải pháp:");
            font.setBold(false);
            builder.writeln("\t"+Util.formatNumber(sangKienResp.getSoTienLamLoi()));
            font.setBold(true);
            builder.writeln("\tC. Danh mục tài liệu nộp kèm báo cáo:");
            font.setBold(false);
            if(sangKienResp != null && sangKienResp.getListFolderFile() != null ){
                for(Folder folder :sangKienResp.getListFolderFile()){

                    String file ="";
                    if(folder.getListFile() != null && folder.getListFile().size() >0){
                        List<String> listFile = folder.getListFile().stream().map(FileReq::getFileName).collect(Collectors.toList());
                        file = String.join(", ", listFile);
                    }
                    builder.writeln("\t"+folder.getFileName()+":"+file);
                }
            }else{
                builder.writeln("\t........");
            }

            font.setBold(true);
            builder.writeln("\tD. Danh sách Những người tham gia tổ chức áp dụng sáng kiến lần đầu (nếu có): ");
            font.setBold(false);
            builder.writeln();
            table = builder.startTable();
            builder.insertCell();
            table.autoFit(AutoFitBehavior.AUTO_FIT_TO_WINDOW);
            builder.getCellFormat().setVerticalAlignment(CellVerticalAlignment.CENTER);
            font.setBold(true);
            builder.write("TT");
            builder.insertCell();
            builder.write("Họ và tên");
            builder.insertCell();
            builder.write("Năm sinh");
            builder.insertCell();
            builder.write("Nơi công tác (hoặc nơi ở)");
            builder.insertCell();
            builder.write("Trình độ chuyên môn");
            builder.insertCell();
            builder.write("Nội dung tham gia");
// End row
            builder.endRow();
            font.setBold(false);
// start a next row and set its properties
            List<SangKienModel> list = new ArrayList<>();
            SangKienModel obj = new SangKienModel();
            obj.setHoTen("");
            obj.setNam("");
            obj.setDonViCongTac("");
            obj.setTrinhDoChuyenMon("");
            obj.setNoiDung("");
            list.add(obj);

            obj = new SangKienModel();
            obj.setHoTen("");
            obj.setNam("");
            obj.setDonViCongTac("");
            obj.setTrinhDoChuyenMon("");
            obj.setNoiDung("");
            list.add(obj);
            for(SangKienModel item :list){
                // builder.getRowFormat().setHeight(100);
//                builder.getRowFormat().setHeightRule(HeightRule.EXACTLY);
                builder.insertCell();
                builder.write("");
                builder.insertCell();
                builder.write(item.getHoTen());
                builder.insertCell();
                builder.write(item.getNam());
                builder.insertCell();
                builder.write(item.getDonViCongTac());
                builder.insertCell();
                builder.write(item.getTrinhDoChuyenMon());
                builder.insertCell();
                builder.write(item.getNoiDung());
                builder.endRow();
            }

// End table
            builder.endTable();
            int rowIndex1 = 1;
            for (Row row : table.getRows()) {
                if (rowIndex1 >= 2) {
                    int cellIndex = 1;
                    for (Cell cell : row.getCells()) {
                        if (cellIndex == 2) {
                            // Truy cập định dạng đoạn văn bản trong ô
                            Paragraph paragraph = cell.getFirstParagraph();
                            ParagraphFormat paragraphFormat2 = paragraph.getParagraphFormat();

                            // Đặt căn chỉnh ngang của đoạn văn bản trong ô
                            paragraphFormat2.setAlignment(ParagraphAlignment.LEFT);
                        }
                        cellIndex++;
                    }
                }
                rowIndex1++;
            }

            builder.writeln("\tChúng tôi cam đoan những điều trình bày trong đơn này là đúng sự thật.");
            builder.setItalic(true);
            builder.writeln("\t\t\t\t  .........., ngày ..... tháng ..... năm .......");
            builder.setItalic(false);
            font.setBold(true);
            builder.writeln("\t\t\t\t\t         Nhóm tác giả");
            font.setBold(false);
            font.setItalic(true);
            builder.writeln("\t\t\t\t\t      (Họ tên và chữ ký)");
            font.setItalic(false);
            builder.writeln();
            builder.writeln();
            font.setBold(true);
            builder.writeln(" Họ và tên \t\t\t\t\t\t\tChữ ký");
            font.setBold(false);
            builder.writeln();
            builder.writeln(" ...............................\t\t\t.......................................................................................");
            builder.writeln();
            builder.writeln(" ...............................\t\t\t.......................................................................................");
            builder.writeln();
            builder.writeln(" ...............................\t\t\t.......................................................................................");
//            paragraphFormat = builder.getParagraphFormat();
//            paragraphFormat.setAlignment(ParagraphAlignment.CENTER);
//            builder.writeln("Chúng tôi cam đoan những điều trình bày trong đơn này là đúng sự thật.");
//            builder.writeln();
//            paragraphFormat = builder.getParagraphFormat();
//            paragraphFormat.setAlignment(ParagraphAlignment.RIGHT);
//            builder.writeln("....., ngày..... tháng ...... năm .....\t\t\t");
//            font.setBold(true);
//            builder.writeln("Nhóm tác giả\t\t\t\t\t");
//            font.setBold(false);
//            font.setItalic(true);
//            builder.writeln("(Họ tên và chữ ký)\t\t\t");
//
//            builder.writeln();
//            builder.writeln();
//            builder.writeln();
//            font.setItalic(false);
//            paragraphFormat = builder.getParagraphFormat();
//            paragraphFormat.setAlignment(ParagraphAlignment.LEFT);
//            builder.write("Họ tên");
//            paragraphFormat = builder.getParagraphFormat();
//            paragraphFormat.setAlignment(ParagraphAlignment.RIGHT);
//            builder.writeln("Chữ ký");
//            paragraphFormat = builder.getParagraphFormat();
//            paragraphFormat.setAlignment(ParagraphAlignment.LEFT);
//            builder.write(".............");
//            paragraphFormat = builder.getParagraphFormat();
//            paragraphFormat.setAlignment(ParagraphAlignment.RIGHT);
//            builder.writeln("...........");

            // Lưu tài liệu thành tệp Word
            String path = System.getProperty("user.dir");
            UUID uuid = UUID.randomUUID();
            String fileName = "xuat_bieu_mau_" + uuid + ".docx";
            String pathSave = path + "/" + fileName;
            doc.save(pathSave);
            return pathSave;
        } catch (Exception ex) {
            System.out.printf(ex.getMessage());
        }
        return null;
    }


}
