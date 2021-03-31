package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;

/**
 * Retrieves vaccine appointment data from Sams Club clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointment data.
 * @author Greg Meyer
 */
@Service
public class SamsClubClinic extends VaccineSpotterClinic
{	
	public SamsClubClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "sams_club";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Sam's Club";
	}
}
