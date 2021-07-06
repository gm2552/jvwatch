package abareaso.io.jvwatch.notifications;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;

import lombok.extern.slf4j.Slf4j;
import twitter4j.TwitterFactory;
import twitter4j.conf.ConfigurationBuilder;

/**
 * Configuration class for creating instances of the publishers that will ultimately post
 * clinic notifications to their final destinations. 
 * @author Greg Meyer
 *
 */
@Configuration
@Slf4j
public class NotificationConfiguration 
{	
	@Autowired
	protected StringRedisTemplate redisTemplate;
	
	@Value("${jvwatch.notifTimeZone:}")
	protected String notifTimeZone;
	
	@ConditionalOnProperty(name="jvwatch.notifications.twitter.enabled", havingValue="true")
	@Bean
	public TwitterPublisher twitterPublisher(final TwitterConfigProperties props)
	{
		final ConfigurationBuilder cb = new ConfigurationBuilder();
		
		cb.setDebugEnabled(true)
		  .setOAuthConsumerKey(props.getConsumerKey())
		  .setOAuthConsumerSecret(props.getConsumerSecret())
		  .setOAuthAccessToken(props.getAccessToken())
		  .setOAuthAccessTokenSecret(props.getAccessTokenSecret());
		
		final TwitterFactory tf = new TwitterFactory(cb.build());
		return new TwitterPublisher(tf.getInstance(), redisTemplate);
	}
	
	@ConditionalOnProperty(name="jvwatch.notifications.email.enabled", havingValue="true")
	@Bean
	public EmailPublisher emailPublisher(final JavaMailSender mailSender, final EmailMessageConfigProperties props)
	{
		return new EmailPublisher(mailSender, props);
	}
	
	@ConditionalOnProperty(name="jvwatch.notifications.logger.enabled", havingValue="true")
	@Bean
	public LoggerPublisher loggerPublisher()
	{
		return new LoggerPublisher();
	}
		
	
	/**
	 * Creates a bean that contains a list of "enabled" publishers.
	 * @return A list of configured publisher instances that will publish vaccine appointment data. 
	 */
	@Bean
	public NotificationsPublisher notificationsPublisher(final List<Publisher> publishers)
	{		
		if (StringUtils.hasText(notifTimeZone))
		{
			// Set a system property
			System.setProperty("jvwatch.notifTimeZone", notifTimeZone);
		}
		
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
		
		log.info(bld.toString());		
		
		return new NotificationsPublisher(publishers);
	}
}
