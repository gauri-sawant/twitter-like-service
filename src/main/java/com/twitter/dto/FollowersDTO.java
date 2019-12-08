package com.twitter.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import io.swagger.annotations.ApiModel;

@ApiModel(
        description = "Representation of a Follower"
)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonDeserialize(builder = FollowersDTO.Builder.class)
public class FollowersDTO {
	
	@JsonProperty(value = "follower-user-id")
	private final int followerUserId;
	
	@JsonProperty(value = "followed-user-id")
	private final int followedUserId;
	
	
    private FollowersDTO(Builder builder) {
        this.followerUserId = builder.followerUserId;
        this.followedUserId = builder.followedUserId;
    }
	
	public static class Builder extends BuilderConstraints<FollowersDTO> {
		
		@JsonProperty(value = "follower-user-id")
		private int followerUserId;
		
		@JsonProperty(value = "followed-user-id")
		private int followedUserId;
		
		public Builder followerUserId(int followerUserId) {
            this.followerUserId = followerUserId;
            return this;
        }
        
        public Builder followedUserId(int followedUserId) {
            this.followedUserId = followedUserId;
            return this;
        }

        @Override
        public FollowersDTO getTargetObject() {
            return new FollowersDTO(this);
        }

	}
}
