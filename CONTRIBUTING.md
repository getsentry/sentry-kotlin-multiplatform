# Contributing

This is still an experimental SDK so we're more than happy to discuss feedback and receive pull requests!

# Code style
The project uses `ktlint` and `spotless` to make sure the code is formatted properly. 
We recommend that you setup a `pre-commit` hook that runs a spotless check when you try to commit.

# Git commit hook

Navigate to the root folder and install the hook:

```shell
git config core.hooksPath .hooks/
```

To run the build and tests:

```shell
make compile
```

# CI

Build and tests are automatically run against branches and pull requests
via GH Actions.