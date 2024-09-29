# URL Shortener

## Table of Contents

- [Project Overview](#project-overview)
    - [Current Limitations](#current-limitations)
- [Tech Stack](#tech-stack)
- [How to Run](#how-to-run)
- [Architecture](#architecture)
- [Testing](#testing)
    - [Run tests](#run-tests)

## Project Overview

The URL Shortener Application provides a simple way to shorten long URLs and retrieve the original URL using a shortened
link. It's designed to be lightweight and efficient, making it easy to integrate into other services or use standalone.

### Current Limitations

- **In-memory Storage**:  
  The current architecture employs an in-memory storage solution, which limits scalability for large production
  environments. Since the application stores URLs solely in memory, all data will be lost upon server restart, and it
  cannot manage large datasets or support distributed usage across multiple servers. To overcome these limitations,
  transitioning to a persistent data store (such as a relational database, NoSQL database, or external cache) would be
  necessary.

- **Consistent Shortened URLs for Each Original Link (Unless a Custom Alias is Used)**:  
  In the current implementation, shortening an original URL using a POST request will always return the same shortened
  URL. This ensures that once a URL has been shortened, the same short link is provided for future requests, regardless
  of the user or session, making the process efficient and reducing storage needs. While users have the option to create
  multiple shortened URLs by specifying custom aliases, the system does not currently support generating different
  shortened links for the same original URL across different sessions. To enable session-based URL generation, the
  application could be extended to create unique short URLs for each user or session.

- **Hashing Strategy**:
  The current URL shortening implementation uses an MD5 hash of the original URL to generate a unique numeric value,
  which is then encoded into a Base62 string. While this approach efficiently creates short, deterministic URLs, it is
  not completely collision-free. Although the likelihood of collisions (where two different URLs generate the same
  shortened link) is low, it is still possible, especially as the number of shortened URLs increases over time.
  Additionally, the system currently limits shortened URLs to a fixed length of 8 characters to ensure consistency and
  simplicity. However, this fixed length could become insufficient as the system scales and more unique URLs are
  generated. Over time, the risk of collisions grows, and the fixed length may no longer provide adequate uniqueness.

Despite these limitations, the architecture is designed to be flexible and can be extended without significant
modifications.

## Tech Stack

- **Kotlin**: 2.0+
- **SDK**: 17.0+
- **Gradle**: 8.0+
- **Ktor**: A Kotlin-based framework for building asynchronous servers and clients in connected systems.
- **Serialization**: Using Kotlinx serialization for JSON handling.
- **JUnit**: For unit testing the application.

## Architecture

This application follows a layered architecture that separates concerns and improves maintainability:

- **API**:
    - **Controllers**: Responsible for handling incoming HTTP requests, validating inputs, and sending responses.

- **Application**:
    - **Ktor Plugins**: The application uses various Ktor plugins for features like serialization and logging.
    - **Dependency Injection**: Manual dependency injection module.

- **Model**:
    - **Domain Model Objects**: Objects that represent entities in the domain and encapsulate related data and
      behavior.

- **Service**:
    - **Business Logic**: Contains the core business functionality for shortening URLs and retrieving original URLs.
      This layer leverages the repository layer to fetch and store data.

- **Repository**:
    - **Data Access**: Handles all data-related operations. This layer abstracts the data storage details, providing a
      clean interface for the service layer.

- **Utility**:
    - **Utilities**: Provides utility classes and extension functions.

- **Application**:
    - **Entry Point**: The main application class initializes the Ktor server and configures routing, plugins, and
      dependency injections. It brings together all the layers and starts the application.

## How to Run

To run the application locally, follow these steps:

1. **Clone the Repository**:

  ```bash
  git clone <repository-url>
  cd <repository-name>
  ```

2. **Build the Project**: Make sure you have Gradle installed, then run:

  ```bash
  ./gradlew build
  ```

3. **Run the Application**: Use the command

  ```bash
  ./gradlew run
  ```

4. **Access the Application**:
   Once the application is running, you can access it through your web browser or API client (like Postman) at the
   following URL:

  ```bash
  http://localhost:8080
  ```

5. **Test the Endpoints**: Use Postman or Curl to test the API endpoints.

**Shorten URL**

   ```bash
   curl --location 'localhost:8080/api/v1/urls/shorten' --header 'Content-Type: application/json' --data '{"longUrl": "${your_long_url}", "alias": "${your_alias}" }'
   ```

**Get Original URL**

   ```bash 
   curl --location 'localhost:8080/api/v1/urls/${shortUrl}'
   ```

## Testing

Testing is a critical part of the development process in this project.

* **Unit Tests**
    * Focus on testing individual components, such as business logic and utility methods, in isolation.
* **Integration Tests**
    * Simulate real requests to the API endpoints and validate the response is as expected. These
      tests ensure that all layers of the application (controller, service, repository) work together correctly.

### Run tests

There are 2 ways to run the tests

1. **Directly from IntelliJ** by clicking the play button next to each test
2. Use **Gradle** `test` task
  
```bash
./gradlew build
```
