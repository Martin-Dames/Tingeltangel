#!/bin/bash

JAR=target/tingeltangel-0.1-beta2-jar-with-dependencies.jar

java -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true \
    -jar $JAR > tt.log 2>&1
