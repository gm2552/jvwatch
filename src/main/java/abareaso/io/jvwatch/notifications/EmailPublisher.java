package abareaso.io.jvwatch.notifications;

import java.util.List;

import javax.mail.Message.RecipientType;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.springframework.mail.javamail.JavaMailSender;

import abareaso.io.jvwatch.model.ClinicData;
import lombok.extern.slf4j.Slf4j;

/**
 * A publisher that sends clinic notification messages to a configured set of email addresses
 * from a configured email sender account.  Email server information is configured using the standard
 * spring.mail.* spring configuration parameters, and the sender, to, and subject information is supplied
 * with jvwatch.notifications.email settings enumerated in the EmailMessageConfigProperties class.
 * @author Greg Meyer
 */
@Slf4j
public class EmailPublisher extends AbstractPublisher
{
	protected final JavaMailSender emailSender;
	
	protected final String from;
	
	protected final List<String> to;
	
	protected final String subject;
	
	public EmailPublisher(JavaMailSender emailSender, EmailMessageConfigProperties props)
	{
		this.emailSender = emailSender;
		
		this.from = props.getFrom();
		
		this.to = props.getTo();
		
		this.subject = props.getSubject();
	}	
	
	@Override
	public void publishAvailableNotification(ClinicData data) 
	{
		final MimeMessage message = new MimeMessage((Session)null); 
		
		try
		{
			message.setFrom(from);
			message.setSubject(subject);
			message.setText(buildAvailableMessage(data));
			
			for (String toRecip : to)
				message.addRecipient(RecipientType.TO, new InternetAddress(toRecip));
			
			emailSender.send(message);
		}
		catch (Exception e)
		{
			log.error("Failed to publish email for unavailable data.", e);
		}
	}

	@Override
	public void publishUnavailableNotification(ClinicData data) 
	{
		final MimeMessage message = new MimeMessage((Session)null); 
		
		try
		{
			message.setFrom(from);
			message.setSubject(subject);
			message.setText(buildUnavailableMessage(data));
			
			for (String toRecip : to)
				message.addRecipient(RecipientType.TO, new InternetAddress(toRecip));
			
			emailSender.send(message);
		}
		catch (Exception e)
		{
			log.error("Failed to publish email for unavailable data.", e);
		}	
	}

}
