package com.project.sns;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan // 패키지를 기반으로 @ConfigurationProperties가 등록된 클래스들을 찾아 값들을 주입하고 빈으로 등록
@SpringBootApplication
public class SnsApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnsApplication.class, args);
    }

}
