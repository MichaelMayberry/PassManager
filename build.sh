#!/bin/bash
set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
cd "$SCRIPT_DIR"

echo "Building..."
./mvnw clean package -DskipTests -q

echo "Packaging DMG..."
rm -rf target/pkg-input target/dist
mkdir target/pkg-input
cp target/PassManager.jar target/pkg-input/

jpackage \
  --input target/pkg-input \
  --main-jar PassManager.jar \
  --main-class com.example.passmanager.Launcher \
  --name PassManager \
  --app-version 1.0 \
  --type dmg \
  --dest target/dist

echo "Done: target/dist/PassManager-1.0.dmg"
