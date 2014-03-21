operias

tetsts
=======

Operias is an open source tool to produce diff reports between two version of a software project. For now, it will only use on maven projects. In the latest version you can also use it to compare you code to a random commit from git.


Usage
=======

To use operias, clone the master branch and execute the following command:
```
  mvn clean compile assembly:single
```
After that, go to the target directory and execute:
```ini
  java -cp '<path-to-project>/target/operias-<version>-jar-with-dependencies.jar' operias.Main [--destination-directory dir] [--revised-directory dir] [--original-directory dir] [--repository-directory dir] [--repository-url url] [--original-commit-id commit-id] [--revised-commit-id commit-id] [--temp-directory dir] [--verbose]
```

Parameter | Short parameter name | Description | 
----------|----------|-----------|
--destination-directory | -d | The directory where the generated site will be placed
--revised-directory | -rd | This directory contains the revised source code, this directory will be compared to the original directory
--original-directory | -od | This directory contains the original source code, unchanged.
--repository-directory | -rpd | Instead of comparing two directories, it is also possible to compare with a specific commit from git. If this parameter is given, Operias will use this as repository to checkout a commit.
--repository-url | -ru | Instead of providing a directory containing the repository, you can also give a git url. Operias will clone the repository into it's own temporary repository directory
--original-commit-id | -oc | Operias will checkout to this commit to use as original source code. If no repository directory or url is given, operias will try to use the revised directory as repository
--revised-commit-id | -rc | Operias will checkout to this commit to use as revised source code, this time if no repository directory or url is given, it wil try to use the original directory.
--temp-directory | -td | If Operias fails to execute, it is possible it does not have the rights to create a temporary folder, use this parameter to set your own temporary directory for Operias. Be aware, it will delete all the contents of the temporary folder when the execution has completed
--verbose | -v | Provide this parameter enable the output of errors, warnings and info messages


