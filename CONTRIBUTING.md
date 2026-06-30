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

# AI Use

You are welcome to use whatever tools you prefer for making a contribution. However, any changes you propose have to be reviewed and tested by you, a human, first, before you submit a pull request with them for the Sentry team to review. If we feel like that did not happen, we will close the PR outright. For example, we will not review visibly AI-generated PRs from an agent instructed to look for and "fix" open issues in the repo. This aligns with our SDK principle: [every line has an owner](https://develop.sentry.dev/sdk/getting-started/principles/#every-line-has-an-owner).
