package abareaso.io.jvwatch.feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import abareaso.io.jvwatch.model.PublixStoresResp;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

/**
 * Reactive feign client for retrieving Publix store locations.
 * @author Greg Meyer
 *
 */
@ReactiveFeignClient(name = "publix-client", url = "${jvwatch.clinics.publix.service.url:}")
public interface PublixClient 
{
	/**
	 * Retrieves all publix locations given city/state.
	 * @param state The state to pull stores from.
	 * @param city The city to pull stores from.
	 * @return A PublixStoresResp structure that contains all of the stores for the given city/state.
	 */
	@GetMapping("storelocation")
	public Mono<PublixStoresResp> getStores(@RequestParam("state") String state, @RequestParam("city") String city);
}
