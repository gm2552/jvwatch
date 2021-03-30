package abareaso.io.jvwatch.notifications;

import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories;

@SpringBootApplication
@EnableRedisRepositories
public class DataTestApplication 
{
	public static void main(String[] args) 
	{
		new SpringApplicationBuilder(DataTestApplication.class).web(WebApplicationType.NONE).run(args);
	}
}
