# spring-oauth2-discord

Updated this thing because I'm sure there are more people struggling to use OAuth2 with Discord as an identity provider...

Many thanks to Reddit user GoldRenard who pointed me to the right direction.

The original OAuth2 tutorial:

https://spring.io/guides/tutorials/spring-boot-oauth2/

Reddit thread related to the solution:

https://www.reddit.com/r/springsource/comments/6kb9ih/spring_security_oauth2_and_discord/

## Running the service

```
mvn clean package
cd target
java -jar -DCLIENT_ID=${CLIENT_ID} -DCLIENT_SECRET=${CLIENT_SECRET} spring-oauth2-discord-0.0.1.jar
```

## Optional things that might be of some use

Discord OAuth2 information:

https://discordapp.com/developers/docs/topics/oauth2

Discord apps:

https://discordapp.com/developers/applications/me

About creating certificates:

http://nemerosa.ghost.io/2015/07/06/enabling-ssl-with-spring-boot/