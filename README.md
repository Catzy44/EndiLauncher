Written from scratch launcher for my mc server.
Java + Swing.

## Building

The project uses Gradle with Java 25. The Microsoft login callback uses
`me.catzy:JavaMicroHTTPServer:1.2.0` from Maven Local. First publish the library
from `~/projects/JavaMicroHTTPServer` with `./gradlew.bat publishToMavenLocal`.
Then, from the launcher project directory, run:

```powershell
.\gradlew.bat build
```

The local HTTP callback can be checked without a Microsoft account after building:

```powershell
& "$env:JAVA_HOME/bin/java.exe" -cp build/libs/EndiLauncher-1.0.3-all.jar tests/MicrosoftCallbackSmoke.java
```

This check needs port 22420 to be free and takes about 11 seconds.

The launcher entry point is `me.catzy44.EndiLauncher`; run it with `./gradlew.bat run`.

Local configuration lives in `src/me/catzy44/Constants.java`, which is excluded
from Git and is never generated or overwritten by Gradle. Set `CONST` for the
Endi API and `MICROSOFT_CLIENT_ID` / `MICROSOFT_SECRET` for Microsoft login,
then rebuild. On a fresh checkout, create this class in package `me.catzy44`
with those three `public static final String` fields.

It's very very very complicated because i didnt want to mess with Mojang EULA.
Launcher is completing all the game files and all the mods on the client's computer.
It's also capable od updating itself, updating gamefiles, etc.

Written in 2021, stopped working on it in 2022 as my server has been closed because of lack of my time.

<img src="https://media.licdn.com/dms/image/v2/D4D2DAQGIUnt-afLNyA/profile-treasury-image-shrink_800_800/profile-treasury-image-shrink_800_800/0/1738640389158?e=1748944800&v=beta&t=XcQStWXJ8blswE3jWoANTXIQPeg50rAPXsXvH1xsGMA"/>
<img src="https://media.licdn.com/dms/image/v2/D4D2DAQG6A36jlpAh7g/profile-treasury-image-shrink_800_800/profile-treasury-image-shrink_800_800/0/1738640486829?e=1748944800&v=beta&t=Klb_G9pxWCb9WgxsuVl03F72De-j5OU6dbjqc8gxkuA"/>
<img src="https://media.licdn.com/dms/image/v2/D4D2DAQGbxDkzhSphuA/profile-treasury-image-shrink_800_800/profile-treasury-image-shrink_800_800/0/1738640414944?e=1748944800&v=beta&t=eDHk8JjeNXi9CrOXM9mq-zupyX4tZ_iECcVz63_TVag"/>
<img src="https://media.licdn.com/dms/image/v2/D4E2DAQHilPoyBi5QMw/profile-treasury-image-shrink_800_800/profile-treasury-image-shrink_800_800/0/1738666249793?e=1748944800&v=beta&t=DjyARYLV_QTgNoeZmpyiyZxGyDvLK5kEN4RRARWFKyI"/>
