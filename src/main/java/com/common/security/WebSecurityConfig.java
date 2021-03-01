package com.common.security;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableJpaAuditing
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${security.enabled: true}")
    private boolean enabled;

    @Value("${swagger.basic.enable}")
    private boolean swagger_enabled;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http.csrf().disable();

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        if (enabled) {
            http.authorizeRequests().anyRequest().authenticated();
            http.apply(new JwtTokenFilterConfigurer(jwtTokenProvider));
        } else {
            log.warn("security is disabled, all access are granted...");
            http.authorizeRequests()
                    .antMatchers("/**")
                    .permitAll();
        }

    }

    @Override
	public void configure(WebSecurity web) {
        List<String> lt = new ArrayList<>();
        lt.add("/web/**");
        lt.add( "/app/**");
        lt.add("/v1/users/login");
        lt.add("/expo/v1/news/**");
        lt.add("/api/v1/eventReport");
        lt.add("/expo/v1/evaluation/add");
        lt.add("/expo/v1/external/**");
        if(swagger_enabled) {
            lt.add("/doc.html");
            lt.add("/webjars/**");
            lt.add("/swagger-resources/**");
            lt.add("/v2/**");
        }
        web.ignoring().antMatchers(lt.toArray(new String[]{}));
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }
    
    // public static void main(String[] args) {
    //     String password = "111111";
    //     PasswordEncoder pe = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    //     String encodedPassword = pe.encode(password);
    //     System.out.println(encodedPassword);
    // }

}