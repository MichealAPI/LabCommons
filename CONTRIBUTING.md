# Contributing to LabCommons

First off, thank you for considering contributing to LabCommons! We're excited to have you join our community. Your help is essential for keeping it great.

This document provides guidelines for contributing to LabCommons. Please read it carefully to ensure a smooth and effective contribution process.

## Table of Contents

- [Ways to Contribute](#ways-to-contribute)
- [Reporting Bugs](#reporting-bugs)
- [Suggesting Enhancements](#suggesting-enhancements)
- [Your First Code Contribution](#your-first-code-contribution)
    - [Setting Up Your Development Environment](#setting-up-your-development-environment)
    - [Making Changes](#making-changes)
- [Coding Style Guidelines](#coding-style-guidelines)
- [Commit Message Guidelines](#commit-message-guidelines)
- [Pull Request Process](#pull-request-process)
- [Code of Conduct](#code-of-conduct)
- [License](#license)
- [Questions?](#questions)

## Ways to Contribute

There are many ways to contribute to LabCommons, and not all of them involve writing code:

*   **Reporting Bugs:** If you find a bug, please report it! (See [Reporting Bugs](#reporting-bugs))
*   **Suggesting Enhancements:** Have an idea for a new feature or an improvement to an existing one? Let us know! (See [Suggesting Enhancements](#suggesting-enhancements))
*   **Writing Documentation:** Good documentation is crucial. If you find areas that are unclear or could be improved, please help us.
*   **Reviewing Code:** Offer your expertise by reviewing Pull Requests from other contributors.
*   **Answering Questions:** Help others by answering questions in [GitHub Discussions](https://github.com/MichealAPI/LabCommons/discussions) or on issues.
*   **Writing Code:** Contribute new features, fix bugs, or refactor existing code.

## Reporting Bugs

If you encounter a bug, please help us by submitting an issue to our [GitHub Issues page](https://github.com/MichealAPI/LabCommons/issues).

Before submitting a bug report, please:

1.  **Search existing issues:** Check if the bug has already been reported.
2.  **Ensure you're on the latest version:** The bug may have been fixed in a recent update.

When submitting a bug report, please include as much detail as possible:

*   A clear and descriptive title.
*   Steps to reproduce the bug.
*   What you expected to happen.
*   What actually happened (including any error messages and stack traces).
*   Your environment (e.g., Minecraft version, Spigot/Velocity version, LabCommons version, Java version).

## Suggesting Enhancements

We welcome suggestions for new features and improvements! You can submit your ideas by:

*   Opening an issue on our [GitHub Issues page](https://github.com/MichealAPI/LabCommons/issues) with the label "enhancement".
*   Starting a discussion on our [GitHub Discussions page](https://github.com/MichealAPI/LabCommons/discussions).

Please provide a clear explanation of the enhancement, why it would be beneficial, and any potential use cases.

## Your First Code Contribution

Ready to dive into the code? Here’s how to get started:

### Setting Up Your Development Environment

1.  **Prerequisites:**
    *   Git
    *   Java Development Kit (JDK) 8 or higher (as specified in the main README)
    *   Apache Maven

2.  **Fork the Repository:**
    Click the "Fork" button at the top right of the [LabCommons repository page](https://github.com/MichealAPI/LabCommons) to create your own copy.

3.  **Clone Your Fork:**
    Clone your forked repository to your local machine:
    ```sh
    git clone https://github.com/YOUR_USERNAME/LabCommons.git
    cd LabCommons
    ```
    Replace `YOUR_USERNAME` with your GitHub username.

4.  **Add an Upstream Remote (Optional but Recommended):**
    This helps you keep your fork synced with the main repository.
    ```sh
    git remote add upstream https://github.com/MichealAPI/LabCommons.git
    ```

5.  **Build the Project:**
    Ensure everything is set up correctly by building the project:
    ```sh
    mvn clean install
    ```

6.  **Set up your IDE:**
    Import the project into your preferred IDE (e.g., IntelliJ IDEA, Eclipse). Most IDEs will automatically recognize it as a Maven project.

### Making Changes

1.  **Keep Your Fork Updated:**
    Before starting any work, ensure your `main` (or `master`) branch is up-to-date with the upstream repository:
    ```sh
    git checkout main
    git pull upstream main  # Or master, depending on the default branch name
    git push origin main    # Update your fork's main branch
    ```

2.  **Create a New Branch:**
    Create a new branch for your changes. Use a descriptive name (e.g., `feat/add-new-utility`, `fix/gui-rendering-bug`).
    ```sh
    git checkout -b your-branch-name
    ```

3.  **Write Your Code:**
    Make your changes, add new features, or fix bugs.

4.  **Write Tests:**
    *   If you're adding a new feature, please include unit tests that cover its functionality.
    *   If you're fixing a bug, add a test that reproduces the bug and verifies the fix.
    *   Run tests using:
        ```sh
        mvn test
        ```
    Ensure all tests pass before committing.

5.  **Commit Your Changes:**
    Follow the [Commit Message Guidelines](#commit-message-guidelines).
    ```sh
    git add .
    git commit -m "Your descriptive commit message"
    ```

6.  **Push to Your Fork:**
    Push your changes to your forked repository:
    ```sh
    git push origin your-branch-name
    ```

7.  **Open a Pull Request:**
    Go to the LabCommons repository on GitHub and open a Pull Request from your branch to the main LabCommons `main` (or `master`) branch. (See [Pull Request Process](#pull-request-process))

## Coding Style Guidelines

While we don't enforce a strict, tool-based style guide at the moment, please adhere to the following general guidelines:

*   **Consistency:** Try to match the coding style of the existing codebase.
*   **Readability:** Write clear, understandable code. Use meaningful variable and method names.
*   **Comments:** Add comments to explain complex logic, non-obvious decisions, or important algorithms. Javadoc is encouraged for public APIs.
*   **Java Conventions:** Follow standard Java naming conventions (e.g., `camelCase` for methods and variables, `PascalCase` for classes).
*   **Avoid Unnecessary Complexity:** Keep your code as simple and straightforward as possible.

## Commit Message Guidelines

Clear and concise commit messages help us understand the history of changes. We encourage a format similar to [Conventional Commits](https://www.conventionalcommits.org/):

*   **Format:** `<type>(<scope>): <subject>`
    *   **Type:** `feat` (new feature), `fix` (bug fix), `docs` (documentation), `style` (formatting, missing semicolons, etc.), `refactor`, `test`, `chore` (updating build tasks, etc.).
    *   **Scope (Optional):** The module or part of the codebase affected (e.g., `gui`, `database`, `chat`).
    *   **Subject:** A short, imperative summary of the change (e.g., "Add chat color utility," "Fix NPE in GUI listener").
*   **Body (Optional):** Provide more context if the subject line isn't enough.
*   **Footer (Optional):** Reference issue numbers (e.g., `Fixes #123`).

**Examples:**

```
feat(gui): Add support for animated item textures
```
```
fix(database): Resolve connection leak in SQL provider

This commit addresses an issue where SQL connections were not
being properly closed under certain high-load conditions, leading
to resource exhaustion.

Closes #42
```

## Pull Request Process

1.  Ensure any install or build dependencies are removed before the end of the layer when doing a build.
2.  Update the README.md with details of changes to the interface, this includes new environment variables, exposed ports, useful file locations and container parameters.
3.  Increase the version numbers in any examples files and the README.md to the new version that this Pull Request would represent. The [Semantic Versioning](http://semver.org/) guidelines are followed.
4.  You may merge the Pull Request in once you have the sign-off of one other developer, or if you do not have permission to do that, you may request the reviewer to merge it for you.
5.  Your Pull Request should:
    *   Have a clear and descriptive title.
    *   Explain the **purpose** of the changes (the "why").
    *   Summarize the **changes** made (the "what").
    *   Link to any relevant issues (e.g., `Fixes #123` or `Relates to #456`).
    *   Confirm that all tests pass.
    *   Be based on an up-to-date `main` (or `master`) branch.

Project maintainers will review your PR and may ask for changes or clarifications. Please be responsive to feedback.

## Code of Conduct

All contributors are expected to adhere to the [Code of Conduct](CODE_OF_CONDUCT.md). Please read it to understand the standards of behavior we expect in our community.

## License

By contributing to LabCommons, you agree that your contributions will be licensed under its [GNU GPLv3 License](LICENSE).

## Questions?

If you have any questions or need clarification, feel free to:

*   Open an issue on [GitHub Issues](https://github.com/MichealAPI/LabCommons/issues).
*   Start a discussion on [GitHub Discussions](https://github.com/MichealAPI/LabCommons/discussions).

Thank you for contributing!
