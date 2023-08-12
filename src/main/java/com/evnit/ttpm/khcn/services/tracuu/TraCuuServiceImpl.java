package com.evnit.ttpm.khcn.services.tracuu;

import com.evnit.ttpm.khcn.models.detai.DanhSachChung;
import com.evnit.ttpm.khcn.models.tracuu.TraCuuReq;
import com.evnit.ttpm.khcn.models.tracuu.TraCuuResp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class TraCuuServiceImpl implements TraCuuService {
    @Autowired
    NamedParameterJdbcTemplate jdbcTemplate;


    @Override
    public List<TraCuuResp> ListTraCuu(TraCuuReq traCuuReq, String page, String pageSize, String userId, String orgId) throws Exception {
        String whereDT = "";
        String whereSK = "";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if (traCuuReq.getLinhVucNghienCuu() != null && !traCuuReq.getLinhVucNghienCuu().isEmpty()) {
            whereDT += " AND dmnc.MA_LVUC_NCUU =:MA_LVUC_NCUU  AND dt.MA_DETAI IN(SELECT MA_DETAI FROM DT_DETAI_LVUC_NCUU WHERE MA_LVUC_NCUU =:MA_LVUC_NCUU)";
            whereSK += " AND dmnc.MA_LVUC_NCUU =:MA_LVUC_NCUU AND sk.MA_SANGKIEN IN(SELECT MA_SANGKIEN FROM SK_SANGKIEN_LVUC_NCUU WHERE MA_LVUC_NCUU =:MA_LVUC_NCUU)";
            parameters.addValue("MA_LVUC_NCUU", traCuuReq.getLinhVucNghienCuu());
        }
        if (traCuuReq.getCapQuanLy() != null && !traCuuReq.getCapQuanLy().isEmpty()) {
            whereDT += " AND cd.MA_CAPDO = :MA_CAPDO ";
            whereSK += " AND cd.MA_CAPDO = :MA_CAPDO";
            parameters.addValue("MA_CAPDO", traCuuReq.getCapQuanLy());
        }
        if (traCuuReq.getNam() != null && !traCuuReq.getNam().toString().isEmpty()) {
            whereDT += " AND YEAR(dt.THOI_GIAN_BAT_DAU) = :NAM ";
            whereSK += " AND YEAR(sk.NAM) = :NAM";
            parameters.addValue("NAM", traCuuReq.getNam());
        }
        if (traCuuReq.getTenDeTaiSK() != null && !traCuuReq.getTenDeTaiSK().isEmpty()) {
            whereDT += " AND dt.TEN_DETAI like :TEN_DETAI ";
            whereSK += " AND sk.TEN_SANGKIEN like :TEN_SANGKIEN";
            parameters.addValue("TEN_DETAI", "%" + traCuuReq.getTenDeTaiSK() + "%");
            parameters.addValue("TEN_SANGKIEN", "%" + traCuuReq.getTenDeTaiSK() + "%");
        }
        if (traCuuReq.getTenChuNhiemTG() != null && !traCuuReq.getTenChuNhiemTG().isEmpty()) {
            whereDT += " AND nth.TEN_NGUOI_THUC_HIEN like :TEN_NGUOI_THUC_HIEN ";
            whereSK += " AND nth.TEN_NGUOI_THUC_HIEN like :TEN_NGUOI_THUC_HIEN";
            parameters.addValue("TEN_NGUOI_THUC_HIEN", "%" + traCuuReq.getTenChuNhiemTG() + "%");
        }
        String queryString = "";
        if (traCuuReq.getHoatDongKhCN().equals("ALL") || traCuuReq.getHoatDongKhCN() == null || traCuuReq.getHoatDongKhCN().isEmpty()) {
            queryString = " SELECT * FROM (" +
                    " SELECT 'DETAI' loaiDeTaiSK, " +
                    " dt.TEN_DETAI tenDeTaiSK, " +
                    " dt.MA_DETAI maDeTaiSK, " +
                    " cd.TEN_CAPDO capQuanLy, " +
                    " nth.TEN_NGUOI_THUC_HIEN tenChuNhiemTG, " +
                    " dv.ORGDESC donViChuTri, " +
                    " YEAR(dt.THOI_GIAN_BAT_DAU) nam, " +
                    " dmnc.TEN_LVUC_NCUU tenNghienCuu " +
                    " FROM DT_DE_TAI dt" +
                    " LEFT JOIN DT_DM_CAPDO_DETAI cd ON dt.MA_CAPDO = cd.MA_CAPDO " +
                    " LEFT JOIN DT_DETAI_NGUOI_THUC_HIEN nth ON dt.MA_DETAI = nth.MA_DETAI AND nth.MA_CHUC_DANH='CNHIEM' " +
                    " LEFT JOIN S_ORGANIZATION dv ON dt.MA_DON_VI_CHU_TRI = dv.ORGID " +
                    " LEFT JOIN DT_DETAI_LVUC_NCUU nc ON dt.MA_DETAI = nc.MA_DETAI " +
                    " LEFT JOIN DM_LVUC_NCUU dmnc on dmnc.MA_LVUC_NCUU = nc.MA_LVUC_NCUU" +
                    " WHERE 1 = 1" +
                    whereDT +
                    " UNION ALL" +
                    " SELECT  'SANGKIEN' AS loaiDeTaiSK," +
                    " sk.TEN_SANGKIEN tenDeTaiSK, " +
                    " sk.MA_SANGKIEN maDeTaiSK, " +
                    " cd.TEN_CAPDO capQuanLy," +
                    " nth.TEN_NGUOI_THUC_HIEN tenChuNhiemTG, " +
                    " dv.ORGDESC donViChuTri," +
                    " sk.NAM nam, " +
                    " dmnc.TEN_LVUC_NCUU tenNghienCuu  " +
                    " FROM SK_SANGKIEN sk" +
                    " LEFT JOIN SK_DM_CAPDO_SANG_KIEN cd ON sk.MA_CAPDO = cd.MA_CAPDO" +
                    " LEFT JOIN SK_SANGKIEN_NGUOI_THUC_HIEN nth ON sk.MA_SANGKIEN = nth.MA_SANGKIEN AND nth.MA_CHUC_DANH='CNHIEM'" +
                    " LEFT JOIN S_ORGANIZATION dv ON sk.MA_DON_VI_DAU_TU = dv.ORGID" +
                    " LEFT JOIN SK_SANGKIEN_LVUC_NCUU nc ON sk.MA_SANGKIEN = nc.MA_SANGKIEN" +
                    " LEFT JOIN DM_LVUC_NCUU dmnc on dmnc.MA_LVUC_NCUU = nc.MA_LVUC_NCUU" +
                    " WHERE 1 = 1" +
                    whereSK +
                    " ) c ORDER BY tenDeTaiSK ASC OFFSET " + page + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
        } else if (traCuuReq.getHoatDongKhCN().equals("DE_TAI")) {
            queryString = " SELECT 'DETAI' loaiDeTaiSK, " +
                    " dt.TEN_DETAI tenDeTaiSK, " +
                    " dt.MA_DETAI maDeTaiSK, " +
                    " cd.TEN_CAPDO capQuanLy, " +
                    " nth.TEN_NGUOI_THUC_HIEN tenChuNhiemTG, " +
                    " dv.ORGDESC donViChuTri, " +
                    " YEAR(dt.THOI_GIAN_BAT_DAU) nam, " +
                    " dmnc.TEN_LVUC_NCUU tenNghienCuu " +
                    " FROM DT_DE_TAI dt" +
                    " LEFT JOIN DT_DM_CAPDO_DETAI cd ON dt.MA_CAPDO = cd.MA_CAPDO " +
                    " LEFT JOIN DT_DETAI_NGUOI_THUC_HIEN nth ON dt.MA_DETAI = nth.MA_DETAI AND nth.MA_CHUC_DANH='CNHIEM' " +
                    " LEFT JOIN S_ORGANIZATION dv ON dt.MA_DON_VI_CHU_TRI = dv.ORGID " +
                    " LEFT JOIN DT_DETAI_LVUC_NCUU nc ON dt.MA_DETAI = nc.MA_DETAI " +
                    " LEFT JOIN DM_LVUC_NCUU dmnc on dmnc.MA_LVUC_NCUU = nc.MA_LVUC_NCUU" +
                    " WHERE 1 = 1"
                    + whereDT
                    + " ORDER BY tenDeTaiSK ASC OFFSET " + page + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
        } else if (traCuuReq.getHoatDongKhCN().equals("SANG_KIEN")) {
            queryString = " SELECT  'SANGKIEN' AS loaiDeTaiSK," +
                    " sk.TEN_SANGKIEN tenDeTaiSK, " +
                    " sk.MA_SANGKIEN maDeTaiSK, " +
                    " cd.TEN_CAPDO capQuanLy," +
                    " nth.TEN_NGUOI_THUC_HIEN tenChuNhiemTG, " +
                    " dv.ORGDESC donViChuTri," +
                    " sk.NAM nam, " +
                    " dmnc.TEN_LVUC_NCUU tenNghienCuu  " +
                    " FROM SK_SANGKIEN sk" +
                    " LEFT JOIN SK_DM_CAPDO_SANG_KIEN cd ON sk.MA_CAPDO = cd.MA_CAPDO" +
                    " LEFT JOIN SK_SANGKIEN_NGUOI_THUC_HIEN nth ON sk.MA_SANGKIEN = nth.MA_SANGKIEN AND nth.MA_CHUC_DANH='CNHIEM'" +
                    " LEFT JOIN S_ORGANIZATION dv ON sk.MA_DON_VI_DAU_TU = dv.ORGID" +
                    " LEFT JOIN SK_SANGKIEN_LVUC_NCUU nc ON sk.MA_SANGKIEN = nc.MA_SANGKIEN" +
                    " LEFT JOIN DM_LVUC_NCUU dmnc on dmnc.MA_LVUC_NCUU = nc.MA_LVUC_NCUU" +
                    " WHERE 1 = 1"
                    + whereSK
                    + " ORDER BY tenDeTaiSK ASC OFFSET " + page + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
        }
        List<TraCuuResp> listObj = jdbcTemplate.query(queryString, parameters, BeanPropertyRowMapper.newInstance(TraCuuResp.class));
        return listObj;
    }


}
