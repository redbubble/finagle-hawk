# HAWK Support for Finagle/Finch

[HTTP Holder-Of-Key Authentication Scheme](https://github.com/hueniverse/hawk) (Hawk) support for [Finagle](https://github.com/finagle/finagle).

Note that it currently doesn't support [Response Payload Validation](https://github.com/hueniverse/hawk#response-payload-validation).

Almost all of this library is Finagle agnostic, only `HawkAuthenticateRequestFilter` is tied to Finagle.

If you like this, you might like other open source code from Redbubble:

* [rb-scala-utils](https://github.com/redbubble/rb-scala-utils) - Miscellaneous utilities (common code) for building
  Scala-based services, using Finch (on which this project depends).
* [finch-template](https://github.com/redbubble/finch-template) - A template project for Finch-based services.
* [rb-graphql-template](https://github.com/redbubble/rb-graphql-template) - A template for Scala HTTP GraphQL services.
* [finch-sangria](https://github.com/redbubble/finch-sangria) - A simple wrapper for using Sangria from within Finch;

# Setup

You will need to add something like the following to your `build.sbt`:

```scala
resolvers += Resolver.jcenterRepo

libraryDependencies += "com.redbubble" %% "finagle-hawk" % "0.1.0"
```

# Usage

```scala
val creds = Credentials(KeyId("Key ID"), Key("8e2dd2949b0e30c544336f73f94e2df3"), Sha256)

object AuthenticationFilter extends HawkAuthenticateRequestFilter(creds)

val authenticatedService = AuthenticationFilter andThen service
```

# Release

For contributors, a cheat sheet to making a new release:

```shell
$ git commit -m "New things" && git push
$ git tag -a v0.0.3 -m "v0.0.3"
$ git push --tags
$ ./sbt publish
```

# Contributing

Issues and pull requests are welcome. Code contributions should be aligned with the above scope to be included, and include unit tests.

