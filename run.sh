#!/bin/bash
# ==============================================================================
# Manipal University Jaipur - Department of Computer Science & Engineering
# CSC2104: Object Oriented Programming Mini-Project
# Library Management System - Build & Run Script for macOS
# ==============================================================================

# Ensure script runs from project root directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || exit 1

echo "======================================================================"
echo "    Manipal University Jaipur - CSC2104 OOP Mini Project"
echo "                Library Management System"
echo "======================================================================"

# Verify JDK installation
if ! command -v javac &> /dev/null || ! command -v java &> /dev/null; then
    echo "[Error] Java Development Kit (JDK) is not installed or not in PATH."
    echo "Please install JDK 11 or higher on your Mac."
    exit 1
fi

JAVA_VER=$(java -version 2>&1 | head -n 1)
echo "[System] Detected $JAVA_VER"

# Create output directories if needed
mkdir -p bin data

# Compile all source files
echo "[Build] Compiling Java source files..."
javac -d bin src/*.java
BUILD_STATUS=$?

if [ $BUILD_STATUS -ne 0 ]; then
    echo "[Error] Compilation failed! Please check syntax errors above."
    exit $BUILD_STATUS
fi

echo "[Build] Compilation successful! Classes placed in 'bin/'."
echo ""

# Execution mode selection
if [ "$1" == "--test" ] || [ "$1" == "-t" ]; then
    echo "[Run] Running automated 16-module verification test suite..."
    java -cp bin Main --test
elif [ "$1" == "--console" ] || [ "$1" == "-c" ]; then
    echo "[Run] Launching Interactive Console Mode (Scanner / do-while)..."
    java -cp bin Main --console
else
    echo "[Run] Launching Java Swing Desktop GUI Application..."
    java -cp bin Main "$@"
fi
