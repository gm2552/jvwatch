package abareaso.io.jvwatch;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import abareaso.io.jvwatch.clinics.CVSClinic;
import abareaso.io.jvwatch.clinics.Clinic;
import abareaso.io.jvwatch.clinics.HyVeeClinic;

@Configuration
public class ClinicConfiguration 
{
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
	
	@Value("${jvwatch.clinics.wallmart.enabled:true}")
	protected boolean enableWallmart;	
	
	@Autowired
	protected CVSClinic cvsClinic;
	
	@Autowired
	protected HyVeeClinic hyVeeClinic;
	
	@Bean()
	public List<Clinic> enabledClinics()
	{
		final List<Clinic> clinics = new ArrayList<>();
		
		//if (enableCVS)
		//	clinics.add(cvsClinic);	
		
		if (enableHyVee)
			clinics.add(hyVeeClinic);	
		
		return clinics;
	}
}
