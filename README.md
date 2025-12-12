# event-booking-system

A Java-based event booking system application built with Gradle and JUnit 5.

## Prerequisites

- Java 17 or higher
- No need to install Gradle (uses Gradle Wrapper)

## Getting Started

### Build the Project

```bash
cd event-booking-service

# On Windows (Command Prompt or PowerShell)
.\gradlew.bat build

# On Linux/Mac or Git Bash
./gradlew build
```

### Run the Application

```bash
# On Windows
.\gradlew.bat run

# On Linux/Mac or Git Bash
./gradlew run
```

### Run Tests

```bash
# On Windows
.\gradlew.bat test

# On Linux/Mac or Git Bash
./gradlew test
```

### Clean Build Artifacts

```bash
# On Windows
.\gradlew.bat clean

# On Linux/Mac or Git Bash
./gradlew clean
```

### Clean and Rebuild

```bash
# On Windows
.\gradlew.bat clean build

# On Linux/Mac or Git Bash
./gradlew clean build
```

## Common Gradle Commands

| Command | Description |
|---------|-------------|
| `./gradlew build` | Compile, test, and package the application |
| `./gradlew run` | Run the application |
| `./gradlew test` | Run all tests |
| `./gradlew clean` | Remove build artifacts |
| `./gradlew tasks` | List all available tasks |
| `./gradlew --version` | Display Gradle version |

## Dependencies

- **JUnit 5** (5.10.0) - Testing framework

## Development

The project uses:
- Java 17
- Gradle 8.5 (via wrapper)
- JUnit 5 for testing