package abareaso.io.jvwatch.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import abareaso.io.jvwatch.model.ClinicData;

/**
 * A publisher that dumps clinic notification messages to the configured logger.
 * @author Greg Meyer
 *
 */
public class LoggerPublisher extends AbstractPublisher
{
	private static final Logger LOGGER = LoggerFactory.getLogger(LoggerPublisher.class);	
	
	public LoggerPublisher()
	{
		
	}
	
	@Override
	public void publishAvailableNotification(ClinicData data) 
	{
		LOGGER.info("Publishing Available Message:\n\t{}", buildAvailableMessage(data));
	}

	@Override
	public void publishUnavailableNotification(ClinicData data) 
	{
		LOGGER.info("Publishing Unavailable Message:\n\t{}", buildUnavailableMessage(data));
	}

}
