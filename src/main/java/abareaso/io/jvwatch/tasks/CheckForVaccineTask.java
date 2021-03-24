package abareaso.io.jvwatch.tasks;

import java.util.LinkedList;
import java.util.List;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import abareaso.io.jvwatch.clinics.Clinic;
import abareaso.io.jvwatch.model.ClinicAvailability;
import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.notifications.NotificationsPublisher;
import abareaso.io.jvwatch.repository.ClinicDataRepository;
@Component
public class CheckForVaccineTask 
{	
	@Qualifier("enabledClinics")
	@Autowired
	protected List<Clinic> enabledClinics;
	
	@Autowired
	protected ClinicDataRepository redisRepo;
	
	@Autowired
	protected NotificationsPublisher publisher;
	
	@Scheduled(fixedRateString = "${jvwatch.checkTask.period:60000}")
	public void checkForVaccine()
	{
		final List<ClinicData> availableClinics = new LinkedList<>();
		
		final List<ClinicData> unavailableClinics = new LinkedList<>();
		
		final List<ClinicData> newlyAvailableLocations = new LinkedList<>();
		
		final List<ClinicData> newlyUnavailableLocations = new LinkedList<>();
		
		for (Clinic clinic : enabledClinics)
		{
			final ClinicAvailability avail = clinic.getLocations();
			
			availableClinics.addAll(avail.getAvailable());
			unavailableClinics.addAll(avail.getUnavailable());
		}
		
		for (ClinicData availClinic : availableClinics)
		{
			if (!redisRepo.findById(availClinic.getId()).isPresent())
			{
				newlyAvailableLocations.add(availClinic);
				redisRepo.save(availClinic);
			}
		}
		
		for (ClinicData unavailClinic : unavailableClinics)
		{
			if (redisRepo.findById(unavailClinic.getId()).isPresent())
			{
				newlyUnavailableLocations.add(unavailClinic);
				redisRepo.deleteById(unavailClinic.getId());
			}
		}
		
		for (ClinicData newAvailClinic : newlyAvailableLocations)
		{
			publisher.publishAvailableNotification(newAvailClinic);
		}
		
		for (ClinicData newUnavailClinic : newlyUnavailableLocations)
		{
			publisher.publishUnavailableNotification(newUnavailClinic);
		}
	}
}
