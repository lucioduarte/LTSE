#! /bin/bash

echo Starting instrumentation...
stty -echo

mkdir temp

stty echo

echo Inserting annotations in CallStack.java

stty -echo

cp CallStack.java CallStack_original.java

txl -i "$INSTR_PATH/rules" -in 2 -o CallStack.java CallStack_original.java $INSTR_PATH/rules/JavaRules.txl

mv CallStack_original.java temp

stty echo

echo CallStack.java succesfully instrumented!

