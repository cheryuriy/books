package server;

        import org.springframework.context.annotation.Bean;
        import org.springframework.context.annotation.Configuration;
        import org.springframework.context.annotation.Profile;
        import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
        import org.springframework.core.io.ClassPathResource;
        import org.springframework.core.io.Resource;
        import java.io.IOException;
        import java.nio.file.Files;
        import java.nio.file.Paths;


@Configuration
public class ProfilesConfig {

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyConfigurer() throws IOException{
        Resource resource;
        String activeProfile;

        PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer =  new PropertySourcesPlaceholderConfigurer();

        // get active profile
        activeProfile = System.getProperty("spring.profiles.active");

        if(activeProfile != null) {
            // choose different property files for different active profile
            if ("testdb".equals(activeProfile)) {
                resource = new ClassPathResource("/application-testdb.properties");
            } else if ("production".equals(activeProfile)) {
                resource = new ClassPathResource("/application-production.properties");
            } else {
                resource = new ClassPathResource("/application-unit.properties");
            }
        }
        else
        {
		String contents;
        try {
            contents = new String(Files.readAllBytes(Paths.get("d://temp//profile_active.txt")));
        } catch (IOException print_to_file1){contents="production";}
            System.out.println("Contents: " + contents);
            if (contents.equals("testdb")) {
                   resource = new ClassPathResource("/application-testdb.properties");

                //System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "testdb");
                System.setProperty("spring.profiles.active","testdb");
            } else if (contents.equals("production")) {
                resource = new ClassPathResource("/application-production.properties");
                //System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "production");
                System.setProperty("spring.profiles.active","production");
            } else {
                resource = new ClassPathResource("/application-unit.properties");
                //System.setProperty(AbstractEnvironment.ACTIVE_PROFILES_PROPERTY_NAME, "unit");
                System.setProperty("spring.profiles.active","unit");
            }
        }
        // load the property file
        propertySourcesPlaceholderConfigurer.setLocation(resource);

        return propertySourcesPlaceholderConfigurer;
    }
}
