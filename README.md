operias
=======

Operias is an open source tool to produce diff reports between two version of a software project. For now, it will only use on maven projects. In the latest version you can also use it to compare you code to a random commit from git.

Demo
=======
For a short demo on how this tool works please watch the video at https://youtu.be/2Dpigi5ghZE

Usage
=======

To use operias, clone the master branch and execute the following command:
```
  mvn clean compile assembly:single
```
After that, go to the target directory and execute:
```ini
  java -cp '<path-to-project>/target/operias-<version>-jar-with-dependencies.jar' operias.Main <args>
```

Parameter | Short parameter name | Description | 
----------|----------|-----------|
--destination-directory | -d | The directory where the generated site will be placed
--revised-directory | -rd | This directory contains the revised source code, this directory will be compared to the original directory. This should be the project directory containing the main pom.xml.
--original-directory | -od | This directory contains the original source code, unchanged. This should be the project directory containing the main pom.xml.
--repository-url | -ru | Instead of providing a directories containing the versions, you can also give a git url. Operias will clone the repository into it's own temporary repository directory.
--original-repository-url | -oru | The git url used for the original version of the source code.
--original-commit-id | -oc | Operias will checkout to this commit to use as original source code. 
--original-branch-name | -obn | Operias will checkout to this branch to use as original source code. 
--revised-repository-url | -oru | The git url used for the revised version of the source code.
--revised-commit-id | -rc | Operias will checkout to this commit to use as revised source code.
--revised-branch-name | -rbn | Operias will checkout to this branch to use as revised source code.
--temp-directory | -td | If Operias fails to execute, it is possible it does not have the rights to create a temporary folder, use this parameter to set your own temporary directory for Operias. Be aware, it will delete all the contents of the temporary folder when the execution has completed
--verbose | -v | Provide this parameter enable the output of errors, warnings and info messages


