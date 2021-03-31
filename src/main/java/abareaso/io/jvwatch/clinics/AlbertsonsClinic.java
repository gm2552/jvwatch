package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;

/**
 * Retrieves vaccine appointment data from Albertsons clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointment data.
 * @author Greg Meyer
 */
@Service
public class AlbertsonsClinic extends VaccineSpotterClinic
{	
	public AlbertsonsClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "albertsons";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Albertsons";
	}		
}
