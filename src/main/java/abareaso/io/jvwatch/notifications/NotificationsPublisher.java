package abareaso.io.jvwatch.notifications;

import java.util.List;

import abareaso.io.jvwatch.model.ClinicData;

public class NotificationsPublisher 
{
	protected final List<Publisher> publishers;
	
	public NotificationsPublisher(List<Publisher> publishers)
	{
		this.publishers = publishers;
	}
	
	public void publishNotification(ClinicData data)
	{
		for (Publisher publisher : publishers)
		{
			if (data.isAvailable())
				publisher.publishAvailableNotification(data);
			else
				publisher.publishUnavailableNotification(data);
		}
	}
}
