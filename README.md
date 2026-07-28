# Expense Tracker

A modular expense tracking application built in Java, supporting user accounts, expense logging, budgets, categories, and reporting.

## Features

- **Authentication** – User registration and login
- **Expense Management** – Add, edit, list, and delete expenses
- **Budgets** – Set spending limits per category and track status
- **Reports** – Monthly summaries and category breakdowns
- **Categories & Settings** – Custom expense/income categories and user preferences

## Tech Stack

- **Language:** Java
- **Build Tool:** Maven 
- **Database:** *MySQL(work bench)*

## Project Structure

```
src/
├── main/java/
│   ├── auth/          # Login, Register, User, UserRepo
│   ├── expense/        # Add/Edit/List, ExpenseRepo, ExpenseService
│   ├── budget/          # Budget and Reports logic
│   ├── category/        # Categories and Settings
│   └── shared/          # Shared models/interfaces used across modules
└── test/java/           # Unit and integration tests
```

## Team & Module Ownership

| Module | Owner | Responsibility |
|---|---|---|
| Auth + User | Person A | Login, Register, User, UserRepo |
| Expense | Person B | Add/Edit/List, ExpenseRepo, ExpenseService |
| Budget + Reports | Person C | Budget tracking, monthly/category reports |
| Categories + Settings | Person D | Category management, user settings |
| QA/Polish | Person E | Integration testing, bug fixes, code review |

## Getting Started

### Prerequisites

- JDK 17+ 
- Maven or Gradle installed
- *MySQL*

### Installation

1. Clone the repository
   ```bash
   git clone <repo-url>
   cd expense-tracker
   ```

2. Build the project
   ```bash
   mvn clean install
   ```
   *(or `./gradlew build` if using Gradle)*

3. Configure environment variables / application properties
   ```bash
   cp src/main/resources/application.properties.example src/main/resources/application.properties
   ```
   Then fill in database credentials and any required secrets.

4. Run the application
   ```bash
   mvn spring-boot:run
   ```
   *(adjust based on your actual run command)*

## Running Tests

```bash
mvn test
```

## Contributing

1. Create a feature branch from `main`: `git checkout -b feature/your-feature`
2. Commit your changes with clear messages
3. Push and open a pull request for review
4. Ensure your module implements the shared interfaces agreed upon by the team before merging

## License

MIT
