package config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;
import jakarta.servlet.MultipartConfigElement;
import jakarta.servlet.ServletRegistration;

public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class<?>[]{WebConfig.class, DatabaseConfig.class};
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[]{};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }
    
    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        // Configure multipart file upload
        // location, max file size (5MB), max request size (10MB), file size threshold (1MB)
        registration.setMultipartConfig(new MultipartConfigElement(
            null, 5242880, 10485760, 1048576));
    }
}