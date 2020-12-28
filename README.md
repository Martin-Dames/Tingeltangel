# Tingeltangel
A tool for the Ting Pen to create your own books. Atm there's only a 
[german website](http://www.ting-el-tangel.de/) providing some documentation.

# How to install and run the software

Download the installer, install and run the Application.

# How to build the software
1. Make sure you've the Java JDK 14 installed. (currently there's a bug with Java 15)
2. Clone the repository
```
git clone git@github.com:Martin-Dames/Tingeltangel.git
```
3. Execute the following command to build and run the software:
```
gradlew run
```
4. If you want to create your own installer run:
```
gradlew jpackage
```
## Notes: 
* jpackage doesn't allow SNAPSHOT versions, so you might want to change the version.
* Windows only: [install WIX](https://wixtoolset.org/)
* Since recently there was a migration to installers and gradle, the projects 
  [andersicht](andersicht/) and [tingeltangel-manager](tingeltangel-manager/) are empty projects
  starting the [tingeltangel](tingeltangel/) project with a different commandline option only.
  Futurewise, the project should be splitted.
* If you need the tingeltangel version with no official books, use [tingeltangel](tingeltangel/) with the commandline option `disable-official-books`




# TODO
 * read YAML files from tttool software
 * extract strings to i18n-files
## LICENSE
```
                    GNU GENERAL PUBLIC LICENSE
                       Version 2, June 1991

Copyright (C) 1989, 1991 Free Software Foundation, Inc., <http://fsf.org/>
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
Everyone is permitted to copy and distribute verbatim copies
of this license document, but changing it is not allowed.
```
