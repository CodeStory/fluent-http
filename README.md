## TODO

 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Set ContenType on xml using yaml
 + Javadoc
 + Compatibilité Guice

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform

Go to https://oss.sonatype.org/, log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

