package abareaso.io.jvwatch.model;

import lombok.Data;

/**
 * Request data model for requesting appointment time slots from a HyVee clinic.
 * @author Greg Meyer
 *
 */
@Data
public class HyVeeTimeSlotRequest 
{
	private String operationName = "GetCovidVaccineTimeSlots";
	
	private String query = "query GetCovidVaccineTimeSlots($locationId: ID!, $covidVaccineManufacturerId: ID!) { getCovidVaccineTimeSlots(locationId: $locationId, covidVaccineManufacturerId: $covidVaccineManufacturerId)}";
	
	private Variables variables = new Variables();	
	
	@Data
	public static class Variables
	{
		String locationId;
		
		String covidVaccineManufacturerId;
	}	
}
