package abareaso.io.jvwatch.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * Configuration properties for the Twitter API.  These include the API consumerKey and consumerSecret and
 * an accessToken and accessToken secret for a specific twitter account.  These setting use the 
 * jvwatch.notifications.twitter.oaut.* configuration parameters.
 * @author Greg Meyer
 *
 */
@Configuration
@ConfigurationProperties(prefix = "jvwatch.notifications.twitter.oauth")
@Data
public class TwitterConfigProperties 
{
	private String consumerKey;
	
	private String consumerSecret;
	
	private String accessToken;
	
	private String accessTokenSecret;
}
