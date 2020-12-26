Ting-el-Tangel has a [german website](http://www.ting-el-tangel.de/)

# Tingeltangel
A tool for the Ting Pen to create your own books.

# Prerequisites
* [Install Java 8 or higher](https://www.oracle.com/java/technologies/javase-downloads.html#JDK15).


# How to build the software
----
Clone the repository
```
git clone git@github.com:Martin-Dames/Tingeltangel.git
```
If you want to create your own installer on windows, make sure you [install WIX before](https://wixtoolset.org/)


Execute the following command to build and run the software:
```
gradlew run -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true --args='gui-editor'
```
Run following command in the repository root.
```
java -Dawt.useSystemAAFontSettings=on -Dswing.aatext=true -Dsun.java2d.xrender=true -jar tingeltangel/build/libs/application-0.8.0-SNAPSHOT-all.jar gui-editor
```

TODO
----

 * read YAML files from tttool software
 * extract strings to i18n-files

