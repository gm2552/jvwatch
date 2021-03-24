package abareaso.io.jvwatch.clinics;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jvwatch") 
public class ClinicSearchProperties 
{
	private List<String> states;
	
	private double latitude;
	
	private double longitude;
	
	private int radius;
	
	private String cachePrefix = "";

	public List<String> getStates() 
	{
		return states;
	}

	public void setStates(List<String> states) 
	{
		this.states = states;
	}

	public double getLatitude() 
	{
		return latitude;
	}

	public void setLatitude(double latitude) 
	{
		this.latitude = latitude;
	}

	public double getLongitude() 
	{
		return longitude;
	}

	public void setLongitude(double longitude) 
	{
		this.longitude = longitude;
	}

	public int getRadius() 
	{
		return radius;
	}

	public void setRadius(int radius) 
	{
		this.radius = radius;
	}

	public String getCachePrefix() 
	{
		return cachePrefix;
	}

	public void setCachePrefix(String cachePrefix) 
	{
		this.cachePrefix = cachePrefix;
	}
	
	
}
