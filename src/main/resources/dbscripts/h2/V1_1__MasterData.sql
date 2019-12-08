  
  insert into users(user_id, user_name, first_name, last_name) values(1, 'gasaw', 'Gauri', 'Sawant');
  insert into users(user_id, user_name, first_name, last_name) values(2, 'sgore', 'Sanket', 'Gore');
  insert into users(user_id, user_name, first_name, last_name) values(3, 'skulk', 'Shardul', 'Kulkarni');
  insert into users(user_id, user_name, first_name, last_name) values(4, 'pjadh', 'Priyanka', 'Jadhav');
  
  insert into tweets(tweet_id, text, attachment_file_name, user_id) values(1, 'Hello there!!', null, 1);
  insert into tweets(tweet_id, text, attachment_file_name, user_id) values(2, 'Planning to travel to Delhi(India), any suggestions?', null, 1);
  
  
  insert into replies(reply_id, text, attachment_file_name, user_id, tweet_id) values(1, 'Have paratha at Chandani Chowk', null, 2, 2);
  insert into replies(reply_id, text, attachment_file_name, user_id, tweet_id) values(2, 'Visit the Rajpath Bhavan', null, 3, 2);
  insert into replies(reply_id, text, attachment_file_name, user_id, tweet_id) values(3, 'Its pretty cold now, carry some warm clothes', null, 4, 2);