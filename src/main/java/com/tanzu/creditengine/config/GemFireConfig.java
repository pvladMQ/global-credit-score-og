package com.tanzu.creditengine.config;

import com.tanzu.creditengine.entity.CreditScoreCache;
import org.apache.geode.cache.GemFireCache;
import org.apache.geode.cache.client.ClientRegionShortcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.gemfire.client.ClientRegionFactoryBean;
import org.springframework.data.gemfire.config.annotation.EnableEntityDefinedRegions;
import org.springframework.data.gemfire.repository.config.EnableGemfireRepositories;

/**
 * GemFire configuration for the CreditScoreCache region.
 */
@Configuration
@EnableGemfireRepositories(basePackages = "com.tanzu.creditengine.repository")
@EnableEntityDefinedRegions(basePackageClasses = CreditScoreCache.class, clientRegionShortcut = ClientRegionShortcut.PROXY)
public class GemFireConfig {

    /**
     * Defines the CreditScoreCache region as a PROXY region.
     * PROXY regions store data on the server-side (GemFire cluster)
     * and provide sub-second access from any connected client.
     */
    @Bean("CreditScoreCache")
    public ClientRegionFactoryBean<String, CreditScoreCache> creditScoreCacheRegion(GemFireCache gemfireCache) {
        ClientRegionFactoryBean<String, CreditScoreCache> region = new ClientRegionFactoryBean<>();
        region.setCache(gemfireCache);
        region.setRegionName("CreditScoreCache");
        region.setShortcut(ClientRegionShortcut.PROXY);
        return region;
    }
}
