package abareaso.io.jvwatch.clinics;

import java.util.Date;
import java.util.List;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.model.VaccineSpotterResp;
import abareaso.io.jvwatch.model.VaccineSpotterResp.VaccineSpotterFeature;

/**
 * Retrieves vaccine appointment data from Walgreens clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointement data.
 * @author Greg Meyer
 */
@Component
public class WalgreensClinic extends VaccineSpotterClinic
{
	private static final Logger LOGGER = LoggerFactory.getLogger(WalgreensClinic.class);	
	
	public WalgreensClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected ClinicData buildClinicData(VaccineSpotterResp.VaccineSpotterFeature feature, List<Date> dates, boolean available)
	{
		final ClinicData data = new ClinicData();
		
		final StringBuilder id = new StringBuilder(props.getCachePrefix()).append("walgreens-").append(feature.getProperties().getId());
		
		data.setLink(feature.getProperties().getUrl());
		data.setId(id.toString());
		data.setName("Walgreens " + feature.getProperties().getCity());
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
			DateTimeFormatter parser = ISODateTimeFormat.dateTime();
			
			try
			{
				final Date dt = parser.parseDateTime(feature.getProperties().getAppointments_last_fetched()).toDate();
				data.setLastFetched(dt);
			}
			catch (Exception e)
			{
				LOGGER.error("Error parsing walgreens date data : {}", e.getMessage(), e);
			}
		}
		
		data.setAvailable(available);
		
		return data;
	}	
	
	@Override
	protected boolean shouldIncludeLocation(VaccineSpotterFeature feature) 
	{
		boolean retVal = false;
		
		if (feature.getProperties().getProvider_brand().equals("walgreens"))
		{
			double locLat = feature.getGeometry().getCoordinates().get(1);
			double locLong = feature.getGeometry().getCoordinates().get(0);
			
			double distance = distance(props.getLatitude(), props.getLongitude(), locLat, locLong);
			
			if (distance <= props.getRadius())
				retVal = true;
		}
		
		return retVal;
	}

}
