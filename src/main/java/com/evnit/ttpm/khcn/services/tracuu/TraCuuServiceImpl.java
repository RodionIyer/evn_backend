package com.evnit.ttpm.khcn.services.tracuu;

import com.evnit.ttpm.khcn.models.detai.DanhSachChung;
import com.evnit.ttpm.khcn.models.tracuu.TraCuuReq;
import com.evnit.ttpm.khcn.models.tracuu.TraCuuResp;
import com.evnit.ttpm.khcn.util.Util;
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
    public List<TraCuuResp> ListTraCuu(TraCuuReq traCuuReq, String page, String pageSize,String userId,String orgId) throws Exception {
        String whereDT=" WHERE 1=1 ";
        String whereSK =" WHERE 1=1 ";

        MapSqlParameterSource parameters = new MapSqlParameterSource();
        if(Util.isNotEmpty(traCuuReq.getLinhVucNghienCuu())){
             whereDT +=" AND dt.MA_DETAI IN (SELECT MA_DETAI FROM DT_DETAI_LVUC_NCUU WHERE MA_LVUC_NCUU = :MA_LVUC_NCUU) ";
             whereSK +=" AND sk.MA_SANGKIEN IN (SELECT MA_SANGKIEN FROM SK_SANGKIEN_LVUC_NCUU WHERE MA_LVUC_NCUU = :MA_LVUC_NCUU) ";
            parameters.addValue("MA_LVUC_NCUU",traCuuReq.getLinhVucNghienCuu());
        }
        if(Util.isNotEmpty(traCuuReq.getCapQuanLy())){
            whereDT +=" AND dt.MA_CAPDO = :MA_CAPDO ";
            whereSK +=" AND sk.MA_CAPDO = :MA_CAPDO ";
            parameters.addValue("MA_CAPDO",traCuuReq.getCapQuanLy());
        }

        if(Util.isNotEmpty(traCuuReq.getNam())){
            whereDT +=" AND  YEAR(dt.THOI_GIAN_BAT_DAU) =:NAM ";
            whereSK +=" AND sk.NAM = :NAM ";
            parameters.addValue("NAM",traCuuReq.getNam());
        }
        if(Util.isNotEmpty(traCuuReq.getTenDeTaiSK())){
            whereDT +=" AND dt.TEN_DETAI LIKE :TENDTSK ";
            whereSK +=" AND sk.TEN_SANGKIEN LIKE :TENDTSK ";
            parameters.addValue("TENDTSK","%"+traCuuReq.getTenDeTaiSK()+"%");
        }

        if(Util.isNotEmpty(traCuuReq.getTenChuNhiemTG())){
            whereDT +=" AND dt.MA_DETAI IN (SELECT MA_DETAI FROM DT_DETAI_NGUOI_THUC_HIEN WHERE MA_CHUC_DANH='CNHIEM' AND TEN_NGUOI_THUC_HIEN LIKE :TEN_NGUOI_THUC_HIEN)";
            whereSK +=" AND sk.MA_SANGKIEN IN (SELECT MA_SANGKIEN FROM SK_SANGKIEN_NGUOI_THUC_HIEN WHERE TEN_NGUOI_THUC_HIEN LIKE :TEN_NGUOI_THUC_HIEN) ";
            parameters.addValue("TEN_NGUOI_THUC_HIEN","%"+traCuuReq.getTenChuNhiemTG()+"%");
        }

        String queryString = "SELECT * FROM (" +
                " SELECT 'DETAI' loaiDeTaiSK,dt.MA_DETAI maDeTaiSK, dt.TEN_DETAI tenDeTaiSK, cd.TEN_CAPDO capQuanLy,nth.TEN_NGUOI_THUC_HIEN tenChuNhiemTG, dv.ORGDESC donViChuTri,YEAR(dt.THOI_GIAN_BAT_DAU) nam  FROM DT_DE_TAI dt" +
                " LEFT JOIN DT_DM_CAPDO_DETAI cd ON dt.MA_CAPDO = cd.MA_CAPDO" +
                " LEFT JOIN DT_DETAI_NGUOI_THUC_HIEN nth ON dt.MA_DETAI = nth.MA_DETAI AND nth.MA_CHUC_DANH='CNHIEM'" +
                " LEFT JOIN S_ORGANIZATION dv ON dt.MA_DON_VI_CHU_TRI = dv.ORGID" +
                whereDT +
                " UNION ALL" +
                " SELECT  'SANGKIEN' AS loaiDeTaiSK,sk.MA_SANGKIEN maDeTaiSK,sk.TEN_SANGKIEN tenDeTaiSK, cd.TEN_CAPDO capQuanLy,nth.TEN_NGUOI_THUC_HIEN tenChuNhiemTG, dv.ORGDESC donViChuTri,sk.NAM nam FROM SK_SANGKIEN sk" +
                " LEFT JOIN SK_DM_CAPDO_SANG_KIEN cd ON sk.MA_CAPDO = cd.MA_CAPDO" +
                " LEFT JOIN SK_SANGKIEN_NGUOI_THUC_HIEN nth ON sk.MA_SANGKIEN = nth.MA_SANGKIEN AND nth.MA_CHUC_DANH='CNHIEM'" +
                " LEFT JOIN S_ORGANIZATION dv ON sk.MA_DON_VI_DAU_TU = dv.ORGID" +
                whereSK +
                " ) c WHERE 1=1 ";
        if(Util.isNotEmpty(traCuuReq.getHoatDongKhCN())){
            queryString +=" AND loaiDeTaiSK= :loaiDeTaiSK";
            parameters.addValue("loaiDeTaiSK",traCuuReq.getHoatDongKhCN());
        }
        queryString +=" ORDER BY tenDeTaiSK ASC OFFSET " +(Util.toInt(page) * Util.toInt(pageSize)) + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";

        List<TraCuuResp> listObj = jdbcTemplate.query(queryString, parameters, BeanPropertyRowMapper.newInstance(TraCuuResp.class));
        return listObj;
    }


}
