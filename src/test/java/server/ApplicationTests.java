package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.web.client.RestTemplate;
import java.util.Arrays;


@EntityScan(
        basePackageClasses = {ApplicationTests.class, Jsr310JpaConverters.class})
@SpringBootApplication
@EnableResourceServer
public class ApplicationTests
{
    
    public static void main(String[] args) throws Exception{
        System.out.println("Test started successfully...");
        SpringApplication.run(ApplicationTests.class, args);
    }
    @Bean
    RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
        return new RestTemplate(Arrays.asList(hmc));
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter(); }

}
