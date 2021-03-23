package abareaso.io.jvwatch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients("abareaso.io.jvwatch.feign")
@EnableScheduling
@EnableRedisRepositories
public class JVWatachApplication
{
	public static void main(String[] args) 
	{
        SpringApplication.run(JVWatachApplication.class);
	}
}
