## TODO

 + Gestion des imports less qui doivent etre faits cote serveur pour fonctionner
 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Set ContenType on xml using yaml
 + Page 500 (bug photo)
 + Javadoc
 + Compatibilité Guice
 + Https

# Java 8 required

We currently only support java8 b92 version.

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform

Go to https://oss.sonatype.org/, log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

