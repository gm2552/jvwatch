package abareaso.io.jvwatch.model;


import java.util.Date;

import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import lombok.Data;

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
}
