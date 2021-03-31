package abareaso.io.jvwatch.clinics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import abareaso.io.jvwatch.feign.VaccineSpotterClient;
import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.model.VaccineSpotterResp;
import abareaso.io.jvwatch.model.VaccineSpotterResp.VaccineSpotterFeature;

/**
 * An abstract base class that uses the VaccineSpotter API which has the ability retrieve appointment data
 * for multiple clinic "brands".  Sub-classes should be created to filter the result set by the specific
 * clinic brand.  It retrieves vaccine appointment data from HyVee clinics.  This implementation uses the global 
 * jvwatch.states, jvwatch.radius, jvwatch.latitude, and jvwatch.longitude properties to control what general locations
 * are queried for appointment data.
 * @author Greg Meyer
 */
public abstract class VaccineSpotterClinic implements Clinic
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HyVeeClinic.class);		
	
	protected ClinicSearchProperties props;
	
	@Autowired
	protected VaccineSpotterClient vsClient;
	
	public VaccineSpotterClinic(ClinicSearchProperties props)
	{
		this.props = props;
	}
	
	@Override
	public List<ClinicData> getClinicAppointements()
	{
		final List<ClinicData> retVal = new LinkedList<>();
		
		for (String state : props.getStates())
		{
			try
			{
				final VaccineSpotterResp features =  vsClient.getFeatures(state).block();
				
				final List<VaccineSpotterResp.VaccineSpotterFeature> matchingLocs = 
						features.getFeatures().stream().filter(feature -> shouldIncludeLocation(feature)).collect(Collectors.toList());
				
				for (VaccineSpotterResp.VaccineSpotterFeature feature : matchingLocs)
				{
					if (feature.getProperties().isAppointments_available())
					{
					
						final List<Date> dates = getAptInfo(feature.getProperties());
						
						retVal.add(buildClinicData(feature, dates, true));
					}
					else
					{
						retVal.add(buildClinicData(feature, Collections.emptyList(), false));
					}
				}
			}
			catch (Exception e)
			{
				LOGGER.error("Error retrieving Vaccine spotter data : {}", e.getMessage(), e);
			}
		}
		
		return retVal;
	}
	
	@SuppressWarnings("deprecation")
	protected List<Date> getAptInfo(VaccineSpotterResp.VaccineSpotterProperties props)
	{
		final  String pattern ="yyyy-MM-dd";
		
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		
		final LinkedList<Long> times = new LinkedList<>();
		
		for (VaccineSpotterResp.VaccineSpotterAppointment appt : props.getAppointments())
		{
			try 
			{
				final Date dt = simpleDateFormat.parse(appt.getTime().substring(0, 10));
				times.add(dt.getTime());
			} 
			catch (ParseException e) 
			{
				LOGGER.error("Error parsing Vaccine spotter date data : {}", e.getMessage(), e);
			}
		}
		
		Collections.sort(times);
		
		final List<Date> dates = new ArrayList<>();
		
		if (times.size() > 0)
		{
			Date earliest = new Date(times.getFirst());
			dates.add(earliest);
			
			if (times.size() > 1)
			{
				Date latest = new Date(times.getLast());
				
				if (latest.getDate() != earliest.getDate())
					dates.add(latest);
			}
		
		}
		
		return dates;
	}	
	
	protected double distance(double lat1, double lon1, double lat2, double lon2) 
	{
		if ((lat1 == lat2) && (lon1 == lon2)) 
		{
			return 0;
		}
		else 
		{
			double theta = lon1 - lon2;
			double dist = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
			dist = Math.acos(dist);
			dist = Math.toDegrees(dist);
			dist = dist * 60 * 1.1515;

			return (dist);
		}
	}
	
	/**
	 * Indicates whether or not the clinic should be included in the result set.  This method should filter by 
	 * clinic brand name and the location should be within the radius of the global latitude and longitude configuration
	 * settings. 
	 * @param feature The clinic that is being filtered.
	 * @return Returns true if the clinic brand matches the desired brand and if the clinic location is within the desired location radius.
	 * Return false other wise.
	 */
	protected boolean shouldIncludeLocation(VaccineSpotterFeature feature) 
	{
		boolean retVal = false;
		
		if (feature.getProperties().getProvider_brand().equals(getBrandName()))
		{
			double locLat = feature.getGeometry().getCoordinates().get(1);
			double locLong = feature.getGeometry().getCoordinates().get(0);
			
			double distance = distance(props.getLatitude(), props.getLongitude(), locLat, locLong);
			
			if (distance <= props.getRadius())
				retVal = true;
		}
		
		return retVal;
	}
	
	/**
	 * Builds a ClinicData structure from the provided clinic.
	 * @param feature The clinic that has been queried.
	 * @param dates The first available appointment date and last appointment date (if available).  The first entry
	 * in the list will be the first available appointment and the second entry will be the last available appointment. 
	 * If the first date and the last date are the same, this list will only contain the first appointment date (i.e. a list of size 1).  
	 * @param available Indicates if vaccine appointments are available at the clinic.
	 * @return
	 */
	protected ClinicData buildClinicData(VaccineSpotterResp.VaccineSpotterFeature feature, List<Date> dates, boolean available)
	{
		final ClinicData data = new ClinicData();
		
		final StringBuilder id = new StringBuilder(props.getCachePrefix()).append(getBrandName()).append("-").append(feature.getProperties().getId());
		
		data.setLink(feature.getProperties().getUrl());
		data.setId(id.toString());
		data.setName(getDisplayName() + " " + feature.getProperties().getCity());
		data.setState(feature.getProperties().getState());
		data.setZipCode(feature.getProperties().getPostal_code());
		
		if (dates.size() > 0)
		{
			data.setEariestApptDay(dates.get(0));
			
			if (dates.size() > 1)
				data.setLatestApptDay(dates.get(1));
		}		
		
		if (StringUtils.hasText(feature.getProperties().getAppointments_last_fetched()))
		{
			Date dt = null;
			try
			{
				DateTimeFormatter parser = ISODateTimeFormat.dateTime();
				dt = parser.parseDateTime(feature.getProperties().getAppointments_last_fetched()).toDate();
				
			}
			catch (Exception e)
			{

			}
			
			if (dt == null)
			{
				try
				{
					DateTimeFormatter parser = ISODateTimeFormat.dateTimeNoMillis();
					dt = parser.parseDateTime(feature.getProperties().getAppointments_last_fetched()).toDate();
					
				}
				catch (Exception e)
				{
					LOGGER.error("Error parsing {} date data : {}", getDisplayName(), e.getMessage(), e);
				}				
			}
			
			if (dt != null)
			{
				data.setLastFetched(dt);
			}
		}
		
		data.setAvailable(available);
		
		return data;
	}	
	
	/**
	 * The brand name of the clinic that determines if the clinic should be included in the result set.
	 * @return The brand name of the clinic that determines if the clinic should be included in the result set.
	 */
	protected abstract String getBrandName();
	
	/**
	 * The display name of the clinic to use when publishing the clinic data.
	 * @return The isplay name of the clinic to use when publishing the clinic data.
	 */
	protected abstract String getDisplayName();
}
