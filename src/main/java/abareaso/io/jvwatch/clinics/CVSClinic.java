package abareaso.io.jvwatch.clinics;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import abareaso.io.jvwatch.model.VaccineSpotterResp.VaccineSpotterFeature;

/**
 * Retrieves vaccine appointment data from CVS clinics.  This implementation uses the global 
 * jvwatch.states property to control what states are queried for appointment data.  It also
 * uses an additional city filter (jvwatch.clinics.cvs.cities) to further control the list of 
 * appointment data.  The city filter is inclusive only, so all cities that are desired MUST 
 * be included in the city list (they also should be in all upper case).
 * @author Greg Meyer
 *
 */
@Service
public class CVSClinic extends VaccineSpotterClinic
{	
	protected ClinicSearchProperties props;
	
	@Value("${jvwatch.clinics.cvs.cities}")
	protected List<String> cities;	
	
	public CVSClinic(ClinicSearchProperties props)
	{
		super(props);
	}
	
	@Override
	protected String getBrandName()
	{
		return "cvs";
	}
	
	@Override
	protected String getDisplayName()
	{
		return "CVS";
	}
	
	protected boolean shouldIncludeLocation(VaccineSpotterFeature feature) 
	{
		boolean retVal = false;
		
		if (feature.getProperties().getProvider_brand().equals(getBrandName()))
		{
			if (cities.contains(feature.getProperties().getCity().toUpperCase()))
					retVal = true;
		}
		
		return retVal;
	}	
}
