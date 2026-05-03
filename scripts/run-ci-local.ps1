$ErrorActionPreference = "Stop"

if (-not $env:QATRA_ENV) { $env:QATRA_ENV = "local" }
if (-not $env:QATRA_BROWSER) { $env:QATRA_BROWSER = "chrome" }
if (-not $env:QATRA_HEADLESS) { $env:QATRA_HEADLESS = "true" }

mvn -B -ntp validate
mvn -B -ntp -pl qatra-api -am clean test -Dqatra.env=$env:QATRA_ENV -Dsurefire.failIfNoSpecifiedTests=false
mvn -B -ntp -pl qatra-web -am clean test `
  -Dtest=SampleQatraTest `
  -Dqatra.env=$env:QATRA_ENV `
  -Dqatra.browser=$env:QATRA_BROWSER `
  -Dqatra.headless=$env:QATRA_HEADLESS `
  -Dsurefire.failIfNoSpecifiedTests=false
mvn -B -ntp package -DskipTests
