package abareaso.io.jvwatch.model;

import lombok.Data;

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
