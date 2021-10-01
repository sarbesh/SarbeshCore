package com.sarbesh.core.security;

import com.sarbesh.core.config.AccessPolicies;
import com.sarbesh.core.dto.UserRoles;
import com.sarbesh.core.filter.JwtRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Configuration
@EnableWebFluxSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Autowired
    private JwtRequestFilter jwtRequestFilter;

    @Autowired
    private AccessPolicies accessPolicies;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        LOGGER.info("Setting core WebSecurity");
        http
                .csrf().disable()
                // make sure we use stateless session; session won't be used to store user's state.
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .exceptionHandling()
                .authenticationEntryPoint((req,rsp, e) -> rsp.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                .and()
                .addFilterAfter(jwtRequestFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/prelogin/**").permitAll()
                .anyRequest()
                .authenticated()
                .and()
                .authorizeRequests(this::applyAccessPolicies);
    }

    private void applyAccessPolicies(ExpressionUrlAuthorizationConfigurer<HttpSecurity>.ExpressionInterceptUrlRegistry expressionInterceptUrlRegistry) {
        accessPolicies
                .getAccessPolicies()
                .forEach((s, accessPolicies1) -> accessPolicies1
                        .parallelStream()
                        .forEach(accessPolicy -> {
                            List<UserRoles> allowedRoles = accessPolicy.getAllowedRoles();
                            if (allowedRoles.contains(UserRoles.VISITOR)) {
                                accessPolicy.getApi()
                                        .parallelStream()
                                        .forEach(apiPolicy -> {
                                    apiPolicy.getHttpMethod()
                                            .parallelStream()
                                            .forEach(httpMethod -> {
                                        LOGGER.debug("Adding Visitor role for {}:{}", httpMethod, apiPolicy.getUrl());
                                        expressionInterceptUrlRegistry.antMatchers(httpMethod, apiPolicy.getUrl()).permitAll().and();
                                    });
                                });
                            }
                            accessPolicy.getApi()
                                    .parallelStream()
                                    .forEach(apiPolicy -> {
                                apiPolicy.getHttpMethod()
                                        .parallelStream()
                                        .forEach(httpMethod -> {
                                    LOGGER.debug("Adding {} for {}:{}", allowedRoles, httpMethod, apiPolicy.getUrl());
                                    String[] rolesArray = allowedRoles.parallelStream().map(Object::toString).toArray(String[]::new);
                                    expressionInterceptUrlRegistry.antMatchers(httpMethod, apiPolicy.getUrl()).hasAnyRole(rolesArray).and();
                                });
                            });
                        }));
        expressionInterceptUrlRegistry.anyRequest().authenticated();
    }
}
