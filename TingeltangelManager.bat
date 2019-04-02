IF EXIST jre/ (
	jre/bin/java -jar tingeltangel-0.6-jar-with-dependencies.jar gui-manager
) ELSE (
	java -jar tingeltangel-0.6-jar-with-dependencies.jar gui-manager
)