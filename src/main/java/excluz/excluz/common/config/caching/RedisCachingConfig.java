package excluz.excluz.common.config.caching;

import java.time.Duration;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

@EnableCaching
@Configuration
public class RedisCachingConfig {
	@Bean
	public CacheManager redisCacheManager(RedisConnectionFactory redisConnectionFactory) {
		// 캐시 설정을 정의 (만료 시간 10분, null 값 캐싱 방지)
		RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
			.entryTtl(Duration.ofMinutes(10)) // 10분 캐싱 유지
			.disableCachingNullValues(); // null 값 캐싱 방지 (불필요한 캐시 사용 방지)

		// RedisCacheManager를 생성하여 반환
		return RedisCacheManager.builder(RedisCacheWriter.nonLockingRedisCacheWriter(redisConnectionFactory))
			.cacheDefaults(cacheConfig)
			.build();
	}

	/**
	 * RedisTemplate 설정
	 * Redis 데이터 저장, 조회에 사용 (RedisTemplate == Spring Boot에서 Redis에 데이터를 저장하고 조회하는 데 사용하는 객체)
	 *
	 * @param redisConnectionFactory Redis 연결을 담당하는 ConnectionFactory
	 * @return RedisTemplate<String, Object> (키-값 구조로 Redis 데이터 저장 및 조회)
	 */
	@Bean
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
		RedisTemplate<String, Object> template = new RedisTemplate<>(); // RedisTemplate 객체 생성
		template.setConnectionFactory(redisConnectionFactory); // Redis 연결을 위한 ConnectionFactory 설정
		return template; // 설정된 RedisTemplate 반환
	}
}