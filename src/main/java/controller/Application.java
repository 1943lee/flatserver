package controller;

import common.InitializeManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

@SpringBootApplication
@Configuration
@EnableAutoConfiguration(exclude={DataSourceAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {
	public static ApplicationContext context;
    public static ApplicationContext myContext;
    
    public static void main(String[] args) {
       context = SpringApplication.run(Application.class, args);
       myContext = new ClassPathXmlApplicationContext("applicationContext.xml");

       System.out.println("Let's inspect the beans provided by Spring Boot:");

       String[] beanNames = context.getBeanDefinitionNames();
       java.util.Arrays.sort(beanNames);
       for (String beanName : beanNames) {
           System.out.println(beanName);
       }
       
       System.out.println("Let's inspect my beans:");

       beanNames = myContext.getBeanDefinitionNames();
       java.util.Arrays.sort(beanNames);
       for (String beanName : beanNames) {
           System.out.println(beanName);
       }

    }
    
    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        String profile = getProfile();
        logger.debug("运行环境：" + profile);
        List<String> configFiles = new ArrayList<>();
        if(isFlatServerEnabled()){
            configFiles.add(profile + ".app.xml");
        }
        if(isWanGeServerEnabled()){
            configFiles.add(profile + ".app" + ".wange.xml");
        }
        myContext = new ClassPathXmlApplicationContext(configFiles.toArray(new String[]{}));
        return application.sources(Application.class);
    }

    @Bean
    public static PropertySourcesPlaceholderConfigurer propertyPlaceholderConfigurer() {
        String propertiesFilename = getPropertiesFilename();

        PropertySourcesPlaceholderConfigurer configurer = new PropertySourcesPlaceholderConfigurer();
        configurer.setLocation(new ClassPathResource(propertiesFilename));

        return configurer;
    }

    private static String getPropertiesFilename() {
        String activeProfile = getProfile();
        return activeProfile + ".app" +  ".properties";
    }

    private static String getProfile() {
        return System.getProperty("spring.profiles.active", "prd");
    }

    @Component
    @Conditional(FlatServerEnabled.class)
    static class Runner implements CommandLineRunner {

        @Override
        public void run(String... args) throws Exception {
            InitializeManager initializeManager = new InitializeManager();
            initializeManager.init();
        }
    }

    private static Properties appConfig;

    public static Properties getAppConfig(){
        if(appConfig != null){
            return appConfig;
        }

        String propertiesFilename = getPropertiesFilename();

        try {
            appConfig = new Properties();
            appConfig.load(Application.class.getClassLoader().getResourceAsStream(propertiesFilename));
        } catch (IOException e) {
            e.printStackTrace();
        }

        return appConfig;
    }

    public static class FlatServerEnabled implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            boolean enabled = isFlatServerEnabled();
            return enabled;
        }
    }

    private static boolean isFlatServerEnabled() {
        String flagName = "enableFlatServer";
        boolean enabled = isEnabled(flagName);
        return enabled;
    }

    private static boolean isEnabled(String flagName) {
        String enableFlatServer = getAppConfig().getProperty(flagName);
        boolean enabled = false;
        if(enableFlatServer != null && "true".equalsIgnoreCase(enableFlatServer)){
            enabled = true;
        }
        return enabled;
    }

    public static class WanGeServerEnabled implements Condition{

        @Override
        public boolean matches(ConditionContext context, AnnotatedTypeMetadata metadata) {
            boolean enabled = isWanGeServerEnabled();
            return enabled;
        }
    }

    private static boolean isWanGeServerEnabled() {
        return isEnabled("enableWanGeServer");
    }

}
