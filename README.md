# TargetGitRepos

This is an application which displays Trending Git repos in a list format.

Below are the points considered while developing this application.

1. Have used GitHub API to get the trending repositories : 
   https://github-trending-api.now.sh/repositories

2. Have shown the trending repos in “a list“ on the home page with repository name, username, avatar, language, stars and forks.

    

3. Have added ability to search by name. 

4. Have provided a filter that will sort the list alphabetically.

5. Have provided a filter that will sort the list based on Stars.

4. Have placed a mechanism such that application can be used without internet.
(if the data was fetched and then the application is used without internet, it shows previous cached data).

5. On clicking on “any items from the list” the app takes user to another screen and
shows “all the details” for that particular repository. Eg.

     Username - google
     avatar - Image
     Language, Stars, Forks
     Name - Google
     Description
     Git url - https://github.com/google

6. Have provided pull down to refresh the data.

7. Mechanism for Offline data expiry, after 2 hours time the offline cached data will get expired and fresh data will be downloaded when user opens the app