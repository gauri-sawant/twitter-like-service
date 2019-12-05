package com.example.twitter.model;

import java.io.Serializable;

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
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import io.swagger.annotations.ApiModel;

@Entity
@Table(name = "REPLIES")
@ApiModel(description = "Replies")
@NamedQueries({
    @NamedQuery(name = "Replies.getTweetsWithReplies", query = "SELECT r FROM Replies r "
    		+ "WHERE r.tweet.tweetId = :tweetId")
})
public class Replies implements Serializable {
	private static final long serialVersionUID = 1482909747968497290L;

	@Id
	@Column(name = "REPLY_ID", unique = true)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long replyId;

	@Column(name = "TEXT")
	private String text;

	@Lob
	@Column(name = "ATTACHMENT")
	private byte[] attachment;

	@ManyToOne
	@JoinColumn(name = "USER_ID", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TWEET_ID", nullable = false)
	private Tweet tweet;

	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}

	public void setText(String text) {
		this.text = text;
	}

	public void setAttachment(byte[] attachment) {
		this.attachment = attachment;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setTweet(Tweet tweet) {
		this.tweet = tweet;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Long getReplyId() {
		return replyId;
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

	public Tweet getTweet() {
		return tweet;
	}

	public Replies() {
		super();
	}

}
