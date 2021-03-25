package abareaso.io.jvwatch.feign;

import org.springframework.web.bind.annotation.GetMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * Reactive feign client for retrieving CVS clinic data.
 * @author Greg Meyer
 *
 */
@ReactiveFeignClient(name = "cvs-client", url = "${jvwatch.clinics.cvs.service.url:}")
public interface CVSClient 
{
	/**
	 * Retrieves all appointments for all clinics nation wide (the API does not have a location filter).
	 * @return A JSON String of all appointment date.  Because of the way CVS has modeled the JSON, it is 
	 * easier to use a JSON library to navigate the structure vs using object mapping to a POJO.
	 */
    @GetMapping 
    public Mono<String> getAppointments();
}
