## TODO

 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Set ContentType on xml using yaml
 + Javadoc
 + ~~Object mapper custom~~
 + ~~Basic auth~~
 + ~~Custom HandleBar Mapper~~
 + Delete

# CI

[![Build Status](https://api.travis-ci.org/CodeStory/code-story-http.png)](https://api.travis-ci.org/CodeStory/code-story-http.png)

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform

Go to https://oss.sonatype.org/, log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

