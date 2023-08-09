package com.evnit.ttpm.khcn.controllers;

import com.evnit.ttpm.khcn.models.kehoach.FileUpload;
import com.evnit.ttpm.khcn.models.tonghopfile.FileBase64;
import com.evnit.ttpm.khcn.security.services.SecurityUtils;
import com.evnit.ttpm.khcn.services.storage.FileService;
import com.evnit.ttpm.khcn.services.tonghopfile.TongHopFileService;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/service")
public class TestController {

    private final FileService fileService;
    private final TongHopFileService tongHopFileService;

    public TestController(FileService fileService, TongHopFileService tongHopFileService) {
        this.fileService = fileService;
        this.tongHopFileService = tongHopFileService;
    }

    @PostMapping("/test")
    public String test() throws IOException {
        String token = "";
        List<FileBase64> fileBase64List = tongHopFileService.get10RecordsFromDtDetailFileAndKhKeHoachFileAndSkSangKienFile();
        if (Objects.nonNull(fileBase64List) && !fileBase64List.isEmpty()) {
            for (FileBase64 fileBase64 : fileBase64List) {
                String fileName = fileBase64.getTenFile();
                String orgId = SecurityUtils.getPrincipal().getORGID();
                String userId = SecurityUtils.getPrincipal().getUserId().replace("\\", "/");
                SimpleDateFormat sdf = new SimpleDateFormat("ddMMYYYYhhmmss");
                String dateString = sdf.format(new Date());
                String path = "/khcn/" + orgId + "/" + userId + "/" + dateString + "/" + fileName;
                byte[] decodedBytes = Base64.getDecoder().decode(fileBase64.getFileBase64());
                Object obj = fileService.callPostFile(fileName, path, decodedBytes, token);
                if (Objects.nonNull(obj)) {
                    Gson gson = new Gson();
                    FileUpload file = gson.fromJson(gson.toJson(obj), FileUpload.class);
                    int result = tongHopFileService.updateFile(fileBase64.getTableName(), fileBase64.getMaFile(), file);
                    if (result == 1){
                        return "OK";
                    } else {
                        return "NOT OK";
                    }
                }
            }
        }
        return "Không tìm thấy file nào cần xử lý";
    }
}
