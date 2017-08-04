Stochastic Network Calculator (SNC)
===

The Stochastic Network Calculator is a Java tool for the stochastic worst case performance analysis of networks, see http://disco.informatik.uni-kl.de/index.php/projects/disco-snc for a detailed description and historic versions.
The newest release is version 2.0, which can be found under the according [tag](https://github.com/scriptkitty/SNC/releases/tag/v2.0).
For more information on the underlying theory and the innerworkings, please refer to our [technical report](https://arxiv.org/abs/1707.07739).

Note that this software is still under development, so feel free to contact us, if you encounter any bugs.
All external libraries, such as the Apache Commons Math Library and the Jung Java Graph Framework, are in the folder externalLibs/

We appreciate any contribution, so feel free to fork the code, post a pull request or contact us directly.

Getting Started
===

For running the DISCO SNC you need a Java 8 JRE, both official and openjdk should be fine.
There are multiple options to get started with the calculator:

* Download the [JAR] with all necessary libraries (https://github.com/scriptkitty/SNC/blob/master/snc.jar)
* Download the source code of the latest [release](https://github.com/scriptkitty/SNC/releases)
* Clone the newest commit of the master branch, if you are feeling adventurous

In order to execute the .jar file, just open a command line window and enter
```bash
java -jar snc.jar
```
If you choose to work with the source code, note that you have to add the external libraries to avoid compilation errors.
When using an IDE such as Netbeans/Eclipse this can be done easily:

1. Download and extract the latest [release](https://github.com/scriptkitty/SNC/releases)
2. Create a new project from existing sources, for Eclipse: New Project -> Uncheck "Use default location" and navigate to the extracted sources -> Finish the wizzard. See [this](https://netbeans.org/kb/73/java/project-setup.html?print=yes#existing-java-sources) guide for Netbeans.
3. Add the external libraries to the project path, for Eclipse: Right-click on the project name in the package explorer -> Properties -> Java Build Path -> Libraries -> Add JAR -> Then select all jars in externalLibs/. For Netbeans: Right-click on the project name in the Projects Panel -> Properties -> Libraries -> Add JAR and select all jars in externalLibs/.

Alternatively, instead of creating a new project in step 2, both IDEs can cope with copying the downloaded source files into the source directory of an existing project.
