package abareaso.io.jvwatch.model;

import lombok.Data;

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
