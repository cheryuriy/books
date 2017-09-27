package server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.converter.protobuf.ProtobufHttpMessageConverter;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.web.client.RestTemplate;
import server.unit.LoadTest;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;


@EntityScan(
        basePackageClasses = {Start.class, Jsr310JpaConverters.class}
)

@SpringBootApplication
@EnableResourceServer
public class Start extends SpringBootServletInitializer
{
//For Wildfly {{{
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application){
        String contents;
        try {
            contents = new String(Files.readAllBytes(Paths.get("d://temp//profile_active.txt")));
            System.out.println("          ..............Profile:    " + contents + "   ............");
        } catch (IOException print_to_file1){contents="production";}
        if (contents.equals("testdb")) {
            application.profiles("testdb");
        } else if (contents.equals("unit"))
        {
              application.profiles("unit");
        }
        else
                application.profiles("production");

        return application.sources(Start.class);
    }
//For Wildfly }}}


       public static void main(String[] args) throws Exception {
//For Tomcat {{{
  /*         
	AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext(ProfilesConfig.class);
	String activeProfile = 	System.getProperty("spring.profiles.active");
        if (activeProfile != null & "unit".equals(activeProfile)) 	{

        ctx = new AnnotationConfigApplicationContext(LoadTest.class);
        LoadTest loadTest = (LoadTest) ctx.getBean("loadTest");        try {
            loadTest.Start();
        } catch (Exception ex){}
		
        System.exit(0);
	}
		else
              SpringApplication.run(Start.class, args);
*/
//For Tomcat }}}
    }


    @Bean
    RestTemplate restTemplate(ProtobufHttpMessageConverter hmc) {
        return new RestTemplate(Arrays.asList(hmc));
    }

    @Bean
    ProtobufHttpMessageConverter protobufHttpMessageConverter() {
        return new ProtobufHttpMessageConverter(); }

}