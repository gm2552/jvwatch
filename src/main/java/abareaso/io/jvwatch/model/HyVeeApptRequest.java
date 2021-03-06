package abareaso.io.jvwatch.model;

import lombok.Data;

/**
 * Request data model for requesting location information from HyVee clinics.
 * @author Greg Meyer
 *
 */
@Data
public class HyVeeApptRequest 
{
	private String operationName = "SearchPharmaciesNearPointWithCovidVaccineAvailability";
	
	private String query = "query SearchPharmaciesNearPointWithCovidVaccineAvailability($latitude: Float!, $longitude: Float!, $radius: Int!) " + 
	   "{searchPharmaciesNearPoint(latitude: $latitude, longitude: $longitude, radius: $radius) {location {locationId name isCovidVaccineAvailable address {state zip}}}}";
	
	private Variables variables = new Variables();	
	
	@Data
	public static class Variables
	{
		int radius;
		
		double latitude;
		
		double longitude;
	}
}

