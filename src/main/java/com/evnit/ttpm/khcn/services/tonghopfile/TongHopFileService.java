package com.evnit.ttpm.khcn.services.tonghopfile;

import com.evnit.ttpm.khcn.models.kehoach.FileUpload;
import com.evnit.ttpm.khcn.models.tonghopfile.FileBase64;

import java.util.List;

public interface TongHopFileService {
    List<FileBase64> get10RecordsFromDtDetailFileAndKhKeHoachFileAndSkSangKienFile();

    int updateFile(String tableName, String maFile, FileUpload file);
}
