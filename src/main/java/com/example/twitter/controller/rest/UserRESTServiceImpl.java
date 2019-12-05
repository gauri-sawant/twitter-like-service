package com.example.twitter.controller.rest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.Resource;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.example.twitter.dto.UserDTO;
import com.example.twitter.model.User;
import com.example.twitter.persistence.UserRepository;

@Component
public class UserRESTServiceImpl implements UserRESTService {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserRESTServiceImpl.class);
	
	@Resource
	UserRepository userRepo;
	
	@Override
	public Response createUser(UserDTO userDTO) {
		try {
			LOGGER.info(">>createUser");
			if (userRepo.getUserNameCount(userDTO.getUserName()) == 0) {
				User user = new User();
				user.setFirstName(userDTO.getFirstName());
				user.setLastName(userDTO.getLastName());
				user.setUserName(userDTO.getUserName());
				userRepo.save(user);
				LOGGER.info("<<createUser, {} {}", userDTO.getFirstName(), userDTO.getLastName());
				return Response.status(HttpStatus.CREATED.value()).build();
			} else {
				LOGGER.info("<<createUser :: userName already exists {}", userDTO.getUserName());
				return Response.status(HttpStatus.CONFLICT.value()).build();
			}
		} catch (Exception ex) {
			LOGGER.error("<<createUser :: Failed {}", ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}
	
	@Override
	@Transactional
	public Response deleteUser(String userId) {
		try {
			LOGGER.info(">>deleteUser");
			userRepo.clearFollowers(Long.parseLong(userId));
			userRepo.deleteById(Long.parseLong(userId));
			LOGGER.info("<<deleteUser :: User deleted successfully with tweets, followers and replies");
			return Response.noContent().build();
		} catch (Exception ex) {
			LOGGER.error("<<deleteUser :: Failed {}", ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}
	
	@Override
	public Response getUsers() {
		try {
			LOGGER.info(">>getUsers");
			List<UserDTO> userDTOs = userRepo.findAll().stream().map(entry -> new UserDTO.Builder()
											.firstName(entry.getFirstName())
											.lastName(entry.getLastName())
											.userName(entry.getUserName())
											.userId(entry.getUserId())
											.build())
											.collect(Collectors.toCollection(ArrayList::new));
			LOGGER.info("<<getUsers :: list size {}", userDTOs.size());
			return Response.ok(userDTOs).build();
		}catch (Exception ex) {
			LOGGER.error("<<getUsers :: Failed {}", ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}
	
	@Override
	public Response followUser(String followerUserId, String followedUserId) {
		try {
			LOGGER.info(">>followUser");
			Optional<User> followerUserO = findUser(Long.parseLong(followerUserId));
			Optional<User> followedUserO = findUser(Long.parseLong(followedUserId));
			
			if(followedUserO.isPresent() && followerUserO.isPresent()) {
				
				User followedUser = followedUserO.get();
				User followerUser = followerUserO.get();
				
				if(!(followedUser.getUserId().equals(followerUser.getUserId()))) {
					followedUser.getFollowerUser().add(followerUser);
					userRepo.save(followedUser);
					LOGGER.info("<<User {} followed {}", followerUser.getUserName(), followedUser.getUserName());
					return Response.status(HttpStatus.OK.value()).build();
				}
				LOGGER.info("<<followUser is forbidden, either follower or followed user not found");
				return Response.status(HttpStatus.NOT_FOUND.value()).build();
			} 
			LOGGER.info("<<followUser is forbidden");
			return Response.status(HttpStatus.FORBIDDEN.value()).build();
		} catch (Exception ex) {
			LOGGER.info("<<followUser :: Failed {}", ex.getMessage());
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}

	@Override
	public Response unfollowUser(String fromUser, String toUserId) {
		try {

			Optional<User> followerUser = findUser(Long.parseLong(fromUser));
			Optional<User> followedUser = findUser(Long.parseLong(toUserId));

			if (followedUser.isPresent() && followerUser.isPresent()) {
				Long followerId = followerUser.get().getUserId();
				int followerSize = followedUser.get().getFollowerUser().size();
				followedUser.get().getFollowerUser().removeIf(entry -> entry.getUserId().equals(followerId));
				if (followerSize != followedUser.get().getFollowerUser().size()) {
					userRepo.save(followedUser.get());
					return Response.status(HttpStatus.OK.value()).build();
				}
				return Response.status(HttpStatus.NOT_FOUND.value()).build();
			}
			return Response.status(HttpStatus.FORBIDDEN.value()).build();
		} catch (Exception ex) {
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}

	@Override
	public Response getfollowers(String userId) {
		try {
			Optional<User> user = findUser(Long.parseLong(userId));
			List<UserDTO> result = new ArrayList<>();
			if (user.isPresent()) {
				result = user.get().getFollowerUser().stream().map(entry -> new UserDTO.Builder()
										.firstName(entry.getFirstName())
										.lastName(entry.getLastName())
										.userName(entry.getUserName())
										.userId(entry.getUserId())
										.build())
										.collect(Collectors.toCollection(ArrayList::new));
			}
			if (!result.isEmpty()) {
				return Response.ok(result).build();
			} else {
				return Response.noContent().build();
			}
		} catch (Exception ex) {
			return Response.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).build();
		}
	}

	private Optional<User> findUser(Long id) {
		return userRepo.findById(id);
	}

}
