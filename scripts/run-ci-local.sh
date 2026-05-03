#!/usr/bin/env bash
set -euo pipefail

export QATRA_ENV="${QATRA_ENV:-local}"
export QATRA_BROWSER="${QATRA_BROWSER:-chrome}"
export QATRA_HEADLESS="${QATRA_HEADLESS:-true}"

mvn -B -ntp validate
mvn -B -ntp -pl qatra-api -am clean test -Dqatra.env="$QATRA_ENV" -Dsurefire.failIfNoSpecifiedTests=false
mvn -B -ntp -pl qatra-web -am clean test \
  -Dtest=SampleQatraTest \
  -Dqatra.env="$QATRA_ENV" \
  -Dqatra.browser="$QATRA_BROWSER" \
  -Dqatra.headless="$QATRA_HEADLESS" \
  -Dsurefire.failIfNoSpecifiedTests=false
mvn -B -ntp package -DskipTests
