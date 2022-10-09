package br.fai.findcollectors.findcollectorsclient.config.security;


import br.fai.findcollectors.findcollectorsclient.config.security.providers.FindCollectorsAuthenticationProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    FindCollectorsAuthenticationProvider authenticationProvider;

    @Override
    protected void configure(final AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(authenticationProvider);
    }

    @Override
    protected void configure(final HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/assets/**")
                .permitAll()
                .antMatchers("/css/**")
                .permitAll()
                .antMatchers("/js/**")
                .permitAll()
                .antMatchers("/webfonts/**")
                .permitAll()
                .antMatchers("/")
                .permitAll()
                .antMatchers("/account/sign-up")
                .permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/account/sign-in")
                .loginProcessingUrl("/account/login")
                .permitAll()
                .defaultSuccessUrl("/")
                .and()
                .exceptionHandling()
                .accessDeniedPage("/common/access-denied");
    }

}
