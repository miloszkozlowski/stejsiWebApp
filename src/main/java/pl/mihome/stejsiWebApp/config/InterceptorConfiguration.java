package pl.mihome.stejsiWebApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import pl.mihome.stejsiWebApp.controller.AndroidRequestsInterceptor;

@Configuration
public class InterceptorConfiguration implements WebMvcConfigurer {

	@Bean
	public AndroidRequestsInterceptor androidRequestsInterceptor() {
		return new AndroidRequestsInterceptor();
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		WebMvcConfigurer.super.addInterceptors(registry);
		registry.addInterceptor(androidRequestsInterceptor()).addPathPatterns("/userinput/**");
	}
	
	

}
