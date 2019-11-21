package ca.gc.dfo.slims.config;

import ca.gc.dfo.spring_commons.commons_offline_wet.configuration.EAccessWebSecurityConfigAdapter;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends EAccessWebSecurityConfigAdapter
{
	@Override
	public void configure(WebSecurity web)
	{
		 web
         	.ignoring().antMatchers("/css/**", "/img/**", "/js/**", "/console/**");
	}
	
	@Override
	public void configureHttpSecurity(HttpSecurity http) throws Exception
	{
		http
			.headers().frameOptions().sameOrigin()
			.and()
			.headers().httpStrictTransportSecurity().disable()
			.and()
			.csrf().disable();
	}
}
