package com.twitter.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.twitter.model.User;

/**
 * @author gauri sawant
 *
 */
public interface UserRepository extends JpaRepository<User, Long> {
	
	int getUserNameCount(@Param("userName") String userName);
	
	@Modifying
	@Query(value="DELETE FROM FOLLOWERS f WHERE f.FOLLOWED_USER_ID = ?1 OR f.FOLLOWER_USER_ID = ?1", nativeQuery=true)
	void clearFollowers(@Param("userId") Long userId);
}
