package com.evnit.ttpm.khcn;

import com.evnit.ttpm.khcn.controllers.service.WordController;
import com.evnit.ttpm.khcn.models.admin.Q_Lst_Api;
import com.evnit.ttpm.khcn.services.admin.ApiService;
import com.evnit.ttpm.khcn.thread.ThreadSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationStartedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.mvc.method.RequestMappingInfo;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootApplication
@EnableSwagger2
@EnableWebMvc
public class MainApplication {
    ThreadSystem threadSystem;
    @Autowired
    ApiService apiService;

    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    public List<String> getEndpoints() {
        return requestMappingHandlerMapping
                .getHandlerMethods()
                .keySet()
                .stream()
                .map(RequestMappingInfo::toString)
                .collect(Collectors.toList());

    }

    @EventListener
    public void onApplicationStartedEvent(ApplicationStartedEvent event) {
        threadSystem = new ThreadSystem();
        threadSystem.start();

        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods().forEach((key, value)
                        -> {
                        if (value != null && value.toString().contains("com.sypv") && apiService.findAPIByEndPoint(key.toString()) == null) {
                            try {
                                Q_Lst_Api obj = new Q_Lst_Api(null,
                                        key.toString().substring(1, key.toString().length() - 1),
                                        value.toString(),
                                        true,
                                        false,
                                        new Date(),
                                        "system",
                                        new Date(),
                                        null,
                                        null);
                                apiService.insertAPI(obj);

                            } catch (Exception e) {
                               System.out.println(e.getMessage());
                            }
                    }
                });

    }

    public static void main(String[] args) {
//        WordController word = new WordController();
////        word.ConvertToWord();
//        word.KeHoach("", "125");
        SpringApplication.run(MainApplication.class, args);
    }

}
