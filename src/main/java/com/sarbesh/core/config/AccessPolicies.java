package com.sarbesh.core.config;

import com.sarbesh.core.dto.AccessPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@ConfigurationProperties("")
@RefreshScope
public class AccessPolicies implements InitializingBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccessPolicies.class);

    private Map<String, List<AccessPolicy>> accessPolicies = new HashMap<String, List<AccessPolicy>>();

    public Map<String, List<AccessPolicy>> getAccessPolicies() {
        return accessPolicies;
    }

    public void setAccessPolicies(Map<String, List<AccessPolicy>> accessPolicies) {
        this.accessPolicies = accessPolicies;
    }

    public AccessPolicies() {
    }

    public AccessPolicies(Map<String, List<AccessPolicy>> accessPolicies) {
        this.accessPolicies = accessPolicies;
    }

    @Override
    public String toString() {
        return "AccessPolicies = [" + accessPolicies + "]";
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("#AccessPolicies: {} ",accessPolicies);
    }
}
