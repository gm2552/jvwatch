package abareaso.io.jvwatch.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.util.StringUtils;

import abareaso.io.jvwatch.model.ClinicData;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

/**
 * A Twitter publisher.  This publisher tweets clinic availability information
 * to a specific twitter account.
 * @author Greg Meyer
 *
 */
public class TwitterPublisher extends AbstractPublisher
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterPublisher.class);		
	
	protected final Twitter twitter;
	
	protected final StringRedisTemplate redisTemplate;
	
	public TwitterPublisher(Twitter twitter, StringRedisTemplate redisTemplate)
	{
		this.twitter = twitter;
		this.redisTemplate = redisTemplate;
	}

	@Override
	public void publishAvailableNotification(ClinicData data) 
	{
		try
		{	
			final Status status = twitter.updateStatus(buildAvailableMessage(data));
			
			redisTemplate.opsForValue().set("tweet-" + data.getId(), Long.toString(status.getId()));
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to publish tweet for available data.", e);
		}
	}
	
	@Override
	public void publishUnavailableNotification(ClinicData data)
	{
		try
		{
			long tweetId = -1;
			
			final String tweetIdStr = redisTemplate.opsForValue().get("tweet-" + data.getId());

			if (StringUtils.hasText(tweetIdStr))
			{
				tweetId = Long.parseLong(tweetIdStr);
			}
			
			if (tweetId == -1)
				twitter.updateStatus(buildUnavailableMessage(data));
			else 
			{
				
				
			    final StatusUpdate statusUpdate = new StatusUpdate(buildUnavailableMessage(data));
			    statusUpdate.inReplyToStatusId(tweetId);
			    
			    twitter.updateStatus(statusUpdate);
			    
			    redisTemplate.opsForValue().getOperations().delete("tweet-" + data.getId());
			}
			
			
		}
		catch (Exception e)
		{
			LOGGER.error("Failed to publish tweet for unavailable data.", e);
		}
	}
	
	@Override
	protected String buildAvailableMessage(ClinicData data)
	{
		final StringBuilder builder = new StringBuilder(super.buildAvailableMessage(data));
		
		// adding hash tags
		builder.append(" #vaccination #CovidVaccine");
		
		return builder.toString();
	}
}
