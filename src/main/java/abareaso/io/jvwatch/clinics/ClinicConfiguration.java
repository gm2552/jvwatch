package abareaso.io.jvwatch.clinics;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for creating instances of the clinics that will be polled for vaccine
 * appointment data. 
 * @author Greg Meyer
 *
 */
@Configuration
public class ClinicConfiguration 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(ClinicConfiguration.class);		
	
	@Value("${jvwatch.clinics.balls.enabled:true}")
	protected boolean enableBalls;
	
	@Value("${jvwatch.clinics.cosentinos.enabled:true}")
	protected boolean enableCosentinos;
	
	@Value("${jvwatch.clinics.cvs.enabled:true}")
	protected boolean enableCVS;	
	
	@Value("${jvwatch.clinics.hyvee.enabled:true}")
	protected boolean enableHyVee;		
	
	@Value("${jvwatch.clinics.wallgreens.enabled:true}")
	protected boolean enableWallgreens;		
	
	@Value("${jvwatch.clinics.walmart.enabled:true}")
	protected boolean enableWalmart;	
	
	@Value("${jvwatch.clinics.publix.enabled:true}")
	protected boolean enablePublix;		
	
	@Value("${jvwatch.clinics.vons.enabled:true}")
	protected boolean enableVons;		
	
	@Value("${jvwatch.clinics.albertsons.enabled:true}")
	protected boolean enableAlbertsons;	
	
	@Value("${jvwatch.clinics.safeway.enabled:true}")
	protected boolean enableSafeway;		

	@Value("${jvwatch.clinics.samsclub.enabled:true}")
	protected boolean enableSams;	

	@Value("${jvwatch.clinics.winndixie.enabled:true}")
	protected boolean enableWinnDixie;	
	
	@Autowired
	protected CVSClinic cvsClinic;
	
	@Autowired
	protected HyVeeClinic hyVeeClinic;
	
	@Autowired
	protected WalmartClinic walmartClinic;	
	
	@Autowired
	protected WalgreensClinic walgreensClinic;
	
	@Autowired
	protected PublixClinic publixClinic;
	
	@Autowired
	protected VonsClinic vonsClinic;	
	
	@Autowired
	protected AlbertsonsClinic albertsonsClinic;	
	
	@Autowired
	protected SafewayClinic safewayClinic;
	
	@Autowired
	protected SamsClubClinic samsClinic;	

	@Autowired
	protected WinnDixieClinic winnDixieClinic;
	
	/**
	 * Creates a bean that contains a list of "enabled" clinics.
	 * @return A list of configured clinic instances that will be polled for vaccine appointment data. 
	 */
	@Bean
	public List<Clinic> enabledClinics()
	{
		final List<Clinic> clinics = new ArrayList<>();
		
		if (enableCVS)
			clinics.add(cvsClinic);	
		
		if (enableHyVee)
			clinics.add(hyVeeClinic);	
		
		if (enableWalmart)
			clinics.add(walmartClinic);
		
		if (enableWallgreens)
			clinics.add(walgreensClinic);
		
		if (enablePublix)
			clinics.add(publixClinic);
		
		if (enableVons)
			clinics.add(vonsClinic);	

		if (enableAlbertsons)
			clinics.add(albertsonsClinic);

		if (enableSafeway)
			clinics.add(safewayClinic);

		if (enableSams)
			clinics.add(samsClinic);
		
		if (enableWinnDixie)
			clinics.add(winnDixieClinic);
		
		final StringBuilder bld = new StringBuilder("Enabled clinic searchs:");
		if (clinics.isEmpty())
			bld.append("\n\tNONE");
		else
		{
			for (Clinic clin : clinics)
			{
				bld.append("\n\t").append(clin.getClass().getSimpleName());
			}
		}
		
		LOGGER.info(bld.toString());
		
		return clinics;
	}
}
