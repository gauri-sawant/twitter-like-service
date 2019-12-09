package com.twitter.controller.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ws.rs.core.Response;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import com.twitter.dto.RepliesDTO;
import com.twitter.dto.TweetDTO;
import com.twitter.dto.TweetRepliesDTO;
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
		Tweet tweet = createTweet(1L, "tweetText", "tweetfileName",1L);
		when(tweetRepo.save(any(Tweet.class))).thenReturn(tweet);
		when(userRESTServiceImpl.findUser(anyLong())).thenReturn(Optional.of(user));
		TweetDTO tweetDTO = createTweetDTO("tweetText", "tweetfileName");
		Response response = tweetRESTServiceImpl.createTweet(tweetDTO, "1");
		verify(tweetRepo).save(any(Tweet.class));
		assertThat(response.getStatus(), is(201));
	}
	
	
	@Test
	public void shouldNotCreateTweetWhenUserNotFound() {
		
		when(userRESTServiceImpl.findUser(anyLong())).thenReturn(null);
		TweetDTO tweetDTO = createTweetDTO("tweetText", "tweetfileName");
		Response response = tweetRESTServiceImpl.createTweet(tweetDTO, "1");
		verify(tweetRepo, never()).save(any(Tweet.class));
		assertThat(response.getStatus(), is(500));
	}
	

	@Test
	public void shouldAddNewReply() {
		
		User user = createTweetUser(1L, "userName", "firstName", "lastName");
		Tweet tweet = createTweet(1L, "tweetText", "tweetfileName", 1L);
		Replies reply = createReply(1L, "replyText", "replyfileName", 2L);
		when(replyRepo.save(any(Replies.class))).thenReturn(reply);
		when(userRESTServiceImpl.findUser(anyLong())).thenReturn(Optional.of(user));
		when(tweetRepo.findById(anyLong())).thenReturn(Optional.of(tweet));
		RepliesDTO replyDTO = createReplyDTO("replyText", "replyfileName");
		Response response = tweetRESTServiceImpl.addReply(replyDTO, "1", "1");
		verify(replyRepo).save(any(Replies.class));
		assertThat(response.getStatus(), is(201));
	}
	
	
	@Test
	public void shouldGetTweetsForUser() {
		
		Tweet tweet1 = createTweet(1L, "tweetText1", "tweetfileName1", 1L);
		Tweet tweet2 = createTweet(2L, "tweetText2", "tweetfileName2", 1L);
		
		List<Tweet> tweetList = new ArrayList<>(); 
		tweetList.add(tweet1);
		tweetList.add(tweet2);
		when(tweetRepo.getTweets(anyLong())).thenReturn(tweetList);
		
		Response response = tweetRESTServiceImpl.getTweetsForUser("1");
		assertThat(((List<TweetDTO>) response.getEntity()).size(), is(2));
		assertThat(response.getStatus(), is(200));
	}
	
	@Test
	public void shouldNotGetTweetsForIncorrectUserId() {
		Response response = tweetRESTServiceImpl.getTweetsForUser("PP");
		assertThat(response.getStatus(), is(400));
	}
	
	@Test
	public void shouldGetTweetsAndFollowerRepliesForUser() {

		Tweet tweet1 = createTweet(1L, "tweetText1", "tweetfileName1", 1L);
		Replies replies = createReply(1L, "replyText", "replyfileName", 2L);

		// Add Followers
		User followerUser = new User();
		followerUser.setUserId(2L);
		followerUser.setUserName("userName2");

		Set<User> userSet = new HashSet<>();
		userSet.add(followerUser);

		tweet1.getUser().setFollowerUser(userSet);
		
		//Add replies
		List<Replies> replyList = new ArrayList<>();
		replyList.add(replies);
		tweet1.setReplies(replyList);

		List<Tweet> tweetList = new ArrayList<>();
		tweetList.add(tweet1);

		when(tweetRepo.getTweets(anyLong())).thenReturn(tweetList);

		Response response = tweetRESTServiceImpl.getFollowerTweetRepliesForUser("1");
		assertThat(((List<TweetRepliesDTO>) response.getEntity()).size(), is(1));
		assertThat(response.getStatus(), is(200));
	}
	
	@Test
	public void shouldGetTweetsAndNoRepliesForUserIfFollowersNotPresent() {

		Tweet tweet1 = createTweet(1L, "tweetText1", "tweetfileName1", 1L);
		Replies replies = createReply(1L, "replyText", "replyfileName", 2L);
		//Add replies
		List<Replies> replyList = new ArrayList<>();
		replyList.add(replies);
		tweet1.setReplies(replyList);

		List<Tweet> tweetList = new ArrayList<>();
		tweetList.add(tweet1);

		when(tweetRepo.getTweets(anyLong())).thenReturn(tweetList);

		Response response = tweetRESTServiceImpl.getFollowerTweetRepliesForUser("1");
		List<TweetRepliesDTO> tweetRepliesDTO = (List<TweetRepliesDTO>) response.getEntity();
		assertThat((tweetRepliesDTO).size(), is(1));
		
		List<RepliesDTO> repliesDTO = tweetRepliesDTO.get(0).getRepliesDTOs();
		assertThat((repliesDTO).size(), is(0));
		assertThat(response.getStatus(), is(200));
	}
	
	
	@Test
	public void shouldNotGetTweetsAndRepliesForUserIfTweetsNotPresent() {

		when(tweetRepo.getTweets(anyLong())).thenReturn(null);
		Response response = tweetRESTServiceImpl.getFollowerTweetRepliesForUser("1");
		List<TweetRepliesDTO> tweetRepliesDTO = (List<TweetRepliesDTO>) response.getEntity();
		assertThat((tweetRepliesDTO), is(nullValue()));
		assertThat(response.getStatus(), is(500));
	}
	
	
	@Test
	public void shouldNotGetTweetsAndRepliesForIncorrectUserId() {

		Response response = tweetRESTServiceImpl.getFollowerTweetRepliesForUser("PP");
		List<TweetRepliesDTO> tweetRepliesDTO = (List<TweetRepliesDTO>) response.getEntity();
		assertThat((tweetRepliesDTO), is(nullValue()));
		assertThat(response.getStatus(), is(400));
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

	private Tweet createTweet(long tweetId, String tweetText, String tweetfileName, Long userId) {
		Tweet tweet = new Tweet();
		tweet.setTweetId(tweetId);
		tweet.setText(tweetText);
		tweet.setAttachmentFileName(tweetfileName);
		
		User user = new User(userId);
		user.setUserName("userName");
		tweet.setUser(user);
		return tweet;
	}
	
	private Replies createReply(long tweetId, String replyText, String replyFileName, Long userId) {
		Replies reply = new Replies();
		reply.setReplyId(tweetId);
		reply.setText(replyText);
		reply.setAttachmentFileName(replyFileName);
		
		User user = new User(userId);
		user.setUserName("userName");
		reply.setUser(user);
		return reply;
	}

}
