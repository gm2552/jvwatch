package abareaso.io.jvwatch.notifications;

import abareaso.io.jvwatch.model.ClinicData;

/**
 * Interface for a publishing a clinics vaccine appointment availability.  Publishers
 * take the clinic data and publish it to their implementation specific destinations.  For
 * example, the TwitterPublisher tweets out clinic information to a specific twitter account.
 * @author Greg Meyer
 *
 */
public interface Publisher 
{
	/**
	 * Published data for a clinic that has available appointments.
	 * @param data The clinic's data including appointment information.
	 */
	public void publishAvailableNotification(ClinicData data);
	
	/**
	 * Published data for a clinic that no longer has available appointments.
	 * @param data The clinic's data.
	 */
	public void publishUnavailableNotification(ClinicData data);
}
