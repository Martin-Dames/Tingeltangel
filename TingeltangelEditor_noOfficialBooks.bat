IF EXIST open_jre_1.8.0/ (
	start open_jre_1.8.0/bin/javaw -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true -jar tingeltangel-0.7-jar-with-dependencies.jar gui-editor disable-official-books
) ELSE (
	start javaw -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true -jar tingeltangel-0.7-jar-with-dependencies.jar gui-editor disable-official-books
)