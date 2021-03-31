package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;

/**
 * Retrieves vaccine appointment data from Winn-Dixie clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointement data.
 * @author Greg Meyer
 */
@Service
public class WinnDixieClinic extends VaccineSpotterClinic
{
	public WinnDixieClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "winn_dixie";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Winn-Dixie";
	}
}
