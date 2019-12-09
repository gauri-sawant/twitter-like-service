package com.twitter.controller.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.Optional;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.twitter.dto.RepliesDTO;
import com.twitter.dto.TweetDTO;
import com.twitter.model.Replies;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.persistence.RepliesRepository;
import com.twitter.persistence.TweetRepository;

/**
 * @author gauri sawant
 *
 */
@RunWith(MockitoJUnitRunner.class)
public class TweetRESTServiceImplTest {

	@InjectMocks
	private TweetRESTServiceImpl tweetRESTServiceImpl;

	@Mock
	private TweetRepository tweetRepo;
	

	@Mock
	private RepliesRepository replyRepo;
	
	@Mock
	UserRESTServiceImpl userRESTServiceImpl;

	@Test
	public void shouldCreateNewTweet() {
		
		User user = createTweetUser(1L, "userName", "firstName", "lastName");
		Tweet tweet = createTweet(1L, "tweetText", "tweetfileName");
		when(tweetRepo.save(any(Tweet.class))).thenReturn(tweet);
		when(userRESTServiceImpl.findUser(anyLong())).thenReturn(Optional.of(user));
		TweetDTO tweetDTO = createTweetDTO("tweetText", "tweetfileName");
		Response response = tweetRESTServiceImpl.createTweet(tweetDTO, "1");
		verify(tweetRepo).save(any(Tweet.class));
		assertThat(response.getStatus(), is(201));
	}
	

	@Test
	public void shouldAddNewReply() {
		
		User user = createTweetUser(1L, "userName", "firstName", "lastName");
		Tweet tweet = createTweet(1L, "tweetText", "tweetfileName");
		Replies reply = createReply(1L, "replyText", "replyfileName");
		when(replyRepo.save(any(Replies.class))).thenReturn(reply);
		when(userRESTServiceImpl.findUser(anyLong())).thenReturn(Optional.of(user));
		when(tweetRepo.findById(anyLong())).thenReturn(Optional.of(tweet));
		RepliesDTO replyDTO = createReplyDTO("replyText", "replyfileName");
		Response response = tweetRESTServiceImpl.addReply(replyDTO, "1", "1");
		verify(replyRepo).save(any(Replies.class));
		assertThat(response.getStatus(), is(201));
	}

	private RepliesDTO createReplyDTO(String replyText, String replyfileName) {
		return new RepliesDTO.Builder().replyText(replyText).replyAttachment(replyfileName).build();
	}


	private User createTweetUser(long id, String userName, String firstName, String lastName) {
		User user = new User();
		user.setUserId(id);
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return user;
	}

	private TweetDTO createTweetDTO(String tweetText, String tweetfileName) {
		return new TweetDTO.Builder().tweetText(tweetText).tweetAttachment(tweetfileName).build();
	}

	private Tweet createTweet(long tweetId, String tweetText, String tweetfileName) {
		Tweet tweet = new Tweet();
		tweet.setTweetId(tweetId);
		tweet.setText(tweetText);
		tweet.setAttachmentFileName(tweetfileName);
		return tweet;
	}
	
	private Replies createReply(long tweetId, String tweetText, String tweetfileName) {
		Replies reply = new Replies();
		reply.setReplyId(tweetId);
		reply.setText(tweetText);
		reply.setAttachmentFileName(tweetfileName);
		return reply;
	}

}
