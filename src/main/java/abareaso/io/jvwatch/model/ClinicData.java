package abareaso.io.jvwatch.model;


import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

/**
 * Data model for a clinic's appointment availability.  It is also
 * stored in Redis to maintain the clinic's appointment publish state.
 * @author Greg
 *
 */
@RedisHash("jvwatch")
@Data
public class ClinicData 
{
	@Id
	private String id;
	
	private String state;
	
	private String zipCode;
	
	private String name;
	
	private String link;
	
	private Date eariestApptDay;
	
	private Date latestApptDay;
	
	private Date lastFetched;
	
	private boolean available;
}
