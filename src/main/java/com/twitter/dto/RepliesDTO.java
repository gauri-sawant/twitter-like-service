package com.twitter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;

/**
 * @author gauri sawant
 *
 */
@ApiModel(description = "Representation of a Reply")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = RepliesDTO.Builder.class)
public class RepliesDTO {

	@JsonProperty(value = "reply-id")
	private final Long replyId;

	@JsonProperty(value = "reply-text")
	private final String replyText;

	@JsonProperty(value = "reply-attachment")
	private final String replyAttachment;

	@JsonProperty(value = "reply-user")
	private final UserDTO user;

	private RepliesDTO(Builder builder) {
		this.replyId = builder.replyId;
		this.replyText = builder.replyText;
		this.replyAttachment = builder.replyAttachment;
		this.user = builder.user;
	}

	public Long getReplyId() {
		return replyId;
	}

	public String getReplyText() {
		return replyText;
	}

	public String getReplyAttachment() {
		return replyAttachment;
	}

	public UserDTO getUser() {
		return user;
	}

	public static class Builder extends BuilderConstraints<RepliesDTO> {

		@JsonProperty(value = "reply-id")
		private Long replyId;

		@JsonProperty(value = "reply-text")
		private String replyText;

		@JsonProperty(value = "reply-attachment")
		private String replyAttachment;

		@JsonProperty(value = "reply-user")
		private UserDTO user;

		public Builder replyId(Long replyId) {
			this.replyId = replyId;
			return this;
		}

		public Builder replyText(String replyText) {
			this.replyText = replyText;
			return this;
		}

		public Builder replyAttachment(String replyAttachment) {
			this.replyAttachment = replyAttachment;
			return this;
		}

		public Builder user(UserDTO user) {
			this.user = user;
			return this;
		}

		@Override
		public RepliesDTO getTargetObject() {
			return new RepliesDTO(this);
		}

	}
}
