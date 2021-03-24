package abareaso.io.jvwatch.clinics;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	private static final Logger LOGGER = LoggerFactory.getLogger(CVSClinic.class);	
	
	protected ClinicSearchProperties props;
	
	@Value("${jvwatch.clinics.cvs.cities}")
	protected List<String> cities;	
	
	@Value("${jvwatch.clinics.cvs.apptLink}")
	protected String apptLink;	
	
	@Value("${jvwatch.clinics.cvs.service.lockoutUrl}")
	protected String lockoutUrl;	
	
	@Autowired
	protected CVSClient cvsClient;
	
	public CVSClinic(ClinicSearchProperties props)
	{
		this.props = props;
	}
	
	public ClinicAvailability getLocations()
	{
		final ClinicAvailability retVal = new ClinicAvailability();
		
		try
		{
			final String json = cvsClient.getAppointments().block();
			
			final JSONObject stateObjects = new JSONObject(json).getJSONObject("responsePayloadData").getJSONObject("data");
			
			for (String state : props.getStates())
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

									LOGGER.info("Would have notified for CVS {}, {} but site is locked out", clinic.getString("city"), state);
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
								LOGGER.warn("Unknown location status from CVS: {}", clinic.getString("status"));
							}
						}
					}
				}
				catch (Exception e)
				{
					LOGGER.error("Error parsing clinic data from CVS state {} data: {}", state, e.getMessage(), e);
				}
			}
		}
		catch (Exception e)
		{
			LOGGER.error("Error retrieving clinic data from CVS : {}", e.getMessage(), e);
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
		
		final StringBuilder id = new StringBuilder(props.getCachePrefix()).append("cvs-").append(clinic.getString("state")).append("-").append(clinic.getString("city"));
		
		data.setId(id.toString());
		data.setName("CVS " + clinic.getString("city"));
		data.setState(clinic.getString("state"));
		data.setLink(apptLink);
		
		return data;
	}
}
