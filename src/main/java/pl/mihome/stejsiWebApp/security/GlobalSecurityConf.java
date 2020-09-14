package pl.mihome.stejsiWebApp.security;

import org.keycloak.adapters.springboot.KeycloakSpringBootConfigResolver;
import org.keycloak.adapters.springsecurity.KeycloakConfiguration;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.keycloak.adapters.springsecurity.config.*;

@KeycloakConfiguration
@EnableWebSecurity
@EnableGlobalMethodSecurity(securedEnabled = true)
public class GlobalSecurityConf extends KeycloakWebSecurityConfigurerAdapter {
	
	@Bean
	KeycloakSpringBootConfigResolver keycloakConfResolver() {
		return new KeycloakSpringBootConfigResolver();
	}
	
	@Autowired
	void globalConfigurer(AuthenticationManagerBuilder auth) {
		var authorityMapper = new SimpleAuthorityMapper();
		authorityMapper.setPrefix("ROLE_");
		authorityMapper.setConvertToUpperCase(true);
		
		KeycloakAuthenticationProvider authProvider = keycloakAuthenticationProvider();
		authProvider.setGrantedAuthoritiesMapper(authorityMapper);
		auth.authenticationProvider(authProvider);
		
	}

	@Override
	protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
		return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		super.configure(http);
		http.authorizeRequests()
			.antMatchers("/**").permitAll()
			.and().csrf()
			.ignoringAntMatchers("/userinput/**")
			.and().csrf()
			.ignoringAntMatchers("/ngaccess/**");			
	}

}
