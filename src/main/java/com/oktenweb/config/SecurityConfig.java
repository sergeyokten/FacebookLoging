package com.oktenweb.config;

import com.oktenweb.repository.UserRepository;
import com.oktenweb.service.RepositoryUserDetailsService;
import com.oktenweb.service.SimpleSocialUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.social.security.SocialUserDetailsService;
import org.springframework.social.security.SpringSocialConfigurer;

@Configuration
@EnableWebSecurity
@ComponentScan("com.oktenweb.*")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web
                //Spring Security ignores request to static resources such as CSS or JS files.
                .ignoring()
                .antMatchers("/static/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                //Configures form login
                .formLogin()
                .loginPage("/login")
                .loginProcessingUrl("/login/authenticate")
                .failureUrl("/login?error=bad_credentials")
                //Configures the logout function
                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login")
                //Configures url based authorization
                .and()
                .authorizeRequests()
                //Anyone can access the urls
                .antMatchers(
                        "/auth/**",
                        "/login",
                        "/signup/**",
                        "/user/register/**"
                ).permitAll()
                //The rest of the our application is protected.
                .antMatchers("/**").hasRole("USER")
                //Adds the SocialAuthenticationFilter to Spring Security's filter chain.
                .and()
                .apply(new SpringSocialConfigurer());
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth
                .userDetailsService(userDetailsService())
                .passwordEncoder(passwordEncoder());
    }

    @Bean
    public SocialUserDetailsService socialUserDetailsService() {
        return new SimpleSocialUserDetailsService(userDetailsService());
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new RepositoryUserDetailsService(userRepository);
    }


//    @Autowired
//    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
//        auth
//                .inMemoryAuthentication()
//                .withUser("user").password("password").roles("ADMIN");
//    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        CharacterEncodingFilter filter = new CharacterEncodingFilter();
//        filter.setEncoding("UTF-8");
//        filter.setForceEncoding(true);
//        http.addFilterBefore(filter, CsrfFilter.class);
//        http.authorizeRequests()
//                .antMatchers("/").permitAll()
//                .antMatchers("/admin/**").access("hasRole('ADMIN')")
//                .and()
//                .formLogin()
//                /*without this is use of spring loginpage*/
//                .loginPage("/login")
//                // .loginProcessingUrl("/logMe")
//                //.successForwardUrl("/asd")
//                .passwordParameter("password")
//                .usernameParameter("username")
//                /*---------------------------------------*/
//                .and()
//                .csrf();
//        //.and()
//        //.exceptionHandling().accessDeniedPage("/accessDeniedPage");
////         .formLogin()
////                 .loginPage("/toLoginPage")
////                 .loginProcessingUrl("/logMe")
////                 .successForwardUrl("/asd")
////                 .passwordParameter("password")
////                 .usernameParameter("username")
//
//    }
}
