# twitter-like-service

Hello!

This service exposes a twitter like REST API.
API is exposed on the public server : https://twitter-like-service.herokuapp.com/

Following services are exposed at the moment :

User REST API:
1) Add a user
2) Follow a user
3) Unfollow a user
4) Get users
5) Get Followers for a user
6) Delete Users (Deletes all associations for the user - followers, tweets, replies)

Tweet REST API:
1) Add a tweet (Attachment field only accepts filenames)
2) Add a reply to the tweet (Attachment field only accepts filenames)
3) Get tweets of a user
4) Get a list of tweets of a user (including self-tweets and replies by followers)

Swagger URL : https://twitter-like-service.herokuapp.com/twitter/swagger.json

The API is accessible only using the access jwt token (legitimate users gets access token)

Project includes test cases to test all functions of the API and captured validation failures and behaviors (Using Rest Assured)
