package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;

/**
 * Retrieves vaccine appointment data from Walgreens clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointment data.
 * @author Greg Meyer
 */
@Service
public class WalgreensClinic extends VaccineSpotterClinic
{	
	
	public WalgreensClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "walgreens";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Walgreens";
	}	
}
