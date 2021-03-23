package abareaso.io.jvwatch.feign;

import org.springframework.web.bind.annotation.GetMapping;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Mono;

@ReactiveFeignClient(name = "cvs-client", url = "${jvwatch.clinics.cvs.service.url:}")
public interface CVSClient 
{
    @GetMapping 
    public Mono<String> getAppointments();
}
