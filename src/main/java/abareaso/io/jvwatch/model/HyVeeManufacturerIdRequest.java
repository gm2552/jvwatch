package abareaso.io.jvwatch.model;

import lombok.Data;

/**
 * Request data model for requesting vaccine manufacturer information from a HyVee clinic.
 * @author Greg Meyer
 *
 */
@Data
public class HyVeeManufacturerIdRequest 
{
	private String operationName = "GetCovidVaccineLocationAvailability";
	
	private String query = "query GetCovidVaccineLocationAvailability($locationId: ID!) {getCovidVaccineLocationAvailability(locationId: $locationId) { covidVaccineManufacturerId hasAvailability }}";
	
	private Variables variables = new Variables();	
	
	@Data
	public static class Variables
	{
		String locationId;
	}
}
