package abareaso.io.jvwatch.notifications;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * A utility class for shortening URLs.  This is generally used for the appointment link for a 
 * given clinic. 
 * @author Greg Meyer
 *
 */
public class TinyURLCreator 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(TinyURLCreator.class);	
	
	/**
	 * Converts a URL to a tiny URL.
	 * @param URL The URL to convert.
	 * @return A tiny URL representation of the original URL.  If the URL cannot be converted, this function
	 * will return the original URL.
	 */
	public static String toTinyURL(String URL)
	{
		
		
		final RestTemplate templ = new RestTemplate();
		
		try
		{
			final ResponseEntity<String> resp = templ.exchange("http://tinyurl.com/api-create.php?url=" + URL, HttpMethod.GET, null, String.class);
			
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
