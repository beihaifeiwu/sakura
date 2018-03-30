package com.github.beihaifeiwu.sakura.autoconfigure.core;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.GlobalAuthenticationConfigurerAdapter;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;

/**
 * Created by liupin on 2017/4/6.
 */
@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
@ConditionalOnClass({AuthenticationManager.class, GlobalAuthenticationConfigurerAdapter.class})
@EnableGlobalMethodSecurity(prePostEnabled = true,
        securedEnabled = true, jsr250Enabled = true, proxyTargetClass = true)
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class SecurityExtAutoConfiguration {

}
