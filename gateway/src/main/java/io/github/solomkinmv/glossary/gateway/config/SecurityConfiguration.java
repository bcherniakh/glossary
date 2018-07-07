package io.github.solomkinmv.glossary.gateway.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableResourceServer;
import org.springframework.security.oauth2.config.annotation.web.configuration.ResourceServerConfigurerAdapter;
import org.springframework.security.web.util.matcher.RequestHeaderRequestMatcher;

//@Order(1000) // todo: try without this
//@EnableOAuth2Sso
//@EnableWebSecurity
//@Configuration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private RestAuthenticationEntryPoint restAuthenticationEntryPoint;

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .exceptionHandling()
                .authenticationEntryPoint(restAuthenticationEntryPoint)
                .and()
                .authorizeRequests()
                .anyRequest().authenticated()
        ;

//            .antMatchers("/login**", "/error**")
//                .permitAll()
//            .antMatchers("/**").authenticated()
//            .and().oauth2Login()
        //        http.antMatcher("/**")
//            .authorizeRequests()
//            .antMatchers("/", "/login**")
//            .permitAll()
//            .anyRequest()
//            .authenticated()
//        .and().oauth2Login()
//        .and().formLogin().disable()
//        ;
    }
}

@Configuration
@EnableResourceServer
//@EnableWebSecurity
class ResourceSecurityConfiguration extends ResourceServerConfigurerAdapter {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .requestMatcher(new RequestHeaderRequestMatcher("Authorization"))

                .authorizeRequests()
                .anyRequest().authenticated();
    }
}
