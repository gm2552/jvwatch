package abareaso.io.jvwatch.clinics;

import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import abareaso.io.jvwatch.feign.CVSClient;
import abareaso.io.jvwatch.model.ClinicData;

/**
 * Retrieves vaccine appointment data from CVS clinics.  This implementation uses the global 
 * jvwatch.states property to control what states are queried for appointment data.  It also
 * uses an additional city filter (jvwatch.clinics.cvs.cities) to further control the list of 
 * appointment data.  The city filter is inclusive only, so all cities that are desired MUST 
 * be included in the city list (they also should be in all upper case).
 * @author Greg Meyer
 *
 */
@Service
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
	
	public List<ClinicData> getClinicAppointements()
	{
		final List<ClinicData> retVal = new LinkedList<>();
		
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
									
									retVal.add(buildClinicData(clinic, true));
								}
							}
							else if (clinic.getString("status").equalsIgnoreCase("Fully Booked"))
							{
								retVal.add(buildClinicData(clinic, false));
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
			final WebClient webClient = WebClient.create(lockoutUrl);
			 
			final String jsonResp = webClient.post()
			        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
			        .retrieve()
			        .bodyToMono(String.class).block();
			
			if (StringUtils.hasText(jsonResp))
				return false;
			
			return (jsonResp.contains("Please check back later"));
		}
		catch (Exception e)
		{
			return true;
		}
	}
	
	protected ClinicData buildClinicData(JSONObject clinic, boolean available)
	{
		final ClinicData data = new ClinicData();
		
		final StringBuilder id = new StringBuilder(props.getCachePrefix()).append("cvs-").append(clinic.getString("state")).append("-").append(clinic.getString("city"));
		
		data.setId(id.toString());
		data.setName("CVS " + clinic.getString("city"));
		data.setState(clinic.getString("state"));
		data.setLink(apptLink);
		data.setAvailable(available);
		
		return data;
	}
}
