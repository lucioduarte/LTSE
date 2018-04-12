#! /bin/bash

echo Restoring CallStack.java
stty -echo

cp temp/CallStack_original.java CallStack.java

stty echo

echo CallStack.java succesfully restored!

cd temp
rm *
cd ..
rmdir temp