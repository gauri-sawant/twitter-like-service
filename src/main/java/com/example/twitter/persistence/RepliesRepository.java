package com.example.twitter.persistence;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import com.example.twitter.model.Replies;

public interface RepliesRepository extends JpaRepository<Replies, Long> {

	List<Replies> getTweetsWithReplies(@Param("tweetId") Long tweetId);

	/*@Modifying
	@Query("DELETE FROM Replies r WHERE r.user.userId = ?1")
	void deleteUserReplies(@Param("userId") Long userId);*/
}
