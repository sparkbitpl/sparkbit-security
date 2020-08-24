#!/bin/bash
set -euo pipefail
set -x

. `dirname $0`/setVars.sh

export projectName=sparkbit-security

cd $repoRoot

scripts/bump_version.sh
mvn -s "${NEXUS_SETTINGS_XML}" -P release -DskipTests -Dcheckstyle.skip -Dmaven.antrun.skip clean deploy


git commit -a -m "RELEASE: release $projectName-$RELEASE_VERSION"
git tag -am "$projectName-$RELEASE_VERSION" $projectName-$RELEASE_VERSION
git push origin master

unset currentVersion

scripts/bump_version.sh
git commit -a -m "RELEASE: prepare for next development iteration"
git push origin master
