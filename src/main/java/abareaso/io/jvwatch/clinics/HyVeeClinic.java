package abareaso.io.jvwatch.clinics;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import abareaso.io.jvwatch.model.ClinicAvailability;
import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.model.HyVeeRequest;
import abareaso.io.jvwatch.model.HyVeeRequest.Variables;

@Component
public class HyVeeClinic implements Clinic
{	
	@Value("${jvwatch.radius}")
	protected int radius;
	
	@Value("${jvwatch.latitude}")
	protected double latitude;	
	
	@Value("${jvwatch.longitude}")
	protected double longitude;	
	
	@Value("${jvwatch.cachePrefix:}")
	protected String cachePrefix;		
	
	@Value("${jvwatch.clinics.hyvee.apptLink}")
	protected String apptLink;	
	
	@Override
	public ClinicAvailability getLocations() 
	{
		final ClinicAvailability retVal = new ClinicAvailability();
		
		final Variables vars = new Variables();
		vars.setLatitude(latitude);
		vars.setLongitude(longitude);
		vars.setRadius(radius);
		
		final HyVeeRequest req = new HyVeeRequest();
		req.setVariables(vars);
		final RestTemplate template = new RestTemplate();
		
		final HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		final HttpEntity<HyVeeRequest> entity = new HttpEntity<>(req, headers);
		
		try
		{
			final ResponseEntity<String> resp = template.exchange("https://www.hy-vee.com/my-pharmacy/api/graphql", HttpMethod.POST, entity, String.class);
			if (resp.getStatusCodeValue() == 200)
			{
				final JSONArray stateObjects = new JSONObject(resp.getBody()).getJSONObject("data").getJSONArray("searchPharmaciesNearPoint");
				
				for (Object ob : stateObjects)
				{
					final JSONObject clinic = ((JSONObject)ob).getJSONObject("location");
					
					if (clinic.getBoolean("isCovidVaccineAvailable"))
					{
						retVal.getAvailable().add(buildClinicData(clinic));
					}
					else
					{
						retVal.getUnavailable().add(buildClinicData(clinic));
					}
				}
			}
			
		}
		catch (Exception e)
		{
			// warning
		}
		return retVal;
	}

	protected ClinicData buildClinicData(JSONObject clinic)
	{
		final ClinicData data = new ClinicData();
		
		final StringBuilder id = new StringBuilder(cachePrefix).append("hyvee-").append(clinic.getString("locationId"));
		
		data.setId(id.toString());
		data.setName("Hy-Vee " + clinic.getString("name"));
		data.setState(clinic.getJSONObject("address").getString("state"));
		data.setZipCode(clinic.getJSONObject("address").getString("zip"));
		data.setLink(apptLink);
		
		return data;
	}
}
