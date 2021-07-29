package abareaso.io.jvwatch.functions;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.function.context.PollableBean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import abareaso.io.jvwatch.clinics.Clinic;
import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.repository.ClinicDataRepository;
import reactor.core.publisher.Flux;

/**
 * Configure a pollable functional bean that is supplier (i.e. source) of clinic appointment data.
 * The amount of time between each poll is controlled by the jvwatch.checkTask.period configuration
 * setting.  This setting is in milliseconds.
 * @author Greg Meyer
 *
 */
@Configuration
@Profile("supplier")
public class VaccineApptSourceConfiguration 
{	
	/**
	 * List of configured clinics that are offering vaccine appointment data.
	 */
	@Qualifier("enabledClinics")
	@Autowired
	protected List<Clinic> enabledClinics;
	
	/**
	 * A repository to store the state of a clinic's appointments.  If a clinic
	 * exists in the repository, then it represents a state where appointments are currently
	 * available and we have published the appointment information.  When appointments are no
	 * longer available, the clinic will be removed from the repository to represent this state.
	 */
	@Autowired
	protected ClinicDataRepository redisRepo;
	
	/**
	 * Periodically polls clinics for newly available or newly unavailable
	 * vaccine appointments.  Appointmentss are published via a reactive supplier 
	 * paradigm.
	 * @return A function that supplies a Flux of clinics with available or 
	 * unavailable appointments.
	 */
	@PollableBean
	public Supplier<Flux<ClinicData>> vaccineClinicDataSupplier()
	{
		return () ->
		{
			final List<ClinicData> fullClinicData = new LinkedList<>();
			
			/*
			 * Collect data from the enabled clinic sources
			 */
			for (Clinic clinic : enabledClinics)
				fullClinicData.addAll(clinic.getClinicAppointements());
			
			/*
			 * Filter through list to find only those clinics whose
			 * data should be published.
			 */
			final List<ClinicData> publishableData = fullClinicData.stream().filter(clinic -> 
			{
				if (clinic.isAvailable())
				{
					if (!redisRepo.findById(clinic.getId()).isPresent())
					{
						redisRepo.save(clinic);
						return true;
					}
				}
				else
				{
					if (redisRepo.findById(clinic.getId()).isPresent())
					{
						redisRepo.deleteById(clinic.getId());
						return true;
					}				
				}				
				
				return false;
			}).collect(Collectors.toList());

			/*
			 * Hand off the list of publishable clinic data to
			 * the stream via a Flux.
			 */

			return Flux.fromIterable(publishableData);
		};
	}
}
