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

    ##### Tingeltangel CLI
    ```
    gradlew tingeltangel-cli:run
    ```
    ##### Tingeltangel GUI Editor
    ```
    gradlew tingeltangel-gui:run
    ```
    ##### Tingeltangel Andersicht
    ```
    gradlew tingeltangel-andersicht:run
    ```
    ##### Tingeltangel Manager
    ```
    gradlew tingeltangel-manager:run
    ```

    for just building all of them together execute:
    
    ```
    gradlew build
    ```

4. If you want to create your own installer(s) run:
    ```
    gradlew jpackage
    ```
    or if you just need a specific one:
    ```
    gradlew <project-name>:jpackage
    ```
    where project name is as in step 3. It's either 
    * `tingeltangel-gui`
    * `tingeltangel-cli`
    * `tingeltangel-manager`
    * `tingeltangel-andersicht`

## Notes: 
* jpackage doesn't allow SNAPSHOT versions in Windows (It's more a WiX Problem), so you might want to change the version.
* Windows only: [install WiX](https://wixtoolset.org/)
* Since recently there was a migration to installers and gradle, the projects 
  [andersicht](andersicht/) and [tingeltangel-manager](tingeltangel-manager/) are empty projects
  starting the [tingeltangel](tingeltangel/) project with a different commandline option only.
  Futurewise, the project should be splitted.
* If you need the tingeltangel version with no official books, use [tingeltangel](tingeltangel/) with the commandline option `disable-official-books`

# TODO
* read YAML files from tttool software
* extract strings to i18n-files
* writing all kinds of things (logs, caches etc.) into the user directory not in the installation directory. This 
causes ATM, that we cannot install the Application (at least on windows) in the `Program Files` directory.
* The mac installation dmg cannot be executed once installed. I am on this, but it doesn't work for now.
a workaround is executing it manually in the commandline. Here an example:
    ```
    /Applications/tingeltangel-gui.app/Contents/MacOS/tingeltangel-gui
    ```
* Find a solution to not update the version number in the [Main-File](core/src/main/java/tingeltangel/Tingeltangel.java) manually, but using the one from the `gradle.properties` file
## LICENSE
```
                    GNU GENERAL PUBLIC LICENSE
                       Version 2, June 1991

Copyright (C) 1989, 1991 Free Software Foundation, Inc., <http://fsf.org/>
51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA
Everyone is permitted to copy and distribute verbatim copies
of this license document, but changing it is not allowed.
```
