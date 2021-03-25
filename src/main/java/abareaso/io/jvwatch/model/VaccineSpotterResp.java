package abareaso.io.jvwatch.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import lombok.Data;

/**
 * Reply data model for requesting appointment information from clinics supported by the VaccineSpotter API.
 * @author Greg Meyer
 *
 */
@Data
public class VaccineSpotterResp 
{
	private List<VaccineSpotterFeature> features = new LinkedList<>();
	
	@Data
	public static class VaccineSpotterFeature
	{
		private String type;
		
		private VaccineSpotterGeometry geometry;
		
		private VaccineSpotterProperties properties;
	}
	
	@Data
	public static class VaccineSpotterGeometry
	{
		private String type;
		
		private List<Double> coordinates = new ArrayList<>();
	}
	
	@Data
	public static class VaccineSpotterProperties
	{
		private String id;
		
		private String url;
		
		private String name;
		
		private String city;
		
		private String state;
		
		private String provider;
		
		private String postal_code;
		
		private boolean appointments_available;
		
		private List<VaccineSpotterAppointment> appointments = new LinkedList<>();
		
		private String provider_brand;
		
		private String appointments_last_fetched;
	}
	
	@Data 
	public static class VaccineSpotterAppointment
	{
		private String time;
		
		private String type;
	}
	
}
