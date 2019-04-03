IF EXIST open_jre_1.8.0/ (
	start open_jre_1.8.0/bin/javaw -jar tingeltangel-0.6-jar-with-dependencies.jar gui-manager
) ELSE (
	start javaw -jar tingeltangel-0.6-jar-with-dependencies.jar gui-manager
)