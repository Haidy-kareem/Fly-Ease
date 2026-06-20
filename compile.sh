#!/bin/bash
cd "$(dirname "$0")"
mkdir -p bin
javac -d bin oopflightbookingsystem/src/*.java
if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
else
    echo "❌ Compilation failed."
fi
