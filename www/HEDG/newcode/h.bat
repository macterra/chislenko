REM Clean directory; compile and run Hypereconomy
PATH=C:\TRUMPET;C:\WINDOWS;C:\WINDOWS\COMMAND;c:\jdk1.2beta4\bin;c:\jdk1.2beta4
rem PATH = %PATH%;c:\jdk1.2beta4\bin;c:\jdk1.2beta4
del *.ran
del *.sch
del *.eco
del *.int
del *.hec
del click.tot
javac HyperE.java
java HyperE 2 
type click.tot