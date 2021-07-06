package abareaso.io.jvwatch.notifications;

import abareaso.io.jvwatch.model.ClinicData;
import lombok.extern.slf4j.Slf4j;

/**
 * A publisher that dumps clinic notification messages to the configured logger.
 * @author Greg Meyer
 *
 */
@Slf4j
public class LoggerPublisher extends AbstractPublisher
{
	public LoggerPublisher()
	{
		
	}
	
	@Override
	public void publishAvailableNotification(ClinicData data) 
	{
		log.info("Publishing Available Message:\n\t{}", buildAvailableMessage(data));
	}

	@Override
	public void publishUnavailableNotification(ClinicData data) 
	{
		log.info("Publishing Unavailable Message:\n\t{}", buildUnavailableMessage(data));
	}

}
