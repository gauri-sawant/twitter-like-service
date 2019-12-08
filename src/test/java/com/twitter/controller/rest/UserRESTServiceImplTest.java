package com.twitter.controller.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
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

import com.twitter.dto.UserDTO;
import com.twitter.model.User;
import com.twitter.persistence.UserRepository;

@RunWith(MockitoJUnitRunner.class)
public class UserRESTServiceImplTest {

	@InjectMocks
	private UserRESTServiceImpl userRESTServiceImpl;

	@Mock
	private UserRepository userRepo;

	@Test
	public void shouldCreateNewUser() {

		User user = createUser(5L, "userName", "firstName", "lastName");
		when(userRepo.save(any(User.class))).thenReturn(user);
		UserDTO userDTO = createUserDTO();
		Response response = userRESTServiceImpl.createUser(userDTO);
		verify(userRepo).save(any(User.class));
		assertThat(response.getStatus(), is(201));
	}

	@Test
	public void shouldGetAllUsers() {

		when(userRepo.findAll()).thenReturn(createUserList());
		Response response = userRESTServiceImpl.getUsers();
		verify(userRepo).findAll();
		assertThat(((List<UserDTO>) response.getEntity()).size(), is(2));
		assertThat(response.getStatus(), is(200));
	}

	@Test
	public void shouldFollowUser() {

		User follower = createUser(5L, "userName1", "firstName1", "lastName1");
		User followed = createUser(6L, "userName2", "firstName2", "lastName2");
		Set<User> set = new HashSet<>();
		set.add(follower);
		followed.setFollowerUser(set);
		when(userRepo.findById(anyLong())).thenReturn(Optional.of(follower), Optional.of(followed));
		when(userRepo.save(any(User.class))).thenReturn(followed);
		Response response = userRESTServiceImpl.followUser("5", "6");
		verify(userRepo).save(any(User.class));
		assertThat(response.getStatus(), is(200));

	}

	@Test
	public void shouldUnFollowUser() {

		User follower = createUser(5L, "userName1", "firstName1", "lastName1");
		User followed = createUser(6L, "userName2", "firstName2", "lastName2");
		Set<User> set = followed.getFollowerUser();
		set.add(follower);
		followed.setFollowerUser(set);

		when(userRepo.findById(anyLong())).thenReturn(Optional.of(follower), Optional.of(followed));
		when(userRepo.save(any(User.class))).thenReturn(followed);
		Response response = userRESTServiceImpl.unfollowUser("5", "6");
		verify(userRepo).save(any(User.class));
		assertThat(response.getStatus(), is(200));

	}
	
	@Test
	public void shouldGetFollowers() {
		
		User follower1 = createUser(5L, "userName1", "firstName1", "lastName1");
		User follower2 = createUser(4L, "userName3", "firstName3", "lastName3");
		User followed = createUser(6L, "userName2", "firstName2", "lastName2");
		Set<User> set = followed.getFollowerUser();
		set.add(follower2);
		set.add(follower1);
		followed.setFollowerUser(set);
		when(userRepo.findById(anyLong())).thenReturn(Optional.of(followed));
		Response response = userRESTServiceImpl.getfollowers("6");
		verify(userRepo).findById(anyLong());
		assertThat(((List<UserDTO>) response.getEntity()).size(), is(2));
		assertThat(response.getStatus(), is(200));
	}
	
	private UserDTO createUserDTO() {
		return new UserDTO.Builder().firstName("firstName").lastName("lastName").userName("userName").build();
	}

	private User createUser(Long id, String userName, String firstName, String lastName) {
		User user = new User();
		user.setUserId(id);
		user.setUserName(userName);
		user.setFirstName(firstName);
		user.setLastName(lastName);
		return user;
	}

	private List<User> createUserList() {
		List<User> userList = new ArrayList<User>();
		userList.add(createUser(5L, "userName1", "firstName1", "lastName1"));
		userList.add(createUser(6L, "userName2", "firstName2", "lastName2"));
		return userList;
	}

}
