# Code-Story Http

This is the simplest fastest full fledged http server we could come up with.

# Build status

[![Build Status](https://api.travis-ci.org/CodeStory/code-story-http.png)](https://api.travis-ci.org/CodeStory/code-story-http.png)

# Build instructions

Prerequisites
- Java 1.8
- Apache Maven 3

Build the project

    mvn verify

## Generate missing licenses

	mvn license:format

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform

Go to [https://oss.sonatype.org/](https://oss.sonatype.org/), log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

# TODO

 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Javadoc
 + PROD_MODE is not really convenient. It forces to have more complex prod script
 + Add some sort of http standard log
 + Cleanup Payload class. Make Payload immutable?
 + Cors support
 + Principal
 + monitoring
