#!/usr/bin/env bash
set -euo pipefail

mvn -B -ntp -Pcentral-release deploy \
  -DskipTests \
  -Dcentral.skipPublishing=true \
  -Dcentral.autoPublish=false \
  -Dcentral.waitUntil=validated

echo "Central dry-run bundle should be under target/central-publishing or module target directories."
