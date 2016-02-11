#!/bin/bash

JAR=tingeltangel-0.2-beta3-jar-with-dependencies.jar

java -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true -jar $JAR gui-editor
