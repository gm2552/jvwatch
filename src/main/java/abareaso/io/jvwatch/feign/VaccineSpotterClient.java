package abareaso.io.jvwatch.feign;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import abareaso.io.jvwatch.model.VaccineSpotterResp;
import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "vaccine-spotter-client", url = "${jvwatch.clinics.vaccine-spotter.service.url:}")
public interface VaccineSpotterClient 
{
	@GetMapping("/{state}.json")
	public Mono<VaccineSpotterResp> getFeatures(@PathVariable("state") String state);
}
