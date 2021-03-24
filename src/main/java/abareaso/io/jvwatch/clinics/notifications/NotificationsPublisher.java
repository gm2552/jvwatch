package abareaso.io.jvwatch.clinics.notifications;

import java.util.List;

import abareaso.io.jvwatch.model.ClinicData;

public class NotificationsPublisher 
{
	protected final List<Publisher> publishers;
	
	public NotificationsPublisher(List<Publisher> publishers)
	{
		this.publishers = publishers;
	}
	
	public void publishAvailableNotification(ClinicData data)
	{
		for (Publisher publisher : publishers)
		{
			publisher.publishAvailableNotification(data);
		}
	}
	
	public void publishUnavailableNotification(ClinicData data)
	{
		for (Publisher publisher : publishers)
		{
			publisher.publishUnavailableNotification(data);
		}
	}	
}
