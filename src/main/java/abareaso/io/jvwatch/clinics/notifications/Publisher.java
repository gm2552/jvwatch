package abareaso.io.jvwatch.clinics.notifications;

import abareaso.io.jvwatch.model.ClinicData;

public interface Publisher 
{
	public void publishAvailableNotification(ClinicData data);
	
	public void publishUnavailableNotification(ClinicData data);
}
