#!/bin/bash
OPTIONS="-Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true"

start jre/bin/javaw $OPTIONS -jar tingeltangel-0.7-jar-with-dependencies.jar andersicht
