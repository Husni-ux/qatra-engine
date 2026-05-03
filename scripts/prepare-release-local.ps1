param(
    [string]$Version = "0.1.0-alpha"
)

$ErrorActionPreference = "Stop"

if ($Version -like "*SNAPSHOT*") {
    throw "Release version must not contain SNAPSHOT: $Version"
}

Write-Host "Preparing QATRA Engine release version: $Version"

mvn -B -ntp org.codehaus.mojo:versions-maven-plugin:2.17.1:set `
    -DnewVersion=$Version `
    -DgenerateBackupPoms=false

mvn -B -ntp clean verify `
    -Dqatra.env=local `
    -Dqatra.browser=chrome `
    -Dqatra.headless=true `
    -Dsurefire.failIfNoSpecifiedTests=false

Write-Host "Release preparation completed for $Version"
Write-Host "Next dry-run bundle command:"
Write-Host "mvn -B -ntp -Pcentral-release deploy -DskipTests -Dcentral.skipPublishing=true"
