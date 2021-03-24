package abareaso.io.jvwatch.clinics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import abareaso.io.jvwatch.feign.VaccineSpotterClient;
import abareaso.io.jvwatch.model.ClinicAvailability;
import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.model.VaccineSpotterResp;

public abstract class VaccineSpotterClinic implements Clinic
{
	private static final Logger LOGGER = LoggerFactory.getLogger(HyVeeClinic.class);		
	
	@Value("${jvwatch.cachePrefix:}")
	protected String cachePrefix;	
	
	@Value("${jvwatch.states}")
	protected List<String> states;
	
	@Value("${jvwatch.radius}")
	protected int radius;
	
	@Value("${jvwatch.latitude}")
	protected double latitude;	
	
	@Value("${jvwatch.longitude}")
	protected double longitude;	
	
	@Autowired
	protected VaccineSpotterClient vsClient;
	
	@Override
	public ClinicAvailability getLocations() 
	{
		final ClinicAvailability retVal = new ClinicAvailability();
		
		for (String state : states)
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
						
						retVal.getAvailable().add(buildClinicData(feature, dates));
					}
					else
					{
						retVal.getUnavailable().add(buildClinicData(feature, Collections.emptyList()));
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
	
	protected abstract boolean shouldIncludeLocation(VaccineSpotterResp.VaccineSpotterFeature feature);
	
	protected abstract ClinicData buildClinicData(VaccineSpotterResp.VaccineSpotterFeature feature, List<Date> dates);
}
