# Spring Framework - Obtains Lead Information via Web Scraping and Sends AI-Generated Emails

This project demonstrates the integration of Spring AI with Ollama, utilizing the Llava model for AI-powered functionalities. It uses `jsoup` for web scraping to gather lead information and `Spring AI` to generate the email content.
## Table of Contents
- [Prerequisites](#prerequisites)
- [Installation](#installation)
- [Configuration](#configuration)
- [Usage](#usage)
- [Features](#features)
- [Contributing](#contributing)
- [License](#license)

## Prerequisites

Before you begin, ensure you have met the following requirements:

- 8 GB Ram, 16 GB or + preferred
- [Java 22](https://jdk.java.net/22/) recommended
- IDE [IntelliJ IDEA](https://www.jetbrains.com/idea/download) recommended
- [Maven](https://maven.apache.org/download.cgi) version 3.9.0 or higher (Ideally installed for command line use)
- [Ollama](https://ollama.com/download) installed and running on your system
- Llama3.1 model downloaded and available in Ollama for general knowledge

## Installation

1. Clone the repository:
   ```
   git clone https://github.com/ricardoseb/spring-ollama-search-lead.git
   ```

2. Navigate to the project directory:
   ```
   cd spring-ollama-search-lead
   ```

3. Build the project:
   ```
   ./mvnw clean install
   ```

## Configuration

1. This project is pre-configured for Ollama models. If you need to set up any other model, open the application.properties (or application.yml) file and configure the desired model there.
   ```
   spring.ai.ollama.model=desiredModel
   ```

2. Adjust any other settings as needed for your specific use case.

## Usage

Describe how to run and use your application. For example:

1. Start the Spring Boot application:
   ```
   ./mvnw spring-boot:run
   ```

2. Access the API endpoints (provide examples of available endpoints and how to use them).

### Email Configuration


To configure email sending using Gmail, add the following properties to your `application.properties` file, but first follow this tutorial for Gmail configuration:
https://medium.com/@seonggil/send-email-with-spring-boot-and-gmail-27c14fc3d859

```properties
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

## Features

- Integration with Spring AI for seamless AI operations
- Web Scraping capabilities
- Email sending capabilities

## Contributing

Contributions to this project are welcome. Please follow these steps:
1. Fork the repository
2. Create a new branch (`git checkout -b feature/your-feature-name`)
3. Commit your changes (`git commit -am 'Add some feature'`)
4. Push to the branch (`git push origin feature/your-feature-name`)
5. Create a new Pull Request

## License

This project is licensed under the [MIT License](LICENSE).
