package abareaso.io.jvwatch.clinics;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.WebClient;

import abareaso.io.jvwatch.feign.PublixClient;
import abareaso.io.jvwatch.model.ClinicData;
import abareaso.io.jvwatch.model.PublixStoresResp;
import lombok.extern.slf4j.Slf4j;

/**
 * Retrieves vaccine appointment data from Publix clinics.  This implementation uses the global 
 * jvwatch.states property to control what states are queried for appointment data as well
 * as the global longitude and latitude configuratoin settings.  It also
 * uses an additional city filter (jvwatch.clinics.publix.cities) to further control the list of 
 * appointment data.  The city filter is inclusive only, so all cities that are desired MUST 
 * be included in the city list.
 * @author Greg Meyer
 *
 */
@Service
@Slf4j
public class PublixClinic implements Clinic
{	
	@Value("${jvwatch.clinics.publix.apptLink}")
	protected String apptLink;	
	
	@Value("${jvwatch.clinics.publix.cities:}")
	protected List<String> cities;	

	@Autowired
	protected PublixClient pubClient;
	
	protected ClinicSearchProperties props;
	
	protected final String verToken = "Jh0MPoR1RiHF_EPHvAXxHXoG_x5ggrDkKFjEgpXEIFzjV11gMno3i-I-dCmU93-zmmML4AeqcCmD_tgjS7atIa7MrCQ2qrEBzH28TwRK1do1";
	
	protected final String cookie = "__RequestVerificationToken=3V9ONhsH5hy5eZHOJdv80WBV45IzFoy3hTxD55PRcIbAhUUA9tn9hywQGX1EzHC2pR76x6X5hLfHZYd834IeKSYLDXmW-aGZckVA5S8HSe01; akavpau_VP_RX_731644b3be54=1617027948~id=ca2e5b6d97e9d11de7dc4978bb2d4f47; rxVisitor=16170273491810A1TS5M57LB9GS5JAOMJEP4LH3O548CJ; dtSa=-; rxvt=1617029150158|1617027349183; dtPC=2$27349175_673h-vAAHCNOGATFHTICFOPMDGUAHAAJGTKSKH-0e1; dtCookie=v_4_srv_2_sn_BB0F1902BDF77EC414FD67EFE0E60FDD_perc_100000_ol_0_mul_1_app-3A887fe36b88a6ccff_0; dtLatC=1; bm_sv=31491CA69B75C45C31D1DEFDE08D3EA6~/0oBAdhqgupFLU/GlhRWdGDVHQTw2OTwggK/egAj34VLSls5W+tp725Evo5jlpJnAlfP9mDJwc6RcwzMsyHjehuDyrHB8SG8UWGMY3zRhR49uoShwghHJ55ZOkGPPfZce4/1qvz0KbhJ53lleckfioFlpeCDiq1XnOrncVo6GzY=";
	
	public PublixClinic(ClinicSearchProperties props)
	{
		this.props = props;
	}
	
	@Override
	public List<ClinicData> getClinicAppointements() 
	{
		
		final List<ClinicData> retVal = new LinkedList<>();
		
		final List<PublixStoresResp.Store> foundStores = new LinkedList<>();
		
		/*
		 * Get all the clinics in the listed cities
		 */
		for (String state : props.getStates())
		{
			for (String city : cities)
			{
				try
				{
					final PublixStoresResp resp = pubClient.getStores(state, city).block();
					foundStores.addAll(resp.getStores());	
				}
				catch (Exception e)
				{
					log.warn("Error getting publix store in {city}, {state}: ", e.getMessage(), e);
				}
			}
		}
		
		final List<String> apptClinicNames = getApptClinics();

		/*
		 * Go through the list of clinics in the cities and find which one have
		 * appointments
		 */
		for (PublixStoresResp.Store foundStore :  foundStores)
		{
			final ClinicData clinic = new ClinicData();
			clinic.setId(props.getCachePrefix() + "Publix-" + foundStore.getKey());
			clinic.setName("Publix " + foundStore.getName());
			clinic.setState(foundStore.getState());
			clinic.setZipCode(foundStore.getZip());
			clinic.setLink(apptLink);
			
			if (apptClinicNames.contains(foundStore.getName()))
			{
				clinic.setAvailable(true);
			}
			else
			{
				clinic.setAvailable(false);
			}
			
			retVal.add(clinic);
		}
		
		return retVal;
	}
	
	/**
	 * There is not really a JSON API for getting appointments, so this uses
	 * an HTML scraping technique to get a list of the clinic names offering appointments.
	 * @return A list of the clinic names offering appointments.
	 */
	public List<String> getApptClinics()
	{
		final List<String> apptClinicNames = new LinkedList<>();
		
		try
		{
			/*
			 * Get the available appointments in the list location
			 */
			final WebClient webClient = WebClient.create("https://rx.publix.com/en/f/s/store/loc?lat=" + Double.toString(props.getLatitude()) 
			   + "&lon=" +  Double.toString(props.getLongitude()) + "&eid=402");
			 
			final Set<String> version = new HashSet<>();
			version.add(verToken);
			
			final String htmlResp = webClient.get()
			        .header(HttpHeaders.COOKIE, cookie)
			        .header("__RequestVerificationToken", verToken)
			        .retrieve()
			        .bodyToMono(String.class).block();

			if (StringUtils.hasText(htmlResp))
			{
				/*
				 * Find clinics that have available appointments
				 */
		   		final Document doc = Jsoup.parse(htmlResp);
				
				final Elements listItems =  doc.select("div.listItemWrapper");
				
				for (Element el : listItems)
				{
					final Element listItem = el.select("div.listItem").first();
					
					final Element itemSpan = listItem.select("span").get(1);
					
					final Element pharmacyName = itemSpan.selectFirst("strong");	
					
					final Element smallBlock = itemSpan.selectFirst("small");
					
					final Elements hasAppts = smallBlock.getElementsByAttributeValue("name", "pharmacyHasAppointments");
					
					if (hasAppts != null && hasAppts.size() > 0)
					{	
						if (hasAppts.first().val().equals("true"))
						{
							apptClinicNames.add(pharmacyName.text());
						}
					}
					
				}
			}
		}
		catch (Exception e)
		{
			log.error("Error getting list of publix apointment clinics: {}", e.getMessage(), e);
		}
		
		return apptClinicNames;
	}

}
