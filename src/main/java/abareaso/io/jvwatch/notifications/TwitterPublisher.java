package abareaso.io.jvwatch.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abareaso.io.jvwatch.model.ClinicData;
import twitter4j.Twitter;

public class TwitterPublisher extends AbstractPublisher
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TwitterPublisher.class);		
	
	protected final Twitter twitter;
	
	public TwitterPublisher(Twitter twitter)
	{
		this.twitter = twitter;
	}

	@Override
	public void publishAvailableNotification(ClinicData data) 
	{
		try
		{
			twitter.updateStatus(buildAvailableMessage(data));
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
			twitter.updateStatus(buildUnavailableMessage(data));
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
