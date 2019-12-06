package com.example.twitter.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.example.twitter.dto.RepliesDTO;
import com.example.twitter.dto.TweetDTO;
import com.example.twitter.dto.TweetRepliesDTO;
import com.example.twitter.dto.UserDTO;
import com.example.twitter.model.Replies;
import com.example.twitter.model.Tweet;
import com.example.twitter.model.User;
import com.example.twitter.persistence.RepliesRepository;
import com.example.twitter.persistence.TweetRepository;
import com.example.twitter.persistence.UserRepository;

@Component
public class TweetRESTServiceImpl implements TweetRESTService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TweetRESTServiceImpl.class);
	
	@Resource
	TweetRepository tweetRepo;
	
	@Resource
	UserRepository userRepo;
	
	@Resource
	RepliesRepository replyRepo;

	@Override
	public Response createTweet(TweetDTO tweetDTO, String userId) {
		try {
			LOGGER.info(">>createTweet");
			Tweet tweet = new Tweet();
			User user = new User(Long.parseLong(userId));
			tweet.setAttachment(tweetDTO.getTweetAttachment());
			tweet.setText(tweetDTO.getTweetText());
			tweet.setUser(user);
			tweetRepo.save(tweet);
			LOGGER.info("<<createTweet :: Tweet created for user : {}", userId);
			return Response.status(HttpStatus.CREATED.value()).build();
		} catch(NumberFormatException nex) {
			LOGGER.info("<<createTweet :: Failed, bad userId parameter");
			return Response.status(HttpStatus.BAD_REQUEST.value()).build();
		} catch (Exception ex) {
			LOGGER.error("<<createTweet :: Failed {}", ex);
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}
	

	@Override
	public Response addReply(RepliesDTO replyDTO, String tweetId, String userId) {
		try {
			LOGGER.info(">>addReply tweetId {}, userId {}",tweetId, userId);
			Replies reply = new Replies();
			User user = new User(Long.parseLong(userId));
			Tweet tweet = new Tweet(Long.parseLong(tweetId));
			reply.setAttachment(replyDTO.getReplyAttachment());
			reply.setText(replyDTO.getReplyText());
			reply.setTweet(tweet);
			reply.setUser(user);
			replyRepo.save(reply);
			LOGGER.info("<<addReply :: Reply added for user : {}", userId);
			return Response.status(HttpStatus.CREATED.value()).build();
		} catch(NumberFormatException nex) {
			LOGGER.info("<<addReply :: Failed, bad userId/tweetId parameter");
			return Response.status(HttpStatus.BAD_REQUEST.value()).build();
		} catch (Exception ex) {
			LOGGER.error("<<addReply :: Failed {}", ex);
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}

	
	@Override
	public Response getTweetsForUser(String userId) {

		try {
			LOGGER.info(">>getTweetsForUser");
			List<Tweet> tweets = tweetRepo.getTweets(Long.parseLong(userId));
			List<TweetDTO> result = new ArrayList<>();
			if (!tweets.isEmpty()) {
				result = tweets.stream()
						.map(entry -> new TweetDTO.Builder()
								.tweetId(entry.getTweetId())
								.tweetText(entry.getText())
								.tweetAttachment(entry.getAttachment())
								.user(mapUsertoDTO(entry.getUser())).build())
						.collect(Collectors.toCollection(ArrayList::new));
			}
			LOGGER.info("<<getTweetsForUser :: list size : {}", result.size());
			if (!result.isEmpty()) {
				return Response.ok(result).build();
			} else {
				return Response.noContent().build();
			}
		} catch(NumberFormatException nex) {
			LOGGER.info("<<getTweetsForUser :: Failed, bad userId parameter");
			return Response.status(HttpStatus.BAD_REQUEST.value()).build();
		} catch (Exception ex) {
			LOGGER.error("<<getTweetsForUser :: Failed {}", ex);
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}

	@Override
	public Response getFollowerTweetRepliesForUser(String userId) {
		try {
			LOGGER.info(">>getFollowerTweetRepliesForUser");
			List<Tweet> tweets = tweetRepo.getTweets(Long.parseLong(userId));
			List<TweetRepliesDTO> tweetRepliesDTOs = tweets.stream().map(entry -> fetchFollowerReply(entry))
															.collect(Collectors.toCollection(ArrayList::new));
			
			LOGGER.info("<<getFollowerTweetRepliesForUser :: list size : {}", tweetRepliesDTOs.size());
			if (!tweetRepliesDTOs.isEmpty()) {
				return Response.ok(tweetRepliesDTOs).build();
			} else {
				return Response.noContent().build();
			}
		} catch(NumberFormatException nex) {
			LOGGER.info("<<getFollowerTweetRepliesForUser :: Failed, bad userId parameter");
			return Response.status(HttpStatus.BAD_REQUEST.value()).build();
		} catch (Exception ex) {
			LOGGER.error("<<getFollowerTweetRepliesForUser :: Failed {}", ex);
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}
	
	private TweetRepliesDTO fetchFollowerReply(Tweet tweet) {
		
		LOGGER.info(">>fetchFollowerReply");
		TweetDTO tweetDTO = new TweetDTO.Builder()
									.tweetId(tweet.getTweetId())
									.tweetText(tweet.getText())
									.tweetAttachment(tweet.getAttachment())
									.user(mapUsertoDTO(tweet.getUser())).build();
		
		Set<User> followerUsers = tweet.getUser().getFollowerUser();
		List<RepliesDTO> repliesDTOs = new ArrayList<>();
		if(followerUsers != null && !followerUsers.isEmpty()) {
			List<Long> followerList = followerUsers.stream().map(user -> user.getUserId())
			.collect(Collectors.toCollection(ArrayList::new));
			
			List<Replies> replies = tweet.getReplies();
			repliesDTOs = replies.stream()
					.filter(reply -> followerList.contains(reply.getUser().getUserId()))
					.map(reply -> new RepliesDTO.Builder()
											.replyId(reply.getReplyId())
											.replyText(reply.getText())
											.user(mapUsertoDTO(reply.getUser()))
											.replyAttachment(reply.getAttachment())
											.build())
											.collect(Collectors.toCollection(ArrayList::new));
			
		}
		LOGGER.info("<<fetchFollowerReply");
		return new TweetRepliesDTO.Builder().tweetDTO(tweetDTO)
											.repliesDTOs(repliesDTOs).build();
	}

	private UserDTO mapUsertoDTO(User user) {
		return new UserDTO.Builder()
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.userName(user.getUserName())
				.userId(user.getUserId()).build();
	}
}
