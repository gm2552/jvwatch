package abareaso.io.jvwatch.clinics;

import java.util.List;

import abareaso.io.jvwatch.model.ClinicData;

/**
 * Interface for collection data on vaccine appointment availability.
 * @author Greg Meyer
 *
 */
public interface Clinic 
{
	/**
	 * Gathers vaccine appointment availability for clinic locations.  The list of locations
	 * that are part of the data set are controlled by configuration settings.  Some configuration
	 * settings are specific to a clinic's "brand" (i.e. Hy-Vee, Walmart, Walgreens, etc).
	 * @return A list of clinic vaccine appointment data for the configured clinic locations.
	 */
	public List<ClinicData> getClinicAppointements();
}
