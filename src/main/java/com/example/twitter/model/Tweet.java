package com.example.twitter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import io.swagger.annotations.ApiModel;

@Entity
@Table(name = "TWEETS")
@ApiModel(description = "Tweets")
@NamedQueries({
    @NamedQuery(name = "Tweet.getTweets", query = "SELECT t FROM Tweet t WHERE t.user.userId = :userId")
})
public class Tweet implements Serializable {
	private static final long serialVersionUID = 1482309747768497290L;

	@Id
	@Column(name = "TWEET_ID", unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long tweetId;

	@Column(name = "TEXT")
	private String text;

	@Lob
	@Column(name = "ATTACHMENT")
	private byte[] attachment;

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;
	
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "tweet", fetch = FetchType.EAGER)
    private List<Replies> replies = new ArrayList<>();

	public void setTweetId(Long tweetId) {
		this.tweetId = tweetId;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getTweetId() {
		return tweetId;
	}

	public String getText() {
		return text;
	}

	public byte[] getAttachment() {
		return attachment;
	}

	public User getUser() {
		return user;
	}

	public List<Replies> getReplies() {
		return replies;
	}

	public void setReplies(List<Replies> replies) {
		this.replies = replies;
	}
	
	public Tweet() {
		super();
	}
	
	public Tweet(Long tweetId) {
		this.tweetId = tweetId;
	}
}
