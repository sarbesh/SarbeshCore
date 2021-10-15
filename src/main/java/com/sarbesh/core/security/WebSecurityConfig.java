package com.sarbesh.core.security;

import com.sarbesh.core.config.AccessPolicies;
import com.sarbesh.core.dto.UserRoles;
import com.sarbesh.core.filter.JwtRequestFilter;
import com.sarbesh.core.filter.NetworkAccessFilter;
import com.sarbesh.core.logging.RequestLoggingInterceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;


@Configuration
@EnableWebSecurity
@ConditionalOnProperty(
        name = {"security.user-auth.enable"},
        havingValue = "true"
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired(required = false)
    CustomWebSecurityConfigurer customWebSecurityConfigurer;

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AccessPolicies accessPolicies;

    @Autowired
    private RequestLoggingInterceptor requestLoggingInterceptor;

    @Autowired
    private NetworkAccessFilter networkAccessFilter;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    public WebSecurityConfig() {
    }

    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        if (customWebSecurityConfigurer!=null){
            LOGGER.info("Using custom AuthenticationManagerBuilder");
            customWebSecurityConfigurer.configure(auth);
        } else {
            LOGGER.info("Using default AuthenticationManagerBuilder");
            auth.userDetailsService(userDetailsService()).passwordEncoder(passwordEncoder());
        }
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.info("Setting core WebSecurity");
        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilterBefore(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests(this::applyAccessPolicies)
                .exceptionHandling()
                .authenticationEntryPoint((req,rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED));
        if (customWebSecurityConfigurer != null) customWebSecurityConfigurer.configure(http);
    }

    private ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry applyAccessPolicies(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) {
        accessPolicies
                .getAccessPolicies()
                .forEach((s, accessPolicies1) -> accessPolicies1
                        .parallelStream()
                        .forEach(accessPolicy -> {
                            List<UserRoles> allowedRoles = accessPolicy.getAllowedRoles();
                            accessPolicy.getApi()
                                    .parallelStream()
                                    .forEach(apiPolicy -> {
                                        String[] rolesArray = allowedRoles.parallelStream().map(Object::toString).toArray(String[]::new);
                                        String[] urls = apiPolicy.getUrl().toArray(new String[0]);
                                        if(apiPolicy.getHttpMethod().isEmpty()){
                                            LOGGER.debug("Adding {} for {}", rolesArray, urls);
                                            expressionInterceptUrlRegistry
                                                    .antMatchers(urls)
                                                    .hasAnyRole(rolesArray).and();
                                        } else {
                                            apiPolicy.getHttpMethod()
                                                    .parallelStream()
                                                    .forEach(httpMethod -> {
                                                        LOGGER.debug("Adding {} for {}:{}", rolesArray, httpMethod, urls);
                                                        expressionInterceptUrlRegistry
                                                                .antMatchers(httpMethod, urls)
                                                                .hasAnyRole(rolesArray).and();
                                                    });
                                        }
                                    });
                        }));
        expressionInterceptUrlRegistry.anyRequest().authenticated();
        return expressionInterceptUrlRegistry;
    }


}
