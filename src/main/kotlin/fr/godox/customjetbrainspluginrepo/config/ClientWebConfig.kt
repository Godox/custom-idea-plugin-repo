package fr.godox.customjetbrainspluginrepo.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

// On ajoute EnableTransactionManagement
@EnableWebMvc
@Configuration
@ComponentScan(basePackages = {"fr.godox.customjetbrainspluginrepo.controller", "fr.godox.customjetbrainspluginrepo.service"})
@EnableTransactionManagement
public class ClientWebConfig implements WebMvcConfigurer {

    public ClientWebConfig() {
        try {
            Files.createDirectories(new File("./received").toPath());
        } catch (IOException ignored) {

        }
    }

    //    @Bean
//    @Description("Thymeleaf Template Resolver")
//    public SpringResourceTemplateResolver templateResolver() {
//        SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
//        templateResolver.setPrefix("/WEB-INF/views/");
//        templateResolver.setSuffix(".html");
//        templateResolver.setTemplateMode("HTML5");
//        return templateResolver;
//    }
//
//    @Bean
//    @Description("Thymeleaf Template Engine")
//    public SpringTemplateEngine templateEngine() {
//        SpringTemplateEngine templateEngine = new SpringTemplateEngine();
//        templateEngine.addDialect(new Java8TimeDialect());
//        templateEngine.setTemplateResolver(templateResolver());
//        //templateEngine.setTemplateEngineMessageSource(messageSource());
//        return templateEngine;
//    }
//
//
//
//
//    @Bean
//    @Description("Thymeleaf View Resolver")
//    public ThymeleafViewResolver viewResolver() {
//        ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
//        viewResolver.setTemplateEngine(templateEngine());
//        viewResolver.setOrder(1);
//        return viewResolver;
//    }

    /*
    @Bean
    @Description("Spring Message Resolver")
    public ResourceBundleMessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        return messageSource;
    }


     */


//    @Bean
//    public MappingJackson2XmlHttpMessageConverter mappingJackson2XmlHttpMessageConverter(Jackson2ObjectMapperBuilder builder) {
//        XmlMapper xmlMapper = builder.createXmlMapper(true).build();
//        xmlMapper.configure(ToXmlGenerator.Feature.WRITE_XML_DECLARATION, true);
//        return new MappingJackson2XmlHttpMessageConverter(xmlMapper);
//    }

//    @Bean
//    public DataSource dataSource() {
//        EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
//        return builder
//                .setType(EmbeddedDatabaseType.H2) //.H2 or .DERBY
////                .addScript("db/sql/create-db.sql")   // dans resources
//                .build();
//    }

//    @Bean
//    public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
//        LocalContainerEntityManagerFactoryBean em = new LocalContainerEntityManagerFactoryBean();
//        em.setDataSource(dataSource());
//        em.setPackagesToScan("fr.godox.customjetbrainspluginrepo.model");
//
//        JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
//        em.setJpaVendorAdapter(vendorAdapter);
//        em.setJpaProperties(additionalProperties());
//
//        return em;
//    }

//    Properties additionalProperties() {
//        Properties properties = new Properties();
//        properties.setProperty("hibernate.hbm2ddl.auto", "create-drop");
//        properties.setProperty(
//                "hibernate.dialect", "org.hibernate.dialect.H2Dialect");
//        properties.setProperty("hibernate.hbm2ddl.import_files", "insert-data.sql");
//        return properties;
//    }

//    @Bean
//    public PlatformTransactionManager transactionManager(
//            EntityManagerFactory emf) {
//        JpaTransactionManager transactionManager = new JpaTransactionManager();
//        transactionManager.setEntityManagerFactory(emf);
//
//        return transactionManager;
//    }


}