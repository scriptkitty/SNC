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

First Steps
===

After starting the calculator, you have two possible ways to input a network:
* By using the GUI Buttons "Add Node" and "Add Flow"
* By loading a network from file (File -> Load)

When adding nodes and flows by hand, you are prompted to fill in the respective parameters, e.g., service and arrival, or the route of a flow. Every flow has a priority at each traversing node, which is a positive integer. The flows are served in decreasing order of their priority, i.e., the flow with the highest priority is served first.

Alternatively, networks can be entered more conveniently through a textfile format. The following example shows a simple network with three nodes with names "v1", "v2" and "v3", constant rate services and FIFO scheduling. Moreover, there is one flow traversing these nodes with exponential arrivals at the first node of the route, namely v1.
```
# Configuration of Network
# Interface configuration. Unit: Mbps
I v1, FIFO, CR, 1
I v2, FIFO, CR, 3
I v3, FIFO, CR, 4

EOI
# Traffic configuration. Unit Mbps or Mb
# One flow with the route v1->v2->v3 with respective priorities 1, 1, 2
F F1, 3, v1:1, v2:1, v3:2, EXPONENTIAL, 2
EOF
```
For detailled information on the file format, please refer to Section 6 of our [technical report](https://arxiv.org/abs/1707.07739).

The current network is depicted visually in the right panel, whereas the left panel shows a tabular representation. The console panel on the bottom outputs debug and error information as well as computed bounds.
Since the calculator works analytically, performance bounds can either be derived analytically or for a specific choice of parameters. To this end, the button "Analyze Network" outputs an analytic result of the selected performance bound, whereas "Optimize Bound" takes concrete parameters and numerically optimizes the selected bound with respect to theta and potential Hölder parameters.
A bound, e.g. backlog, is of the form `P[q > x] <= p` which takes `x` and outputs the probability `p`. In contrast, an inverse bound takes the maximum violation probability `p`and computes the respective `x` (in the case of backlog)
