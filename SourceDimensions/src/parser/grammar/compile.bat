@ECHO OFF
cd Java
copy *.g ..
cd ..\CSharp
copy *.g ..
cd ..
Jikespg %1
del *.g