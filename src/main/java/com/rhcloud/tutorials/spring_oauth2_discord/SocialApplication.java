package com.rhcloud.tutorials.spring_oauth2_discord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.oauth2.resource.ResourceServerProperties;
import org.springframework.boot.autoconfigure.security.oauth2.resource.UserInfoTokenServices;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.security.oauth2.client.filter.OAuth2ClientAuthenticationProcessingFilter;
import org.springframework.security.oauth2.client.filter.OAuth2ClientContextFilter;
import org.springframework.security.oauth2.client.token.AccessTokenProvider;
import org.springframework.security.oauth2.client.token.AccessTokenProviderChain;
import org.springframework.security.oauth2.client.token.OAuth2AccessTokenSupport;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeAccessTokenProvider;
import org.springframework.security.oauth2.client.token.grant.code.AuthorizationCodeResourceDetails;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.Filter;
import java.security.Principal;
import java.util.Arrays;

@SpringBootApplication
@RestController
@EnableOAuth2Client
public class SocialApplication extends WebSecurityConfigurerAdapter {

    @Autowired
    OAuth2ClientContext oauth2ClientContext;

    public static void main(String[] args) {
        SpringApplication.run(SocialApplication.class, args);
    }

    @RequestMapping("/user")
    public Principal user(Principal principal) {
        return principal;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // @formatter:off
        http.antMatcher("/**").authorizeRequests().antMatchers("/", "/login**", "/webjars/**").permitAll().anyRequest()
                .authenticated().and().exceptionHandling()
                .authenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/")).and().logout()
                .logoutSuccessUrl("/").permitAll().and().csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()).and()
                .addFilterBefore(ssoFilter(), BasicAuthenticationFilter.class);
        // @formatter:on
    }

    @Bean
    public FilterRegistrationBean oauth2ClientFilterRegistration(OAuth2ClientContextFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.setOrder(-100);
        return registration;
    }

    private Filter ssoFilter() {
        OAuth2ClientAuthenticationProcessingFilter discordFilter = new OAuth2ClientAuthenticationProcessingFilter("/login/discord");
        OAuth2RestTemplate discordTemplate = new OAuth2RestTemplate(discord(), oauth2ClientContext);

        ClientHttpRequestFactory requestFactory = new DiscordHttpRequestFactory();

        OAuth2AccessTokenSupport authAccessProvider = new AuthorizationCodeAccessTokenProvider();
        authAccessProvider.setRequestFactory(requestFactory);

        AccessTokenProvider accessTokenProvider = new AccessTokenProviderChain(Arrays.<AccessTokenProvider> asList((AuthorizationCodeAccessTokenProvider)authAccessProvider));
        discordTemplate.setAccessTokenProvider(accessTokenProvider);

        discordTemplate.setRequestFactory(requestFactory);

        discordFilter.setRestTemplate(discordTemplate);
        UserInfoTokenServices tokenServices = new UserInfoTokenServices(discordResource().getUserInfoUri(), discord().getClientId());
        tokenServices.setRestTemplate(discordTemplate);

        discordFilter.setTokenServices(tokenServices);
        return discordFilter;
    }

    @Bean
    @ConfigurationProperties("discord.client")
    public AuthorizationCodeResourceDetails discord() {
        return new AuthorizationCodeResourceDetails();
    }

    @Bean
    @ConfigurationProperties("discord.resource")
    public ResourceServerProperties discordResource() {
        return new ResourceServerProperties();
    }

}