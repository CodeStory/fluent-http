## TODO

 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Set ContentType on xml using yaml
 + Javadoc
 + ~~Object mapper custom~~
 + ~~Basic auth~~
 + ~~Custom HandleBar Mapper~~
 + ~~Delete~~
 + handle logs properly rather than 'sysouting' like a pig ;)
 + PROD_MODE is not really convenient. It forces to have more complex prod script
 + PROD_MODE and app.port should be also environement variables
 + Add some sort of http standard log

# CI

[![Build Status](https://api.travis-ci.org/CodeStory/code-story-http.png)](https://api.travis-ci.org/CodeStory/code-story-http.png)

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform

Go to https://oss.sonatype.org/, log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

