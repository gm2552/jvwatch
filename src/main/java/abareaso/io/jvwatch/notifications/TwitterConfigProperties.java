package abareaso.io.jvwatch.notifications;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for the Twitter API.  These include the API consumerKey and consumerSecret and
 * an accessToken and accessToken secret for a specific twitter account.  These setting use the 
 * jvwatch.notifications.twitter.oaut.* configuration parameters.
 * @author Greg Meyer
 *
 */
@Configuration
@ConfigurationProperties(prefix = "jvwatch.notifications.twitter.oauth") 
public class TwitterConfigProperties 
{
	private String consumerKey;
	
	private String consumerSecret;
	
	private String accessToken;
	
	private String accessTokenSecret;

	public String getConsumerKey() 
	{
		return consumerKey;
	}

	public void setConsumerKey(String consumerKey) 
	{
		this.consumerKey = consumerKey;
	}

	public String getConsumerSecret() 
	{
		return consumerSecret;
	}

	public void setConsumerSecret(String consumerSecret) 
	{
		this.consumerSecret = consumerSecret;
	}

	public String getAccessToken() 
	{
		return accessToken;
	}

	public void setAccessToken(String accessToken) 
	{
		this.accessToken = accessToken;
	}

	public String getAccessTokenSecret() 
	{
		return accessTokenSecret;
	}

	public void setAccessTokenSecret(String accessTokenSecret) 
	{
		this.accessTokenSecret = accessTokenSecret;
	}
	
	
}
