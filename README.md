## TODO

 + POST...
 + Gestion des imports less qui doivent etre faits cote serveur pour fonctionner
 + Supporter les coffee et less pré-générés
 + Supporter les templates pré-générés
 + Use file structure as yaml
 + Set ContenType on xmls using yaml
 + Déployer sur maven central
 + Query params
 + Page 500 (bug photo)
 + Access yaml on server side
 + Better hot reload
 + Javadoc
 + Compatibilité Guice
 + Security for free
 + Https

# Deploy on Maven Central

Build the release :

	mvn release:clean
	mvn release:prepare
	mvn release:perform

Go to https://oss.sonatype.org/, log in, go to **Staging Repositories**, close the *netcode-story-XXXX* repository then release it.
Synchro to Maven Central is done hourly.

