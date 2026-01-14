#!/bin/bash
# Script to verify BattleArena JAR file on the server

JAR_FILE="plugins/BattleArena-5.0.1.jar"

echo "=== BattleArena JAR Verification ==="
echo ""

# Check if file exists
if [ ! -e "$JAR_FILE" ]; then
    echo "ERROR: $JAR_FILE does not exist!"
    exit 1
fi

# Check if it's a file or directory
if [ -d "$JAR_FILE" ]; then
    echo "ERROR: $JAR_FILE is a DIRECTORY, not a file!"
    echo "This is the problem! Delete the directory and copy the JAR file."
    exit 1
fi

if [ ! -f "$JAR_FILE" ]; then
    echo "ERROR: $JAR_FILE is not a regular file (might be a symlink or special file)"
    exit 1
fi

echo "✓ File exists and is a regular file"
echo ""

# Check file type
echo "File type:"
file "$JAR_FILE"
echo ""

# Check file size
echo "File size:"
ls -lh "$JAR_FILE"
echo ""

# Check if it's a valid ZIP/JAR
echo "Testing ZIP integrity:"
unzip -t "$JAR_FILE" 2>&1 | tail -3
echo ""

# Check for paper-plugin.yml
echo "Checking for paper-plugin.yml:"
if unzip -l "$JAR_FILE" | grep -q "paper-plugin.yml"; then
    echo "✓ paper-plugin.yml found in JAR"
    echo ""
    echo "Contents of paper-plugin.yml:"
    unzip -p "$JAR_FILE" paper-plugin.yml
else
    echo "✗ paper-plugin.yml NOT FOUND in JAR!"
fi
echo ""

# Check for plugin.yml
echo "Checking for plugin.yml:"
if unzip -l "$JAR_FILE" | grep -q "^.*plugin\.yml$"; then
    echo "✓ plugin.yml found in JAR"
else
    echo "✗ plugin.yml NOT FOUND in JAR!"
fi
echo ""

# Check file permissions
echo "File permissions:"
ls -la "$JAR_FILE"
echo ""

echo "=== Verification Complete ==="
