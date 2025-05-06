package com.emobile.springtodo.config;

import com.emobile.springtodo.dto.TaskDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Configuration
@Profile("!test")
public class CacheConfig {

    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {


        RedisCacheConfiguration listConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(objectMapper(), List.class)
                ));

        RedisCacheConfiguration singleConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(1))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(
                        new Jackson2JsonRedisSerializer<>(objectMapper(), TaskDTO.class)
                ));

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("all-tasks", listConfig);
        cacheConfigurations.put("tasks", singleConfig);
        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        SimpleModule module = new SimpleModule();

        return new ObjectMapper()
                .registerModule(new JavaTimeModule())
                .registerModule(module)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL)
                .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }
}
