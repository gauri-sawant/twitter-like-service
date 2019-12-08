package com.twitter.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.twitter.model.Tweet;

/**
 * @author gauri sawant
 *
 */
public interface TweetRepository extends JpaRepository<Tweet, Long> {

	List<Tweet> getTweets(@Param("userId") Long userId);

	/*@Modifying
	@Query("DELETE FROM Tweet t WHERE t.user.userId = ?1")
	void deleteUserTweets(@Param("userId") Long userId);*/
}
