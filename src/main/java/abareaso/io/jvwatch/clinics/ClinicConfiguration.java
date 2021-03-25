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
	
	@Value("${jvwatch.clinics.wllmart.enabled:true}")
	protected boolean enableWalmart;	
	
	@Autowired
	protected CVSClinic cvsClinic;
	
	@Autowired
	protected HyVeeClinic hyVeeClinic;
	
	@Autowired
	protected WalmartClinic walmartClinic;	
	
	@Autowired
	protected WalgreensClinic walgreensClinic;
	
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
