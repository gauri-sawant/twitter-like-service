package com.twitter.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.twitter.dto.RepliesDTO;
import com.twitter.dto.TweetDTO;
import com.twitter.dto.TweetRepliesDTO;
import com.twitter.dto.UserDTO;
import com.twitter.model.Replies;
import com.twitter.model.Tweet;
import com.twitter.model.User;
import com.twitter.persistence.RepliesRepository;
import com.twitter.persistence.TweetRepository;
import com.twitter.persistence.UserRepository;

@Component
public class TweetRESTServiceImpl implements TweetRESTService {

	private static final Logger LOGGER = LoggerFactory.getLogger(TweetRESTServiceImpl.class);
	
	@Resource
	TweetRepository tweetRepo;
	
	@Resource
	UserRepository userRepo;
	
	@Resource
	RepliesRepository replyRepo;
	
	@Inject
	UserRESTServiceImpl userRESTServiceImpl;
	
	@Inject
	TweetRESTServiceImpl tweetRESTServiceImpl;

	@Override
	public Response createTweet(TweetDTO tweetDTO, String userId) {
		try {
			LOGGER.info(">>createTweet");
			Tweet tweet = new Tweet();
			tweet.setAttachmentFileName(tweetDTO.getTweetAttachment());
			tweet.setText(tweetDTO.getTweetText());
			Optional<User> user = userRESTServiceImpl.findUser(Long.parseLong(userId));
			if(user.isPresent()) {
				tweet.setUser(user.get());
			} else {
				
				LOGGER.info("<<createTweet :: Failed, userId not found");
				return Response.status(HttpStatus.NOT_FOUND.value()).build();
			}
			
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
			reply.setAttachmentFileName(replyDTO.getReplyAttachment());
			reply.setText(replyDTO.getReplyText());
			
			Optional<User> user = userRESTServiceImpl.findUser(Long.parseLong(userId));
			if(user.isPresent()) {
				reply.setUser(user.get());
			} else {
				LOGGER.info("<<addReply :: Failed, userId not found");
				return Response.status(HttpStatus.NOT_FOUND.value()).build();
			}
			
			Optional<Tweet> tweet = tweetRESTServiceImpl.findTweetById(Long.parseLong(tweetId));
			if(tweet.isPresent()) {
				reply.setTweet(tweet.get());
			} else {
				LOGGER.info("<<addReply :: Failed, tweet not found");
				return Response.status(HttpStatus.NOT_FOUND.value()).build();
			}
			
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
								.tweetAttachment(entry.getAttachmentFileName())
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
									.tweetAttachment(tweet.getAttachmentFileName())
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
											.replyAttachment(reply.getAttachmentFileName())
											.build())
											.collect(Collectors.toCollection(ArrayList::new));
			
		}
		LOGGER.info("<<fetchFollowerReply");
		return new TweetRepliesDTO.Builder().tweetDTO(tweetDTO)
											.repliesDTOs(repliesDTOs).build();
	}

	public Optional<Tweet> findTweetById(Long tweetId) {
		return tweetRepo.findById(tweetId); 
	}
	
	private UserDTO mapUsertoDTO(User user) {
		return new UserDTO.Builder()
				.firstName(user.getFirstName())
				.lastName(user.getLastName())
				.userName(user.getUserName())
				.userId(user.getUserId()).build();
	}
	
}
