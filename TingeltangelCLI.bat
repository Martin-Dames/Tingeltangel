IF EXIST open_jre_1.8.0/ (
	open_jre_1.8.0/bin/java -jar tingeltangel-0.6-jar-with-dependencies.jar cli
) ELSE (
	java -jar tingeltangel-0.6-jar-with-dependencies.jar cli
)