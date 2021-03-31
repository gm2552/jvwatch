package abareaso.io.jvwatch.clinics;

import org.springframework.stereotype.Service;


/**
 * Retrieves vaccine appointment data from Walgreens clinics.  This is a subclass of the VaccineSpotterClinic and
 * uses its filtering rules for appointement data.
 * @author Greg Meyer
 */
@Service
public class WalmartClinic extends VaccineSpotterClinic
{	
	
	public WalmartClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "walmart";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "Walmart";
	}		

}
