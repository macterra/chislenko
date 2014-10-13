REM clean directory, build and run access generator and some models for hypereconomy project
PATH=C:\TRUMPET;C:\WINDOWS;C:\WINDOWS\COMMAND;c:\jdk1.2beta3\bin;c:\jdk1.2beta3
del *.class
del access2.*
del *.ran
del *.sch
del *.eco
del *.int
del *.hec
del *.dat
del click.tot
REM Compile:
javac Generator.java
javac HyperE.java
REM Generate access file:
java Generator
REM running models by one to preserve disk space:
java HyperE 2
del access2.*
java HyperE 4
del access2.*
java HyperE 3
del access2.*
java HyperE 0
del access2.*
java HyperE 1
del access2.*