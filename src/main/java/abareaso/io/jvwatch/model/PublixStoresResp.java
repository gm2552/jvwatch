package abareaso.io.jvwatch.model;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

/**
 * Reply data model for requesting Publix store information.
 * @author Greg Meyer
 *
 */
@Data
public class PublixStoresResp 
{
	@JsonProperty("Stores")
	private List<Store> stores = new LinkedList<>();
	
	@Data
	public static class Store
	{
		@JsonProperty("KEY")
		private String key;
		
		@JsonProperty("NAME")
		private String name;
		
		@JsonProperty("CITY")
		private String city;
		
		@JsonProperty("STATE")
		private String state;
		
		@JsonProperty("ZIP")
		private String zip;
		
		@JsonProperty("CLAT")
		private double latitude;
		
		@JsonProperty("CLON")
		private double longitude;
	}
}
