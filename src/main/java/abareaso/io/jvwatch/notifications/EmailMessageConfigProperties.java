package abareaso.io.jvwatch.notifications;

import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jvwatch.notifications.email") 
public class EmailMessageConfigProperties 
{
	private boolean enabled;
	
	private String from;
	
	private List<String> to;
	
	private String subject;

	public boolean isEnabled() 
	{
		return enabled;
	}

	public void setEnabled(boolean enabled) 
	{
		this.enabled = enabled;
	}

	public String getFrom() 
	{
		return from;
	}

	public void setFrom(String from) 
	{
		this.from = from;
	}

	public List<String> getTo() 
	{
		return to;
	}

	public void setTo(List<String> to) 
	{
		this.to = to;
	}

	public String getSubject() 
	{
		return subject;
	}

	public void setSubject(String subject) 
	{
		this.subject = subject;
	}
	
	
}
