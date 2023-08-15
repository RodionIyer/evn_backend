package com.evnit.ttpm.khcn.services.tonghopfile;

import com.evnit.ttpm.khcn.models.kehoach.FileUpload;
import com.evnit.ttpm.khcn.models.tonghopfile.FileBase64;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TongHopFileServiceImpl implements TongHopFileService {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public TongHopFileServiceImpl(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<FileBase64> get10RecordsFromDtDetailFileAndKhKeHoachFileAndSkSangKienFile() {
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        String queryString = "SELECT TOP(10) TABLE_NAME, MA_FILE, TEN_FILE, FILE_BASE64 " +
                "FROM ((SELECT 'DT_DETAI_FILE' AS TABLE_NAME, MA_FILE, TEN_FILE, FILE_BASE64 FROM DT_DETAI_FILE WHERE FILE_BASE64 is not null) " +
                "UNION ALL " +
                "(SELECT 'KH_KE_HOACH_FILE' AS TABLE_NAME, MA_FILE, TEN_FILE, FILE_BASE64 FROM KH_KE_HOACH_FILE WHERE FILE_BASE64 is not null) " +
                "UNION ALL " +
                "(SELECT 'SK_SANGKIEN_FILE' AS TABLE_NAME, MA_FILE, TEN_FILE, FILE_BASE64 FROM SK_SANGKIEN_FILE WHERE FILE_BASE64 is not null)) AS DATA;";
        return jdbcTemplate.query(queryString, parameters, BeanPropertyRowMapper.newInstance(FileBase64.class));
    }

    @Override
    public int updateFile(String tableName, String maFile, FileUpload file) {
        String queryString = "UPDATE  [" + tableName + "] " +
                "SET [DUONG_DAN] = :DUONG_DAN, [FILE_BASE64] = NULL " +
                "WHERE [MA_FILE] = :MA_FILE";
        MapSqlParameterSource parameters = new MapSqlParameterSource();
        parameters.addValue("DUONG_DAN", file.getPath());
        parameters.addValue("MA_FILE", maFile);
        return jdbcTemplate.update(queryString, parameters);
    }
}
