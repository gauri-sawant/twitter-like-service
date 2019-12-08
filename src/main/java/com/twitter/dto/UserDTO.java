package com.twitter.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;

/**
 * @author gauri sawant
 *
 */
@ApiModel(description = "Representation of a User")
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = UserDTO.Builder.class)
public class UserDTO {

	@JsonProperty(value = "user-id")
	private final Long userId;

	@NotNull
	@NotBlank
	@JsonProperty(value = "user-name")
	private final String userName;

	@JsonProperty(value = "first-name")
	private final String firstName;

	@JsonProperty(value = "last-name")
	private final String lastName;

	private UserDTO(Builder builder) {
		this.userId = builder.userId;
		this.userName = builder.userName;
		this.firstName = builder.firstName;
		this.lastName = builder.lastName;
	}

	public Long getUserId() {
		return userId;
	}

	public String getUserName() {
		return userName;
	}

	public String getFirstName() {
		return firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public static class Builder extends BuilderConstraints<UserDTO> {

		@JsonProperty(value = "user-id")
		private Long userId;

		@JsonProperty(value = "user-name")
		private String userName;

		@JsonProperty(value = "first-name")
		private String firstName;

		@JsonProperty(value = "last-name")
		private String lastName;

		public Builder userId(Long userId) {
			this.userId = userId;
			return this;
		}

		public Builder userName(String userName) {
			this.userName = userName;
			return this;
		}

		public Builder firstName(String firstName) {
			this.firstName = firstName;
			return this;
		}

		public Builder lastName(String lastName) {
			this.lastName = lastName;
			return this;
		}

		@Override
		public UserDTO getTargetObject() {
			return new UserDTO(this);
		}
	}
}
