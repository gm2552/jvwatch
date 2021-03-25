package abareaso.io.jvwatch;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

import reactivefeign.spring.config.EnableReactiveFeignClients;

@SpringBootApplication
@EnableReactiveFeignClients
@EnableRedisRepositories
@EnableConfigurationProperties
public class JVWatachApplication
{
	public static void main(String[] args) 
	{		
        SpringApplication.run(JVWatachApplication.class);
	}
}
