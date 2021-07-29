package abareaso.io.jvwatch.functions;

import java.util.function.Consumer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.notifications.NotificationsPublisher;

/**
 * Creates a functional bean that is a consumer (i.e. sink) for clinic appointment 
 * data.  The consumer function uses the NotificationsPublisher to publish the appointments
 * to the configured destinations.
 * @author Greg Meyer
 */
@Configuration
@Profile("consumer")
public class VaccineApptSinkConfiguration 
{
	@Autowired
	protected NotificationsPublisher publisher;
	
	@Bean
	public Consumer<ClinicData> vaccineClinicDataSink()
	{
		return data ->
		{
			publisher.publishNotification(data);
		};
	}
	
}
