package abareaso.io.jvwatch.notifications;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Configuration class for creating instances of the publishers that will ultimately post
 * clinic notifications to their final destinations. 
 * @author Greg Meyer
 *
 */
@Configuration
public class NotificationConfiguration 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(NotificationConfiguration.class);			
	
	@Value("${jvwatch.notifications.logger.enabled:true}")
	protected boolean logPublisherEnabled;
	
	@Autowired(required=false)
	protected Twitter twitter;
	
	/**
	 * Creates a twitter API instance for posting notifications to twitter.  The configuration setting
	 * jvwatch.notifications.twitter.enabled must be set to 'true' for the twitter publisher to be used.
	 * Additional configuration is required to set the twitter API keys and secrets.
	 * @param props Contains the twitter API keys and secrets.  The are contained in the 
	 * jvwatch.notifications.twitter.oauth.* settings.
	 * @return An instance of the Twitter API class.
	 */
	@ConditionalOnProperty(name="jvwatch.notifications.twitter.enabled", havingValue="true")
	@Bean
	public Twitter twitter(TwitterConfigProperties props)
	{
		final ConfigurationBuilder cb = new ConfigurationBuilder();
		
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(props.getConsumerKey())
		  .setOAuthConsumerSecret(props.getConsumerSecret())
		  .setOAuthAccessToken(props.getAccessToken())
		  .setOAuthAccessTokenSecret(props.getAccessTokenSecret());
		
		final TwitterFactory tf = new TwitterFactory(cb.build());
		return tf.getInstance();
	}
	
	/**
	 * Creates a bean that contains a list of "enabled" publishers.
	 * @return A list of configured publisher instances that will publish vaccine appointment data. 
	 */
	@Bean
	public NotificationsPublisher notificationsPublisher()
	{
		final List<Publisher> publishers = new ArrayList<>();
		
		if (twitter != null)
			publishers.add(new TwitterPublisher(twitter));
		
		if (logPublisherEnabled)
			publishers.add(new LoggerPublisher());
		
		final StringBuilder bld = new StringBuilder("Enabled notification publishers:");
		if (publishers.isEmpty())
			bld.append("\n\tNONE");
		else
		{
			for (Publisher pub : publishers)
			{
				bld.append("\n\t").append(pub.getClass().getSimpleName());
			}
		}
		
		LOGGER.info(bld.toString());		
		
		return new NotificationsPublisher(publishers);
	}
}