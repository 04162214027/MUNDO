# Contributing to Mobile Shop ERP

First off, thank you for considering contributing to Mobile Shop ERP! It's people like you that make this project better.

## Code of Conduct

This project and everyone participating in it is governed by our Code of Conduct. By participating, you are expected to uphold this code.

## How Can I Contribute?

### Reporting Bugs

Before creating bug reports, please check the existing issues to avoid duplicates. When you create a bug report, include as many details as possible:

- Use a clear and descriptive title
- Describe the exact steps to reproduce the problem
- Provide specific examples to demonstrate the steps
- Describe the behavior you observed and what behavior you expected
- Include screenshots if possible
- Include your device and OS information

### Suggesting Enhancements

Enhancement suggestions are tracked as GitHub issues. When creating an enhancement suggestion, include:

- Use a clear and descriptive title
- Provide a detailed description of the suggested enhancement
- Explain why this enhancement would be useful
- List any alternatives you've considered

### Pull Requests

1. Fork the repo and create your branch from `main` or `develop`
2. If you've added code that should be tested, add tests
3. Ensure the test suite passes
4. Make sure your code follows the existing code style
5. Write a clear commit message
6. Open a pull request with a clear description of changes

## Development Setup

### Prerequisites
- Android Studio Hedgehog or later
- JDK 17
- Android SDK 26+

### Setup Steps

1. Clone your fork:
```bash
git clone https://github.com/YOUR_USERNAME/MobileShopERP.git
cd MobileShopERP
```

2. Open in Android Studio

3. Sync Gradle files

4. Run the app on an emulator or device

## Coding Style

- Follow Kotlin coding conventions
- Use meaningful variable and function names
- Comment complex logic
- Keep functions small and focused
- Use Jetpack Compose best practices

## Commit Messages

- Use the present tense ("Add feature" not "Added feature")
- Use the imperative mood ("Move cursor to..." not "Moves cursor to...")
- Limit the first line to 72 characters or less
- Reference issues and pull requests after the first line

Example:
```
Add customer search functionality

- Implement search bar in customer list
- Add filtering by name and phone
- Update UI to show search results

Fixes #123
```

## Project Structure

```
app/src/main/java/com/mobileshop/erp/
├── data/           # Database, DAOs, Repositories
├── di/             # Dependency Injection modules
├── ui/             # Compose UI and ViewModels
│   ├── navigation/ # Navigation setup
│   ├── screens/    # Screen composables
│   └── theme/      # App theming
└── MobileShopApp.kt
```

## Testing

- Write unit tests for business logic
- Write UI tests for critical user flows
- Ensure all tests pass before submitting PR

## Documentation

- Update README.md if you change functionality
- Add KDoc comments to public APIs
- Update changelog for notable changes

## Questions?

Feel free to open an issue with your question or reach out to the maintainers.

## License

By contributing, you agree that your contributions will be licensed under the MIT License.

Thank you for contributing! 🎉
