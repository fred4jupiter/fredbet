# release section
git tag -l | xargs git tag -d
git config user.name "fred4jupiter"
git config user.email "hamsterhase@gmx.de"
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} versions:commit
mvn build-helper:parse-version scm:tag -Dbasedir=. -Dtag=release_\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.incrementalVersion} -Dusername=${GITHUB_USERNAME} -Dpassword=${GITHUB_PASSWORD}
mvn clean package -DskipTests
PROJECT_REL_VERSION=$(mvn -q -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
git pull https://${GITHUB_USERNAME}:${GITHUB_PASSWORD}@github.com/fred4jupiter/fredbet.git master
mvn build-helper:parse-version versions:set -DnewVersion=\${parsedVersion.majorVersion}.\${parsedVersion.minorVersion}.\${parsedVersion.nextIncrementalVersion}-SNAPSHOT versions:commit
NEXT_DEV_VERSION=$(mvn -q -Dexec.executable='echo' -Dexec.args='${project.version}' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.3.1:exec)
git pull https://${GITHUB_USERNAME}:${GITHUB_PASSWORD}@github.com/fred4jupiter/fredbet.git master
git commit -a -m "set next dev version to ${NEXT_DEV_VERSION}"
git push https://${GITHUB_USERNAME}:${GITHUB_PASSWORD}@github.com/fred4jupiter/fredbet.git master
echo "release version is: ${PROJECT_REL_VERSION}"
echo "next development version is: ${NEXT_DEV_VERSION}"
export PROJECT_REL_VERSION

# Docker Hub publish section
docker login -e $DOCKER_EMAIL -u $DOCKER_USERNAME -p $DOCKER_PASSWORD
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:$PROJECT_REL_VERSION
docker push hamsterhase/fredbet:$PROJECT_REL_VERSION
docker tag -f fred4jupiter/fredbet hamsterhase/fredbet:latest
docker push hamsterhase/fredbet:latest