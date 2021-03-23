package abareaso.io.jvwatch.model;

import java.util.LinkedList;
import java.util.List;

import lombok.Data;

@Data
public class ClinicAvailability 
{
	private List<ClinicData> available = new LinkedList<>();
	
	private List<ClinicData> unavailable = new LinkedList<>();	
}
