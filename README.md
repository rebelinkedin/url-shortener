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

- **Unique Shortened URLs for Each Original URL (Unless a Custom Alias is Used)**:  
  In the current implementation, the POST request is idempotent for a given original URL. This means that once a URL has
  been shortened using a hashed version of the original URL, the same shortened link will always be returned, regardless
  of the user or session. This approach optimizes storage. While users can generate multiple shortened URLs for the same
  original URL by specifying custom aliases, the system does not currently support generating different hashed URLs for
  the same original link across multiple users or sessions when custom aliases are not provided. This limitation can be
  addressed by extending the application to support user- or session-based URL generation, associating each shortened
  URL with a user or session.

Despite these limitations, the architecture is designed to be flexible and can be extended without significant
modifications. The application can be integrated with a database layer and support features such as user-based URL
shortening.## Tech Stack

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
    - **Exceptions**: Internal error models.
    - **Domain Model Objects**: Data objects to encapsulate the data structure model.

- **Service**:
    - **Business Logic**: Contains the core functionality for URL shortening and retrieving original URLs. This layer
      interacts with the repository layer to fetch and store data.

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

> git clone <repository-url> && cd your-repo

2. **Build the Project**: Make sure you have Gradle installed, then run:

> ./gradlew build

3. **Run the Application**: Use the command

> ./gradlew run

4. **Access the Application**:
   Once the application is running, you can access it through your web browser or API client (like Postman) at the
   following URL:

> http://localhost:8080

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

* There are 2 ways to run the tests

    1. **Directly from IntelliJ** by clicking the play button next to each test
    2. Use Gradle `test` task

> ./gradlew clean test
