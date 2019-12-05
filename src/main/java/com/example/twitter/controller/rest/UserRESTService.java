package com.example.twitter.controller.rest;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.example.twitter.authorization.Secured;
import com.example.twitter.dto.UserDTO;

@Api(value = "/user")
@Path(value = "/user")
@Secured
public interface UserRESTService {
	String API_VERSION = "1.0";
	
	@Path("/createUser")
	@Consumes( {MediaType.APPLICATION_JSON })
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION })
	@POST
	@ResponseStatus(HttpStatus.CREATED)
	@ApiOperation(value = "Create a user")
	@ApiResponses(value = {
			@ApiResponse(code = HTTPResponseCodes.CREATED, message = "User created"),
			@ApiResponse(code = HTTPResponseCodes.CONFLICT, message = "User Name already exists"),
			@ApiResponse(code = HTTPResponseCodes.INTERNAL_SERVER_ERROR, message = "The server experienced a runtime exception while processing the request. Try again later or contact customer support.")
	})
	Response createUser(UserDTO userDTO);
	
    @Path("/getUsers")
    @Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version="+API_VERSION})
    @GET
    @ApiOperation(value = "Get list of users", response = UserDTO[].class)
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = "OK."),
            @ApiResponse(code = 500, message = "The server experienced a runtime exception while processing the request. Try again later.")
    		})
    Response getUsers();
	
    
	@DELETE
	@Path("/deleteUser/{userId}")
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION })
	@ApiOperation(value = "Delete user")
	@ApiResponses(value = {
			@ApiResponse(code = HTTPResponseCodes.NO_CONTENT, message = "The user was deleted successfully."),
			@ApiResponse(code = HTTPResponseCodes.INTERNAL_SERVER_ERROR, message = "The server experienced a runtime exception while processing the request. Try again later or contact customer support.")
	})
	Response deleteUser(@PathParam("userId") String userId);
	
	@Path("follow/follower/{followerUserId}/follow/{followedUserId}")
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION })
	@POST
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Follow a user")
	@ApiResponses(value = {
			@ApiResponse(code = HTTPResponseCodes.OK, message = "User followed"),
			@ApiResponse(code = HTTPResponseCodes.INTERNAL_SERVER_ERROR, message = "The server experienced a runtime exception while processing the request. Try again later or contact customer support.")
	})
	Response followUser(@PathParam("followerUserId") String fromUser, @PathParam("followedUserId") String toUserId);
	
	@Path("unfollow/follower/{followerUserId}/unfollow/{followedUserId}")
	@Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version=" + API_VERSION })
	@POST
	@ResponseStatus(HttpStatus.OK)
	@ApiOperation(value = "Unfollow a user")
	@ApiResponses(value = {
			@ApiResponse(code = HTTPResponseCodes.OK, message = "User unfollowed"),
			@ApiResponse(code = HTTPResponseCodes.INTERNAL_SERVER_ERROR, message = "The server experienced a runtime exception while processing the request. Try again later or contact customer support.")
	})
	Response unfollowUser(@PathParam("followerUserId") String fromUser, @PathParam("followedUserId") String toUserId);
	
	
    @Path("getfollowers/{userId}")
    @Produces({ MediaType.APPLICATION_JSON + ";charset=utf-8;version="+API_VERSION})
    @GET
    @ApiOperation(value = "get followers for a user", response = UserDTO[].class)
    @ApiResponses(value = { 
    		@ApiResponse(code = 200, message = "OK."),
    		@ApiResponse(code = 404, message = "Requested user not found."),
            @ApiResponse(code = 500, message = "The server experienced a runtime exception while processing the request. Try again later.")
    		})
    Response getfollowers(@PathParam("userId") String userId);
}
