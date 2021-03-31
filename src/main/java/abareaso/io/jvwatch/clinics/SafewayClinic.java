package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;

/**
 * Retrieves vaccine appointment data from Safeway clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointment data.
 * @author Greg Meyer
 */
@Service
public class SafewayClinic extends VaccineSpotterClinic
{
	public SafewayClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "safeway";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Safeway";
	}
}
