package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;

/**
 * Retrieves vaccine appointment data from Vons clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointment data.
 * @author Greg Meyer
 */
@Service
public class VonsClinic extends VaccineSpotterClinic
{
	public VonsClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "vons";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Vons";
	}	
}
