package com.evnit.ttpm.khcn.controllers.service;

import com.evnit.ttpm.khcn.models.kehoach.FileUpload;
import com.evnit.ttpm.khcn.models.tonghopfile.FileBase64;
import com.evnit.ttpm.khcn.payload.response.service.ExecServiceResponse;
import com.evnit.ttpm.khcn.security.services.SecurityUtils;
import com.evnit.ttpm.khcn.services.mail.MailService;
import com.evnit.ttpm.khcn.services.storage.FileService;
import com.evnit.ttpm.khcn.services.tonghopfile.TongHopFileService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Objects;

@Component
public class ToolCallNgoaiController {

    @Autowired
    FileService fileService;
    @Autowired
    TongHopFileService tongHopFileService;
    @Autowired
    private MailService mailService;

    @Value("${app.login.url}")
    private String loginUrl;
    @Value("${app.login.username}")
    private String username;
    @Value("${app.login.password}")
    private String password;

    public ExecServiceResponse toolAutoUpload(String token) throws IOException {

        //String token = "";
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
//                    if (result == 1){
//                        return "OK";
//                    } else {
//                        return "NOT OK";
//                    }
                }
            }
        }
        return new ExecServiceResponse(1, "Thực hiện thành công");
    }

    public ExecServiceResponse toolAutoEmail(String token) {
        //String token ="";
        mailService.GetSendMailDB(token);
        return new ExecServiceResponse(-1, "Thực hiện thành công");
    }

    public String getToken() throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        RestTemplate restTemplate = new RestTemplate();

        String requestBody = "{" +
                "    \"username\": \"" + username + "\"," +
                "    \"password\": \"" + password + "\"," +
                "    \"expiration\": 120," +
                "    \"deviceInfo\": {" +
                "        \"deviceId\": \"ac5ac2c0-568a-4bf2-bb31-ae86e82eb120\"," +
                "        \"deviceType\": \"Windows/windows-10/desktop/Chrome\"," +
                "        \"appId\": \"KHCN\"," +
                "        \"appVersion\": \"KHCN 1.0.0 build 18/04/2023\"" +
                "    }\n" +
                "}";

        HttpEntity<Object> requestEntity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<String> result = restTemplate.postForEntity(loginUrl, requestEntity,
                String.class);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode responseJson = objectMapper.readTree(result.getBody());
        return responseJson.get("data").get("accessToken").asText();
    }

    public String Token() {
        // HttpHeaders headers = new HttpHeaders();
        // headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        // headers.set("Authorization", bearerToken);
        // HttpEntity<String>: To get result as String.
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
//        ByteArrayResource contentsAsResource = new ByteArrayResource(fileByte) {
//            @Override
//            public String getFilename() {
//                return fileName; // Filename has to be returned in order to be able to post.
//            }
//        };
//        body.add("file", contentsAsResource);
//        body.add("path", pathMogodb);
//        // RestTemplate
//        RestTemplate restTemplate = new RestTemplate();
//        // Dữ liệu đính kèm theo yêu cầu.
//        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
//        // Gửi yêu cầu với phương thức POST.
//        ResponseEntity<Object> result = restTemplate.postForEntity(urlFileService + "/file/upload", requestEntity,
//                Object.class);
//        return result.getBody();
        return null;
    }
}
