package abareaso.io.jvwatch.clinics;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import abareaso.io.jvwatch.feign.CVSClient;
import abareaso.io.jvwatch.model.ClinicAvailability;
import abareaso.io.jvwatch.model.ClinicData;

@Component
public class CVSClinic implements Clinic
{
	@Value("${jvwatch.states}")
	protected List<String> states;
	
	@Value("${jvwatch.clinics.cvs.cities}")
	protected List<String> cities;	
	
	@Value("${jvwatch.cachePrefix:}")
	protected String cachePrefix;	

	@Value("${jvwatch.clinics.cvs.apptLink}")
	protected String apptLink;	
	
	@Value("${jvwatch.clinics.cvs.service.lockoutUrl}")
	protected String lockoutUrl;	
	
	@Autowired
	protected CVSClient cvsClient;
	
	public ClinicAvailability getLocations()
	{
		final ClinicAvailability retVal = new ClinicAvailability();
		
		final String json = cvsClient.getAppointments().block();
		
		try
		{
			final JSONObject stateObjects = new JSONObject(json).getJSONObject("responsePayloadData").getJSONObject("data");
			
			for (String state : states)
			{
				try
				{
					final JSONArray stateClinics = stateObjects.getJSONArray(state);
					
					for (Object ob : stateClinics)
					{
						final JSONObject clinic = (JSONObject)ob;
						
						if (cities.contains(clinic.getString("city")))
						{
							if (clinic.getString("status").equalsIgnoreCase("Available"))
							{
								if (siteLockedOut())
								{
									// Warning
								}
								else
								{
									retVal.getAvailable().add(buildClinicData(clinic));
								}
							}
							else if (clinic.getString("status").equalsIgnoreCase("Fully Booked"))
							{
								retVal.getUnavailable().add(buildClinicData(clinic));
							}
							else
							{
								// warning
							}
						}
					}
				}
				catch (Exception e)
				{
					
				}
			}
		}
		catch (Exception e)
		{
			// warning
		}
		return retVal;
	}
	
	protected boolean siteLockedOut()
	{
		try
		{
			final  RestTemplate templ = new RestTemplate();
			
			final ResponseEntity<String> ent = templ.exchange(lockoutUrl, HttpMethod.GET, null, String.class);
			
			if (ent.getStatusCodeValue() != 200)
				return false;
			
			if (StringUtils.hasText(ent.getBody()))
				return false;
			
			return (ent.getBody().contains("Please check back later"));
		}
		catch (Exception e)
		{
			return true;
		}
	}
	
	protected ClinicData buildClinicData(JSONObject clinic)
	{
		final ClinicData data = new ClinicData();
		
		final StringBuilder id = new StringBuilder(cachePrefix).append("cvs-").append(clinic.getString("state")).append("-").append(clinic.getString("city"));
		
		data.setId(id.toString());
		data.setName("CVS " + clinic.getString("city"));
		data.setState(clinic.getString("state"));
		data.setLink(apptLink);
		
		return data;
	}
}
