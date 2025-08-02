package za.pulsewatch.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import za.pulsewatch.service.LowResourceSignalProcessor;

@Configuration
@EnableCaching
public class OfflineConfig {

    @Bean
    @Profile("offline")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("ppgSignals", "healthMetrics", "userSessions");
    }

    @Bean
    public LowResourceSignalProcessor lowResourceSignalProcessor() {
        return new LowResourceSignalProcessor();
    }
}
