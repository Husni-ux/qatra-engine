# Namespace Decision

QATRA currently uses:

```xml
<groupId>io.github.qatra</groupId>
```

This is clean and professional if the project owns or verifies the GitHub organization/account/namespace `qatra`.

If the first release will be published under the maintainer's GitHub account, use a Central Portal verified namespace such as:

```xml
<groupId>io.github.husni-ux</groupId>
```

or another approved namespace.

Changing the groupId later is possible but not ideal because users will depend on the original Maven coordinates. Decide before the first public release.

Recommended decision before publishing:

```text
Option A: Create/verify GitHub organization: qatra
GroupId: io.github.qatra

Option B: Publish under personal GitHub namespace
GroupId: io.github.husni-ux
```
