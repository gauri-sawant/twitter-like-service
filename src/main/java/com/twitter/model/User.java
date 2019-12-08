package com.twitter.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import io.swagger.annotations.ApiModel;

/**
 * @author gauri sawant
 *
 */
@Entity
@Table(name = "USERS")
@ApiModel(description = "Users")
@NamedQueries({
    @NamedQuery(name = "User.getUserNameCount", query = "SELECT count(u) FROM User u WHERE u.userName = :userName")
})
public class User implements Serializable {
    private static final long serialVersionUID = 1482309747968497290L;

    @Id
    @Column(name = "USER_ID", unique = true)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;
    
    @Column(name = "USER_NAME")
    private String userName;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;
    
	@ManyToMany(cascade={CascadeType.PERSIST}, fetch = FetchType.EAGER)
	@JoinTable(name="FOLLOWERS",
		joinColumns={@JoinColumn(name="FOLLOWED_USER_ID")},
		inverseJoinColumns={@JoinColumn(name="FOLLOWER_USER_ID")})
	private Set<User> followerUser = new HashSet<>();

	@ManyToMany(mappedBy="followerUser")
	private Set<User> followedUser = new HashSet<>();
   
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Replies> replies = new ArrayList<>();
    
    @OneToMany(cascade = CascadeType.REMOVE, mappedBy = "user")
    private List<Tweet> tweet = new ArrayList<>();
	
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public void setFollowedUser(Set<User> followedUser) {
		this.followedUser = followedUser;
	}

	public void setFollowerUser(Set<User> followerUser) {
		this.followerUser = followerUser;
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

	public Set<User> getFollowedUser() {
		return followedUser;
	}

	public Set<User> getFollowerUser() {
		return followerUser;
	}
	
	public List<Replies> getReplies() {
		return replies;
	}

	public void setReplies(List<Replies> replies) {
		this.replies = replies;
	}

	public List<Tweet> getTweet() {
		return tweet;
	}

	public void setTweet(List<Tweet> tweet) {
		this.tweet = tweet;
	}

	public User() {
		super();
	}
	
	public User(Long userId) {
		this.userId = userId;
	}
	
	public User(Long userId, String userName, String firstName, String lastName) {
		this.userId = userId;
		this.userName = userName;
		this.firstName = firstName;
		this.lastName = lastName;
	}
}
