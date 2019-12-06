package com.example.twitter.controller.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.twitter.authorization.Secured;
import com.example.twitter.dto.RepliesDTO;
import com.example.twitter.dto.TweetDTO;
import com.example.twitter.dto.TweetRepliesDTO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@Api(value = "/tweet")
@Path(value = "/tweet")
@Secured
public interface TweetRESTService {
	String API_VERSION = "1.0";

	@Path("/createTweet/{userId}")
	@Consumes( {MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION })
	@POST
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a tweet")
	@ApiResponses(value = {
			@ApiResponse(code = HTTPResponseCodes.CREATED, message = "User created"),
			@ApiResponse(code = HTTPResponseCodes.CONFLICT, message = "User Name already exists"),
			@ApiResponse(code = HTTPResponseCodes.INTERNAL_SERVER_ERROR, message = "The server experienced a runtime exception while processing the request. Try again later or contact customer support.")
	})
	Response createTweet(TweetDTO tweetDTO, @PathParam("userId") String userId);
	
	
	@Path("/addReply/{tweetId}/{userId}")
	@Consumes( {MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION })
	@POST
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Add a reply")
	@ApiResponses(value = {
			@ApiResponse(code = HTTPResponseCodes.CREATED, message = "User created"),
			@ApiResponse(code = HTTPResponseCodes.CONFLICT, message = "User Name already exists"),
			@ApiResponse(code = HTTPResponseCodes.INTERNAL_SERVER_ERROR, message = "The server experienced a runtime exception while processing the request. Try again later or contact customer support.")
	})
	Response addReply(RepliesDTO replyDTO, @PathParam("tweetId") String tweetId, @PathParam("userId") String userId);
	
	@Path("getTweets/{userId}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION})
	@GET
	@ApiOperation(value = "Get user tweets", response = TweetDTO[].class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK."),
			@ApiResponse(code = 404, message = "Requested user not found."),
			@ApiResponse(code = 500, message = "The server experienced a runtime exception while processing the request. Try again later.") })
	Response getTweetsForUser(@PathParam("userId") String userId);
	
	

	@Path("getFollowerTweetRepliesForUser/{userId}")
	@Produces({MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION})
	@GET
	@ApiOperation(value = "Get user tweets and follower replies to the tweet", response = TweetRepliesDTO[].class)
	@ApiResponses(value = { @ApiResponse(code = 200, message = "OK."),
			@ApiResponse(code = 404, message = "Requested user not found."),
			@ApiResponse(code = 500, message = "The server experienced a runtime exception while processing the request. Try again later.") })
	Response getFollowerTweetRepliesForUser(@PathParam("userId") String userId);
}
