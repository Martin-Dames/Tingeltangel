#!/bin/bash

VERSION=`date -u +%y%j`
NAME="Tingeltangel-$VERSION"

rm -r __build 2> /dev/null
mkdir __build

javac -d __build/ -source 1.6 -target 1.6 -cp src src/tingeltangel/Tingeltangel.java

cp src/id_trans.data __build/
cp src/commands.properties __build/
cp src/menu.properties __build/

rm -r $NAME 2> /dev/null
mkdir $NAME

cd __build
jar -cmf ../manifest.mf ../$NAME/Tingeltangel.jar *
cd ..
echo $VERSION > $NAME/VERSION
date -u >> $NAME/VERSION

cp sample.png Tingeltangel.sh Tingeltangel.bat $NAME/
# cp -r win_sox $NAME/
# cp -r win_mpg123 $NAME/
cp -r html $NAME/

zip -r $NAME.zip $NAME > /dev/null

rm -r __build
rm -r $NAME


