package abareaso.io.jvwatch.notifications;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.springframework.util.StringUtils;

import abareaso.io.jvwatch.model.ClinicData;

/**
 * Abstract base class for publisher.  This class creates generic logic for create available and
 * unavailable messages.
 * @author Greg Meyer
 *
 */
public abstract class AbstractPublisher implements Publisher
{	
	protected String buildAvailableMessage(ClinicData data)
	{
		final SimpleDateFormat formatter = new SimpleDateFormat("MMM dd");
		
		final StringBuilder dayString = new StringBuilder();
		
		if (data.getEariestApptDay() != null && data.getLatestApptDay() == null)
			dayString.append(" on ").append(formatter.format(data.getEariestApptDay()));
		else if (data.getEariestApptDay() != null && data.getLatestApptDay() != null)
		{
			dayString.append(" from ").append(formatter.format(data.getEariestApptDay()));
			dayString.append(" to ").append(formatter.format(data.getLatestApptDay()));
		}
		
		String link = TinyURLCreator.toTinyURL(data.getLink());
		if (!StringUtils.hasText(link))
			link = data.getLink();
		
		final StringBuilder builder = new StringBuilder(data.getState());
		
		final String zipCode = StringUtils.hasText(data.getZipCode()) ?  ", zip code " + data.getZipCode() : "";
		
		builder.append(": Vaccine appointments available at ").append(data.getName());
		builder.append(dayString.toString());
		builder.append(".  Sign up here").append(zipCode).append(":\n");
		builder.append(link);
		
		if (data.getLastFetched() == null)
			data.setLastFetched(new Date());

		final DateFormat dateFormat = new SimpleDateFormat("hh:mm aa zzz");
		applyFormatTZ(dateFormat);
		
		builder.append(" (as of ").append(dateFormat.format(data.getLastFetched())).append(")");
		
		return builder.toString();
	}
	
	protected String buildUnavailableMessage(ClinicData data)
	{
		
		final StringBuilder builder = new StringBuilder("Vaccine appointments no longer available at ").append(data.getName());
		
		if (data.getLastFetched() == null)
			data.setLastFetched(new Date());

		final DateFormat dateFormat = new SimpleDateFormat("hh:mm aa zzz");
		applyFormatTZ(dateFormat);
		
		builder.append(" (as of ").append(dateFormat.format(data.getLastFetched())).append(")");
		
		return builder.toString();
	}
	
	protected void applyFormatTZ(DateFormat dateFormat)
	{
		final String notifTimeZone = System.getProperty("jvwatch.notifTimeZone");
		
		if (StringUtils.hasText(notifTimeZone))
			dateFormat.setTimeZone(TimeZone.getTimeZone(notifTimeZone));
	}
}
