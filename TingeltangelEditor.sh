#!/bin/bash

JAR=tingeltangel-0.6-jar-with-dependencies.jar

OPTIONS=""
OPTIONS="$OPTIONS -Dawt.useSystemAAFontSettings=on"
OPTIONS="$OPTIONS -Dswing.aatext=true"
OPTIONS="$OPTIONS -Dsun.java2d.xrender=true"

java $OPTIONS -jar $JAR gui-editor
