package ec.mil.ejercito.cedmt.sidoc.config;

// CaffeineCacheManagerConfig.java
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineCacheManagerConfig {
    @Bean
    public CacheManager cacheManager(Caffeine<Object, Object> caffeine) {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager("manualImages");
        cacheManager.setCaffeine(caffeine);
        return cacheManager;
    }
}