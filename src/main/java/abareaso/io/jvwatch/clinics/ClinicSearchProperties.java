package abareaso.io.jvwatch.clinics;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration properties for clinic search parameters.  These are global 
 * parameters applied to many of the clinic implementations.  Other clinic
 * implementations may require implementation specific configuration information.
 * @author Greg Meyer
 *
 */
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

	/**
	 * Gets the cache prefix to be pre-appended to the cache key.  This is useful
	 * when running multiple instances of this application (as a data source) against the same cache
	 * instance (ex: a single instance of Redis used by multiple application instances).
	 * @return The cache prefix name.  Defaults to "" which if fine when only running a single instance of this application.
	 */
	public String getCachePrefix() 
	{
		return cachePrefix;
	}

	public void setCachePrefix(String cachePrefix) 
	{
		this.cachePrefix = cachePrefix;
	}
	
	
}
