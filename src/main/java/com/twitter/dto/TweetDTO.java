package com.twitter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;

@ApiModel(description = "Representation of a Tweet")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = TweetDTO.Builder.class)
public class TweetDTO {

	@JsonProperty(value = "tweet-id")
	private final Long tweetId;

	@JsonProperty(value = "tweet-text")
	private final String tweetText;

	@JsonProperty(value = "tweet-attachment")
	private final String tweetAttachment;

	@JsonProperty(value = "tweet-user")
	private final UserDTO user;

	private TweetDTO(Builder builder) {
		this.tweetId = builder.tweetId;
		this.tweetText = builder.tweetText;
		this.tweetAttachment = builder.tweetAttachment;
		this.user = builder.user;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public String getTweetText() {
		return tweetText;
	}

	public String getTweetAttachment() {
		return tweetAttachment;
	}

	public UserDTO getUser() {
		return user;
	}

	public static class Builder extends BuilderConstraints<TweetDTO> {

		@JsonProperty(value = "tweet-id")
		private Long tweetId;

		@JsonProperty(value = "tweet-text")
		private String tweetText;

		@JsonProperty(value = "tweet-attachment")
		private String tweetAttachment;

		@JsonProperty(value = "tweet-user")
		private UserDTO user;

		public Builder tweetId(Long tweetId) {
			this.tweetId = tweetId;
			return this;
		}

		public Builder tweetText(String tweetText) {
			this.tweetText = tweetText;
			return this;
		}

		public Builder tweetAttachment(String tweetAttachment) {
			this.tweetAttachment = tweetAttachment;
			return this;
		}

		public Builder user(UserDTO user) {
			this.user = user;
			return this;
		}

		@Override
		public TweetDTO getTargetObject() {
			return new TweetDTO(this);
		}

	}
}
