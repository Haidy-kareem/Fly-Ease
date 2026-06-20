#!/bin/bash
# Run script for SkyBook Flight Booking System
cd "$(dirname "$0")"
# Compile
echo "Compiling..."
mkdir -p bin
javac -d bin -sourcepath oopflightbookingsystem/src oopflightbookingsystem/src/*.java
if [ $? -ne 0 ]; then
    echo "Compilation failed!"
    exit 1
fi
echo "Starting SkyBook..."
# Run from the project root so "data/" resolves correctly
java -cp bin oopflightbookingsystem.src.Gui
