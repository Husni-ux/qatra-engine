# QATRA Release Process

## Release types

QATRA currently uses early alpha releases:

```text
0.1.0-alpha
0.2.0-alpha
0.3.0-alpha
```

Development versions should keep the `-SNAPSHOT` suffix:

```text
0.1.0-alpha-SNAPSHOT
```

Release versions must not contain `SNAPSHOT`.

## Recommended first release flow

1. Finish the planned feature set.
2. Run the full local CI script.
3. Update `CHANGELOG.md`.
4. Run release preparation locally.
5. Build a Central dry-run bundle.
6. Review generated source, Javadoc, POM, and signature files.
7. Run the GitHub Actions `Maven Central Release` workflow with `dry_run=true`.
8. If successful, run it again with `dry_run=false` and `auto_publish=false`.
9. Review the deployment manually in Central Portal.
10. Publish from the portal.
11. Create a GitHub release and tag.

## GitHub release title

```text
QATRA Engine v0.1.0-alpha
```

## Suggested release notes

```markdown
# QATRA Engine v0.1.0-alpha

First alpha release of QATRA Engine.

## Included modules

- qatra-core
- qatra-web
- qatra-api

## Highlights

- Fluent Selenium Web API
- Smart waits
- Advanced web assertions
- Arabic/RTL scanner
- RTL baseline and quality gate
- API testing starter module
- Page Object Model support
- Data-driven testing support
- Environment profiles
- CI/CD workflows

## Known limitations

- Mobile testing module is not included yet.
- Database testing module is not included yet.
- API module is still early.
- Maven Central release should be considered alpha quality.
```

## Post-release version bump

After publishing `0.1.0-alpha`, move the project to the next snapshot:

```bash
mvn -B -ntp org.codehaus.mojo:versions-maven-plugin:2.17.1:set \
  -DnewVersion=0.2.0-alpha-SNAPSHOT \
  -DgenerateBackupPoms=false
```
