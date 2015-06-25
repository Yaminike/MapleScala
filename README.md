# MapleScala
MapleScala is a MapleStory emulator written in [Scala][1] langauge.

## Packages in use
* MySQL
* [Scalikejdbc][5]
* [Akka IO][6]
* [pbkdf2-scala][7]

## Developers
* [Yaminike][8]

## How to run
The server can be run either with the [Typesafe Activator][2] or with an IDE like [IntelliJ][3].

Please note, as the this project is written in Scala you must first configure your enviroment so it can run Scala applications (see [link][4]).
And when using the Typesafe Activator, it should also be installed

### Typesafe Activator
1. Run the batch version of activator located in the source root, this will start the activator on your localhost and will open a new window in your browser
2. When the activator is running, it should automatically compile the source, please wait untill it finishes
3. To run the server, go to the run tab of the activator and press run.

### IntelliJ
1. Ensure you have all Scala plugins installed.
2. Configure Main.scala to run
3. Press Run

### Configuration
All user configuration can be found in the [application config file](/src/main/resources/application.conf).

Please note, the default database driver is MySQL, if you wish to change it, please also update [build.sbt](/build.sbt) to include the driver you want to use.

### WZ Data
All data from the WZ files is converterted to the [NX (PKG4) Format Specification](http://nxformat.github.io/) and compressed using GZip rather than LZ4, due native support existing for it.
The files should be placed in the resources/XML folder.

A download is available from [here][9] (obviously you should unzip all files into the folder)

### License

> Copyright 2015 Yaminike
>
> Licensed under the Apache License, Version 2.0 (the "License");<br />
> you may not use this file except in compliance with the License.<br />
> You may obtain a copy of the License at
>
> [http://www.apache.org/licenses/LICENSE-2.0](http://www.apache.org/licenses/LICENSE-2.0)
>
> Unless required by applicable law or agreed to in writing, software
> distributed under the License is distributed on an "AS IS" BASIS,
> WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.<br />
> See the License for the specific language governing permissions and
> limitations under the License.

[1]: http://www.scala-lang.org/
[2]: http://www.typesafe.com/community/core-tools/activator-and-sbt
[3]: https://www.jetbrains.com/idea/
[4]: http://www.scala-lang.org/download/install.html
[5]: http://scalikejdbc.org/
[6]: http://akka.io/
[7]: https://github.com/nremond/pbkdf2-scala
[8]: https://github.com/Yaminike/
[9]: https://mega.co.nz/#!VM4iDAZY!pyxlWJJHnygtmB4-Pl4UjNtkcKs5P-LtSm6u8y9ZTGw
