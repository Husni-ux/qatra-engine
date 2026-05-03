#!/usr/bin/env bash
set -euo pipefail

VERSION="${1:-0.1.0-alpha}"

if [[ "$VERSION" == *SNAPSHOT* ]]; then
  echo "Release version must not contain SNAPSHOT: $VERSION" >&2
  exit 1
fi

echo "Preparing QATRA Engine release version: $VERSION"

mvn -B -ntp org.codehaus.mojo:versions-maven-plugin:2.17.1:set \
  -DnewVersion="$VERSION" \
  -DgenerateBackupPoms=false

mvn -B -ntp clean verify \
  -Dqatra.env=local \
  -Dqatra.browser=chrome \
  -Dqatra.headless=true \
  -Dsurefire.failIfNoSpecifiedTests=false

echo "Release preparation completed for $VERSION"
echo "Next dry-run bundle command:"
echo "mvn -B -ntp -Pcentral-release deploy -DskipTests -Dcentral.skipPublishing=true"
