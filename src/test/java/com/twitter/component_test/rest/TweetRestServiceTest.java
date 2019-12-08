package com.twitter.component_test.rest;

import static com.jayway.restassured.RestAssured.given;
import static org.assertj.core.api.Assertions.assertThat;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.response.Response;
import com.twitter.TwitterApplication;
import com.twitter.controller.rest.UserRESTServiceImpl;
import com.twitter.dto.TweetDTO;
import com.twitter.persistence.UserRepository;

/**
 * @author gauri sawant
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(classes = TwitterApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class TweetRestServiceTest {

	private static final String HEADER_KEY = "x-jwt-assertion";
	private static final String HEADER_VALUE = "eyJ0eXAiOiJKV1QiLCJhbGciOiJSUzI1NiIsIng1dCI6Ik1HWmlObUU1WVdaaE5qVmpOekUxTVdJMllqUmtPVGczWkRaaE1URmpPR05oT1Roa05tRTRZUSJ9.eyJzdWIiOiJwZ2Fpa0BuZXRzLmV1IiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb250aWVyIjoiVW5saW1pdGVkIiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wva2V5dHlwZSI6IlBST0RVQ1RJT04iLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC92ZXJzaW9uIjoiMS4yIiwiaXNzIjoid3NvMi5vcmdcL3Byb2R1Y3RzXC9hbSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2FwcGxpY2F0aW9ubmFtZSI6Ik5BQS1BZG1pbi1VSSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2VuZHVzZXIiOiJwZ2Fpa0BuZXRzLmV1QGNhcmJvbi5zdXBlciIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2VuZHVzZXJUZW5hbnRJZCI6Ii0xMjM0IiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb25VVUlkIjoiMTc0NmM2MjctYmM5NS00MTEzLTg4ZjAtOWU1Y2IwM2M2ZjUyIiwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvc3Vic2NyaWJlciI6Ik5FVFMuRVVcL2lsb3JpQG5ldHMuZXUiLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC90aWVyIjoiR29sZCIsImV4cCI6MTU1NzMxMDYxMSwiaHR0cDpcL1wvd3NvMi5vcmdcL2NsYWltc1wvYXBwbGljYXRpb25pZCI6IjEzNjEiLCJodHRwOlwvXC93c28yLm9yZ1wvY2xhaW1zXC91c2VydHlwZSI6IkFQUExJQ0FUSU9OX1VTRVIiLCJNdWx0aUF0dHJpYnV0ZVNlcGFyYXRvciI6W10sImVtYWlsIjoicGdhaWtAbmV0cy5ldSIsImh0dHA6XC9cL3dzbzIub3JnXC9jbGFpbXNcL2FwaWNvbnRleHQiOiJcL21zXC9hYXBheVwvcmVwb3J0aW5nXC8xLjIifQ==.NBZG8Yea0DCU1gLKYXFQC15ThfGLoQ5Jfhc290hoHD9Umjs3OqB8GmZUkfS8zjFMgfj7rD8b9G8Z1Ytnduox3d+uzi3sfisbFidw2T4pXm1j/J+RVoehe5K4unISnhtedzAokpJsUlKa6HHUzu8mREF2XKNzhNiSP/8nsU7uyysSpfQbu7AancVOMAL6P2zBGld+UcRz0vQlWigBTJr1N3XGyIU54FvRCu15JT+SLYX6jDH6w80BAohj/lay/FzBz+cZxOlSftc2/KFQyR5ZM4rbHDkacFXKeawgla1Odztm+83gknr2zhDxFhao1kmmeM6LGMi3NcEtizUAiwzAUrhQNRCSEwv4sN1Up2IAiSlDJu4ttZ2J+Y6LjIKaA7nELcTJcQyPXeG70sgt3IKnMjujrOrw6D6N5eLrZ9J9+tIgb1YnmafqpvpdysF09bI01vo8K7qggKFy745a412jFGhVikgf8E4+yrOlk8eHZXM7UZrxJOBzO4izQeevtEIhvZ/EyYmFbGjnvh1szNaCvwICZvihbsEbz+rp3uR020JJJoy5pNh2alcGwMyL7r4cVZksuJ10xGhgOwn7aCMjxj/nIPcYCFmJ7uwtqckwol1s00w0nZJ352OVlAH8xKLlOsFN5jAvh0oevnMWKRgm7s9WuHKGXv0nAuAq8Y7ZvpI=";

	
	 private static String tweetPayload = "{\n" +
		        "  \"tweet-text\": \"Tweet Added for User\",\n" +
		        "  \"tweet-attachment\": \"\"\n" +
		        "}";
	 
	 private static String replyPayload = "{\n" +
		        "  \"reply-text\": \"Reply Added for User\",\n" +
		        "  \"reply-attachment\": \"\"\n" +
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
	public void shouldGiveUnauthorizedAccessWhenHeaderNotPresent() {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.get("/tweet/getTweets/1")
				.then()
				.statusCode(401)
				.log()
				.body()
				.extract().response();
	}
	
	@Test
	public void shouldCreateTweetsForUser() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(tweetPayload)
				.post("/tweet/createTweet/1")
				.then()
				.statusCode(201)
				.log()
				.body().extract()
				.response();

		Response response = given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.get("/tweet/getTweets/1")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();

		TweetDTO[] tweetDTOs = objectMapper.readValue(response.getBody().asString(), TweetDTO[].class);
		assertThat(tweetDTOs.length).isEqualTo(3);
	}
	
	
	@Test
	public void shouldGetTweetsForUser() throws JsonMappingException, JsonProcessingException {

		Response response = given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.get("/tweet/getTweets/1")
				.then()
				.statusCode(200)
				.log()
				.body().extract()
				.response();

		TweetDTO[] tweetDTOs = objectMapper.readValue(response.getBody().asString(), TweetDTO[].class);
		assertThat(tweetDTOs).isNotNull();
	}
	
	
	@Test
	public void shouldAddReplyForTweetUser() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(replyPayload)
				.post("/tweet/addReply/1/2")
				.then()
				.statusCode(201)
				.log()
				.body().extract()
				.response();
	} 
	
	@Test
	public void shouldNotAddReplyWhenUserNotFound() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(replyPayload)
				.post("/tweet/addReply/1/10")
				.then()
				.statusCode(404)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldNotAddTweetWhenUserNotFound() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(tweetPayload)
				.post("/tweet/createTweet/10")
				.then()
				.statusCode(404)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldNotAddTweetWhenIncorrectUserId() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(tweetPayload)
				.post("/tweet/createTweet/PP")
				.then()
				.statusCode(400)
				.log()
				.body().extract()
				.response();
	}
	
	
	@Test
	public void shouldNotAddReplyWhenIncorrectUserId() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(replyPayload)
				.post("/tweet/addReply/1/RR")
				.then()
				.statusCode(400)
				.log()
				.body().extract()
				.response();
	}
	
	@Test
	public void shouldNotAddReplyWhenTweetIdNotFound() throws JsonMappingException, JsonProcessingException {

		given().when()
				.accept(MediaType.APPLICATION_JSON)
				.contentType(MediaType.APPLICATION_JSON)
				.header(HEADER_KEY, HEADER_VALUE)
				.body(replyPayload)
				.post("/tweet/addReply/100/1")
				.then()
				.statusCode(404)
				.log()
				.body().extract()
				.response();
	}
}
