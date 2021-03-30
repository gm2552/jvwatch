package abareaso.io.jvwatch.notifications;

import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.any;

import java.util.Random;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import abareaso.io.jvwatch.model.ClinicData;
import twitter4j.Status;
import twitter4j.StatusUpdate;
import twitter4j.Twitter;

@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = DataTestApplication.class, webEnvironment = WebEnvironment.NONE)
public class TwitterPublisherTest 
{
	@Autowired
	protected StringRedisTemplate redisTemplate;
	
	@Mock
	protected Twitter twitter;
	
	@Mock
	protected Status tweetStatus;
	
	@BeforeEach
	public void setUp() throws Exception
	{
		when(tweetStatus.getId()).thenReturn(new Random().nextLong());
		when(twitter.updateStatus(anyString())).thenReturn(tweetStatus);
	}
	
	@Test
	public void testPublishAvailableAndUnavilableMessage_assertPublished() throws Exception
	{
		final TwitterPublisher pub = new TwitterPublisher(twitter, redisTemplate);
		
		final ClinicData data = new ClinicData();
		data.setAvailable(true);
		data.setId(UUID.randomUUID().toString());
		data.setName("Test Clinic");
		data.setState("MO");
		data.setZipCode("64117");
		
		pub.publishAvailableNotification(data);
		
		data.setAvailable(false);
		pub.publishUnavailableNotification(data);
		
		verify(twitter, times(1)).updateStatus(anyString());
		verify(twitter, times(1)).updateStatus((StatusUpdate)any());
	}
}
