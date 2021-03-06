package abareaso.io.jvwatch.clinics;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.model.HyVeeApptRequest;
import abareaso.io.jvwatch.model.HyVeeManufacturerIdRequest;
import abareaso.io.jvwatch.model.HyVeeTimeSlotRequest;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * Retrieves vaccine appointment data from HyVee clinics.  This implementation uses the global 
 * jvwatch.radius, jvwatch.latitude, and jvwatch.longitude properties to control what general locations
 * are queried for appointment data.
 * @author Greg Meyer
 */
@Service
@Slf4j
public class HyVeeClinic implements Clinic
{	
	protected ClinicSearchProperties props;
	
	@Value("${jvwatch.clinics.hyvee.apptLink}")
	protected String apptLink;	
	
	public HyVeeClinic(ClinicSearchProperties props)
	{
		this.props = props;
	}
	
	@Override
	public List<ClinicData> getClinicAppointements()
	{
		final List<ClinicData> retVal = new LinkedList<>();
		
		final HyVeeApptRequest.Variables vars = new HyVeeApptRequest.Variables();
		vars.setLatitude(props.getLatitude());
		vars.setLongitude(props.getLongitude());
		vars.setRadius(props.getRadius());
		
		final HyVeeApptRequest req = new HyVeeApptRequest();
		req.setVariables(vars);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.ALL));
		
		try
		{			
			final WebClient webClient = WebClient.create("https://www.hy-vee.com/my-pharmacy/api/graphql");
			 
			final String jsonResp = webClient.post()
			        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			        .body(Mono.just(req), HyVeeApptRequest.class)
			        .retrieve()
			        .bodyToMono(String.class).block();
			
			if (StringUtils.hasText(jsonResp))
			{
				final JSONArray stateObjects = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("searchPharmaciesNearPoint");
				
				for (Object ob : stateObjects)
				{
					final JSONObject clinic = ((JSONObject)ob).getJSONObject("location");
					
					if (clinic.getBoolean("isCovidVaccineAvailable"))
					{
						final List<Date> dates = getAptInfo(clinic.getString("locationId"));
						
						retVal.add(buildClinicData(clinic, dates, true));
					}
					else
					{
						retVal.add(buildClinicData(clinic, Collections.emptyList(), false));
					}
				}
			}
			
		}
		catch (Exception e)
		{
			log.error("Error retrieving clinic data from HyVee : {}", e.getMessage(), e);
		}
		return retVal;
	}

	@SuppressWarnings("deprecation")
	protected List<Date> getAptInfo(String locId)
	{
		final  String pattern ="MM/dd/yyyy";
		
		final SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		
		final List<String> manIds = this.getAvailableManufacturerIds(locId); 
		
		final LinkedList<String> times = new LinkedList<>();
		
		for (String manId : manIds)
		{
			times.addAll(getAvailableApptTimeSlots(locId, manId));
		}
		
		Collections.sort(times);
		
		
		
		final List<Date> dates = new ArrayList<>();
		
		if (times.size() > 0)
		{
			try 
			{
				Date earliest = simpleDateFormat.parse(times.getFirst().substring(0, 10));
				dates.add(earliest);
				
				if (times.size() > 1)
				{
					Date latest = simpleDateFormat.parse(times.getLast().substring(0, 10));
					
					if (latest.getDate() != earliest.getDate())
						dates.add(latest);
				}
			} 
			catch (ParseException e) 
			{
				log.error("Error parsing HyVee date data : {}", e.getMessage(), e);
			}
		
		}
		
		return dates;
	}
	
	protected List<String> getAvailableManufacturerIds(String locId)
	{
		final List<String> retVal  = new LinkedList<>();
		
		final HyVeeManufacturerIdRequest.Variables vars = new HyVeeManufacturerIdRequest.Variables();
		vars.setLocationId(locId);
		
		final HyVeeManufacturerIdRequest req = new HyVeeManufacturerIdRequest();
		req.setVariables(vars);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		try
		{
			final WebClient webClient = WebClient.create("https://www.hy-vee.com/my-pharmacy/api/graphql");
			 
			final String jsonResp = webClient.post()
			        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			        .body(Mono.just(req), HyVeeManufacturerIdRequest.class)
			        .retrieve()
			        .bodyToMono(String.class)
			        .block();			
			
			final JSONArray availObjects = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("getCovidVaccineLocationAvailability");
			
			for (Object ob : availObjects)
			{
				final JSONObject man = (JSONObject)ob;
				
				if (man.getBoolean("hasAvailability"))
				{
					retVal.add(man.getString("covidVaccineManufacturerId"));
				}
			}
		}
		catch (Exception e)
		{
			log.error("Error retrieving HyVee manufacturer data : {}", e.getMessage(), e);
		}
		
		return retVal;
	}
	
	protected LinkedList<String> getAvailableApptTimeSlots(String locId, String manId)
	{
		final LinkedList<String> retVal = new LinkedList<>();
		
		final HyVeeTimeSlotRequest.Variables vars = new HyVeeTimeSlotRequest.Variables();
		vars.setLocationId(locId);
		vars.setCovidVaccineManufacturerId(manId);
		
		final HyVeeTimeSlotRequest req = new HyVeeTimeSlotRequest();
		req.setVariables(vars);
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		try
		{
			final WebClient webClient = WebClient.create("https://www.hy-vee.com/my-pharmacy/api/graphql");
			 
			final String jsonResp = webClient.post()
			        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			        .body(Mono.just(req), HyVeeTimeSlotRequest.class)
			        .retrieve()
			        .bodyToMono(String.class).block();				
			
			if (StringUtils.hasText(jsonResp))
			{
				final JSONArray availTimes = new JSONObject(jsonResp).getJSONObject("data").getJSONArray("getCovidVaccineTimeSlots");

				for (Object time : availTimes)
				{
					retVal.add(time.toString());
				}
			}
		}
		catch (Exception e)
		{
			log.error("Error retrieving HyVee appointment data : {}", e.getMessage(), e);
		}
		
		return retVal;
	}
	
	protected ClinicData buildClinicData(JSONObject clinic, List<Date> dates, boolean available)
	{
		final ClinicData data = new ClinicData();
		
		final StringBuilder id = new StringBuilder(props.getCachePrefix()).append("hyvee-").append(clinic.getString("locationId"));
		
		data.setId(id.toString());
		data.setName("Hy-Vee " + clinic.getString("name"));
		data.setState(clinic.getJSONObject("address").getString("state"));
		data.setZipCode(clinic.getJSONObject("address").getString("zip"));
		data.setLink(apptLink);
		
		if (dates.size() > 0)
		{
			data.setEariestApptDay(dates.get(0));
			
			if (dates.size() > 1)
				data.setLatestApptDay(dates.get(1));
		}
		
		data.setAvailable(available);
		
		return data;
	}
}
