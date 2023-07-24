## Contributing

Please first do all the standard things you would need to do to PR to a repository on GitHub (Forking, Cloning,
creating a new branch, adding a remote, etc.).

Then to set up a development environment, just make sure you have a Java 17 and and Java 8 version installed. To
run the mod in a development environment, just run `./gradlew runClient` (Make sure to run the gradle task directly
and not an IntelliJ task with a similar name. Otherwise you might need more configuration. Running that very gradle task
through IntelliJ is fine, however.). Feel free to make changes to the test mod to test your changes.

When creating a PR, try to have commits which are as atomic as possible.
This means that when possible, commits should always:

 - result in a working state, even without commits that are done further down the line
 - result in a state without any incomplete features. Features that are only used in a later commit are fine, but you
   can also merge those if you wish.
 - should not be able to be split apart without violating another rule.

These rules are not set in stone, so if you think a commit feels right, just do it. Just remember that your PR will not
be squashed, so make sure that every commit can land on master

## Creating a release

Github actions automatically generates a new release on our maven for every tag pushed to GitHub, so to create a
release, just run:

```bash
git tag "<newVersion>"
git push origin "<newVersion>"
```

To check which new version to use just check the old version number at [the maven] and go from there. We use semver
to find the next version, so:

 - For ABI neutral changes, bump the patch version (e.g. only touched internal classes or change of method bodies).
 - For backwards compatible changes, bump the minor version.
 - For breaking changes, bump the major version.

Don't be scared of labeling a breaking change a breaking change, even if it is only a small one. Since people typically
vendor our library and do not need to be up to date all the time.

[the maven]: https://maven.notenoughupdates.org/#/releases/org/notenoughupdates/moulconfig/MoulConfig