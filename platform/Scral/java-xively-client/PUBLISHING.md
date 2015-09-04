To make using the Xively java client as straightforward as possible it will be
published to Maven central repository, meaning it can just be added as a
dependency to standard Maven, Ivy or Gradle build file.

This guide is intended for developers of the JAR, and aims to outline the steps
needed to publish new versions of the library.

## Sonatype OSS Maven Repository Usage Guide

All information contained in this document is derived from this original
document:

https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide

so if anything doesn't work, then please consult this document for more up to
date information.

## Prerequisites

* JDK 1.7+
* Maven 2.2.1+ (I tested with 3.0.5 but older versions should also work)
* Git

## Sonatype project and account

An account for the Xively dev team has already been created on Sonatype, and
similarly a project has been created to hold our published JARs under the
`com.xively` namespace, so the initial steps outlined in the main Usage Guide
have already been completed.

However you will need to set up a local `~/.m2/settings.xml` file containing
the login details for the Xively Dev team account.

```xml
<settings>
  ...
  <servers>
    <server>
      <id>sonatype-nexus-snapshots</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
    <server>
      <id>sonatype-nexus-staging</id>
      <username>your-jira-id</username>
      <password>your-jira-pwd</password>
    </server>
  </servers>
  ...
</settings>
```

## Create a GPG key

To publish JARs you'll also need to create a GPG key which Maven will use to sign released files. Full details
on this process can be found here: https://docs.sonatype.org/display/Repository/How+To+Generate+PGP+Signatures+With+Maven, but the 
basic steps are:

Make sure GPG is installed:

```bash
$ gpg --version
```

Generate a key pair:

```bash
$ gpg --gen-key
```

Accept all the default options unless you have other specific requirements.

View your key to obtain it's keyid:

```bash
$ gpg --list-keys
/home/username/.gnupg/pubring.gpg
----------------------------
pub   2048R/008287DF 2013-05-21
uid                  User Name <user.name@xively.com>
sub   2048R/40C13F1E 2013-05-21
```
The keyid here would be `008287DF`.

Distribute your public key:

```bash
$ gpg --keyserver hkp://pool.sks-keyservers.net --send-keys 008287DF
```

With this config in place, Maven will now automatically sign all released files
using this key so you should see that you are prompted for your GPG passphrase
when pushing JARs to published repositories.

## Publishing a Snapshot

Snapshot versions published to Sonatype are *not* synchronized to central, so
if users want to use the SNAPSHOT version, they will need to explicitly add the
Sonatype snapshot repository to their build file.

Successfully published SNAPSHOT versions will be found in:
https://oss.sonatype.org/content/repositories/snapshots/com/xively/client/xively-java-client/

To actual publish the snapshot, you should run:

```bash
$ mvn -DperformRelease=true clean deploy
```

For details on what a SNAPSHOT version is, please read POM syntax guide at
Sonatype has some good details:
http://books.sonatype.com/mvnref-book/reference/pom-relationships-sect-pom-syntax.html

As many snapshots as you like can be published, as Sonatype only keeps the most
recent 5. If a non snapshot version is released then all snapshots for that
version are deleted.

## Stage a release

NOTE: not yet tested

First prepare the release:

```bash
$ mvn release:clean
$ mvn release:prepare
```

Then stage the release:

```bash
$ mvn release perform
```

## Promote the release

This is the point at which the JAR will actually be pushed to the Maven central
repository; previously it will have just been in the Sonatype repositories.

There are a number of steps to the release process, so rather than replicating the information in
the Sonatype guide, please follow the steps outlined here:

https://docs.sonatype.org/display/Repository/Sonatype+OSS+Maven+Repository+Usage+Guide#SonatypeOSSMavenRepositoryUsageGuide-8a.ReleaseIt

The first time you promote a release, you need to comment on the OSSRH JIRA
ticket you created initially when creating the project so we can know you are
ready to be synced. We will review your promoted artifacts. If no problem
found, Sonatype will activate Central Sync for you and close your JIRA ticket.

After Central Sync is activated, your future promotion will be synced
automatically. The sync process runs roughly every 2 hours.
