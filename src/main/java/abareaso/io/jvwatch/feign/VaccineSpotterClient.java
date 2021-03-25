package abareaso.io.jvwatch.feign;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import abareaso.io.jvwatch.model.VaccineSpotterResp;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * Reactive feign client for retrieving clinic data from the VaccineSpotterAPI.
 * @author Greg Meyer
 *
 */
@ReactiveFeignClient(name = "vaccine-spotter-client", url = "${jvwatch.clinics.vaccine-spotter.service.url:}")
public interface VaccineSpotterClient 
{
	/**
	 * Retrieves all appointment data for a given state.
	 * @param state The state to pull appointment data for.
	 * @return A VaccineSpotterResp structure that contains all of the clinics offering appointments for the given state.
	 */
	@GetMapping("/{state}.json")
	public Mono<VaccineSpotterResp> getFeatures(@PathVariable("state") String state);
}
