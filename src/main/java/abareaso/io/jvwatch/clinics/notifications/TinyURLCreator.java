package abareaso.io.jvwatch.clinics.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class TinyURLCreator 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TinyURLCreator.class);	
	
	public static String toTinyURL(String URL)
	{
		
		
		final RestTemplate templ = new RestTemplate();
		
		try
		{
			ResponseEntity<String> resp = templ.exchange("http://tinyurl.com/api-create.php?url=" + URL, HttpMethod.GET, null, String.class);
			
			if (resp.getStatusCodeValue() == 200)
				return resp.getBody();
		}
		catch (Exception e)
		{
			LOGGER.warn("Failed to create tiny URL for url {}.  Will fall back to long URL.", URL);
		}
		
		return "";
	}
}
