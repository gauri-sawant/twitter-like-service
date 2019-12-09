package com.twitter.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;

/**
 * @author gauri sawant
 *
 */
@ApiModel(description = "Representation of a Tweet and Replies")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = TweetRepliesDTO.Builder.class)
public class TweetRepliesDTO {

	@JsonProperty(value = "tweet")
	private final TweetDTO tweetDTO;

	@JsonProperty(value = "replies")
	private final List<RepliesDTO> repliesDTOs;

	private TweetRepliesDTO(Builder builder) {
		this.tweetDTO = builder.tweetDTO;
		this.repliesDTOs = builder.repliesDTOs;
	}

	public TweetDTO getTweetDTO() {
		return tweetDTO;
	}

	public List<RepliesDTO> getRepliesDTOs() {
		return repliesDTOs;
	}

	public static class Builder extends BuilderConstraints<TweetRepliesDTO> {

		@JsonProperty(value = "tweet")
		private TweetDTO tweetDTO;

		@JsonProperty(value = "replies")
		private List<RepliesDTO> repliesDTOs;

		public Builder tweetDTO(TweetDTO tweetDTO) {
			this.tweetDTO = tweetDTO;
			return this;
		}

		public Builder repliesDTOs(List<RepliesDTO> repliesDTOs) {
			this.repliesDTOs = repliesDTOs;
			return this;
		}

		@Override
		public TweetRepliesDTO getTargetObject() {
			return new TweetRepliesDTO(this);
		}

	}
}
