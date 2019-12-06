package com.example.twitter.controller.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.ws.rs.core.MediaType;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;

import com.example.twitter.TwitterApplication;
import com.example.twitter.controller.rest.UserRESTServiceImpl;
import com.example.twitter.dto.UserDTO;
import com.example.twitter.model.User;
import com.example.twitter.persistence.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwitterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class UserRestServiceTest {

	private static final String HEADER_KEY = "x-jwt-assertion";
	private static final String HEADER_VALUE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik1HWmlObUU1WVdaaE5qVmpOekUxTVdJMllqUmtPVGczWkRaaE1URmpPR05oT1Roa05tRTRZUSJ9.eyJzdWIiOiJwZ2Fpa0BuZXRzLmV1IiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb250aWVyIjoiVW5saW1pdGVkIiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wva2V5dHlwZSI6IlBST0RVQ1RJT04iLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC92ZXJzaW9uIjoiMS4yIiwiaXNzIjoid3NvMi5vcmdcL3Byb2R1Y3RzXC9hbSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2FwcGxpY2F0aW9ubmFtZSI6Ik5BQS1BZG1pbi1VSSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2VuZHVzZXIiOiJwZ2Fpa0BuZXRzLmV1QGNhcmJvbi5zdXBlciIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2VuZHVzZXJUZW5hbnRJZCI6Ii0xMjM0IiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb25VVUlkIjoiMTc0NmM2MjctYmM5NS00MTEzLTg4ZjAtOWU1Y2IwM2M2ZjUyIiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvc3Vic2NyaWJlciI6Ik5FVFMuRVVcL2lsb3JpQG5ldHMuZXUiLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC90aWVyIjoiR29sZCIsImV4cCI6MTU1NzMxMDYxMSwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb25pZCI6IjEzNjEiLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC91c2VydHlwZSI6IkFQUExJQ0FUSU9OX1VTRVIiLCJNdWx0aUF0dHJpYnV0ZVNlcGFyYXRvciI6W10sImVtYWlsIjoicGdhaWtAbmV0cy5ldSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2FwaWNvbnRleHQiOiJcL21zXC9hYXBheVwvcmVwb3J0aW5nXC8xLjIifQ==.NBZG8Yea0DCU1gLKYXFQC15ThfGLoQ5Jfhc290hoHD9Umjs3OqB8GmZUkfS8zjFMgfj7rD8b9G8Z1Ytnduox3d+uzi3sfisbFidw2T4pXm1j/J+RVoehe5K4unISnhtedzAokpJsUlKa6HHUzu8mREF2XKNzhNiSP/8nsU7uyysSpfQbu7AancVOMAL6P2zBGld+UcRz0vQlWigBTJr1N3XGyIU54FvRCu15JT+SLYX6jDH6w80BAohj/lay/FzBz+cZxOlSftc2/KFQyR5ZM4rbHDkacFXKeawgla1Odztm+83gknr2zhDxFhao1kmmeM6LGMi3NcEtizUAiwzAUrhQNRCSEwv4sN1Up2IAiSlDJu4ttZ2J+Y6LjIKaA7nELcTJcQyPXeG70sgt3IKnMjujrOrw6D6N5eLrZ9J9+tIgb1YnmafqpvpdysF09bI01vo8K7qggKFy745a412jFGhVikgf8E4+yrOlk8eHZXM7UZrxJOBzO4izQeevtEIhvZ/EyYmFbGjnvh1szNaCvwICZvihbsEbz+rp3uR020JJJoy5pNh2alcGwMyL7r4cVZksuJ10xGhgOwn7aCMjxj/nIPcYCFmJ7uwtqckwol1s00w0nZJ352OVlAH8xKLlOsFN5jAvh0oevnMWKRgm7s9WuHKGXv0nAuAq8Y7ZvpI=";

	
	 private static String userPayload = "{\n" +
		        "  \"user-name\": \"Some Username\",\n" +
		        "  \"first-name\": \"Some firstname\",\n" +
		        "  \"last-name\": \"Some lastname\"\n" +
		        "}";
	 
	 private static String existingUserNamePayload = "{\n" +
		        "  \"user-name\": \"gasaw\",\n" +
		        "  \"first-name\": \"Some firstname\",\n" +
		        "  \"last-name\": \"Some lastname\"\n" +
		        "}";
	
	@Resource
	private UserRepository userRepository;

	@Inject
	UserRESTServiceImpl userRESTServiceImpl;

	@Autowired
	private ObjectMapper objectMapper;

	@LocalServerPort
	protected int port;

	@Before
	public void setup() {
		RestAssured.baseURI = "http://localhost";
		RestAssured.basePath = "/twitter";
		RestAssured.port = port;
		
		
		given().when()
		.accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)
		.header(HEADER_KEY, HEADER_VALUE)
		.post("/user/follow/follower/2/follow/1")
		.then()
		.statusCode(200)
		.log()
		.body().extract()
		.response();
	}

	@Test
	public void shouldGiveUnauthorizedAccessWhenHeaderNotPresent()
			throws JsonMappingException, JsonProcessingException {

		given().when().accept(MediaType.APPLICATION_JSON)
		.contentType(MediaType.APPLICATION_JSON)
		.get("/user/getUsers")
		.then()
		.statusCode(401)
		.log()
		.body()
		.extract().response();
	}

	@Test
	public void shouldGetUsers() throws JsonMappingException, JsonProcessingException {

		Response response = given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.get("/user/getUsers")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();

		UserDTO[] userDTOs = objectMapper.readValue(response.getBody().asString(), UserDTO[].class);
		assertThat(userDTOs).isNotNull();
	}
	
	@Test
	public void shouldCreateUser() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(userPayload)
				.post("/user/createUser")
				.then()
				.statusCode(201)
				.log()
				.body().extract()
				.response();
		
		Response userReponse = given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.get("/user/getUsers")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();

		UserDTO[] userDTOs = objectMapper.readValue(userReponse.getBody().asString(), UserDTO[].class);
		assertThat(userDTOs.length).isEqualTo(5);
	}
	
	@Test
	public void shouldNotCreateUserIfUsernameExists() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(existingUserNamePayload)
				.post("/user/createUser")
				.then()
				.statusCode(409)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldFollowUser() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/follow/follower/3/follow/1")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();
		
		Optional<User> user = userRESTServiceImpl.findUser(1L);
		assertThat(user.get().getFollowerUser()).isNotNull();
		assertThat(user.get().getFollowerUser().size()).isEqualTo(2);
	}
	
	@Test
	public void shouldNotFollowUserWhenWrongUserId() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/follow/follower/ÅÅ/follow/PP")
				.then()
				.statusCode(400)
				.log()
				.body().extract()
				.response();
		
	}
	
	@Test
	public void shouldNotFollowSameUserId() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/follow/follower/1/follow/1")
				.then()
				.statusCode(403)
				.log()
				.body().extract()
				.response();
		
	}
	
	@Test
	public void shouldUnFollowUser() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/unfollow/follower/2/unfollow/1")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();
		
		Optional<User> user = userRESTServiceImpl.findUser(1L);
		assertThat(user.get().getFollowerUser().size()).isEqualTo(0);
	}
	
	
	@Test
	public void shouldNotUnFollowUserWhenWrongUserId() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/unfollow/follower/ÅÅ/unfollow/PP")
				.then()
				.statusCode(400)
				.log()
				.body().extract()
				.response();
		
	}
	
	@Test
	public void shouldNotUnFollowUserWhenUserNotPresent() throws JsonMappingException, JsonProcessingException {
		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/unfollow/follower/9/unfollow/1")
				.then()
				.statusCode(404)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldNotUnFollowUserWhenFollowerNotFound() throws JsonMappingException, JsonProcessingException {
		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.post("/user/unfollow/follower/3/unfollow/1")
				.then()
				.statusCode(403)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldGetFollowers() throws JsonMappingException, JsonProcessingException {

		Response response = given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.get("/user/getfollowers/1")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();

		UserDTO[] userDTOs = objectMapper.readValue(response.getBody().asString(), UserDTO[].class);
		assertThat(userDTOs).isNotNull();
		assertThat(userDTOs.length).isEqualTo(1);
	}
	
	@Test
	public void shouldDeleteUser() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.delete("/user/deleteUser/4")
				.then()
				.statusCode(204)
				.log()
				.body().extract()
				.response();

		Optional<User> user = userRESTServiceImpl.findUser(4L);
		assertThat(user.isPresent()).isEqualTo(false);
	}
	
	@Test
	public void shouldNotDeleteUserWhenUserNotFound() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.delete("/user/deleteUser/99")
				.then()
				.statusCode(500)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldNotDeleteUserWhenBadUserId() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.delete("/user/deleteUser/PP")
				.then()
				.statusCode(400)
				.log()
				.body().extract()
				.response();
	}
}
