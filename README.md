operias
=======

Operias is an open source tool to produce diff reports between your code and a given branch on github. It shows the differences in source file and the difference in code coverage in one simple overview.


Usage
=======

To use operias, clone the master branch and execute the following command:

  mvn clean compile assembly:single

After that, go to the target directory and execute:

  java -cp '<path-to-project>/target/operias-<version>-jar-with-dependencies.jar' operias.Main <revisedDirectory> <originalDirectory>

After execution, the site will be available in the site folder.
