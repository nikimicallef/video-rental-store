# README #

This application contains two main entities;

- Film: Represents a film boarded onto the system. Apart from the `film name`, the `film type` (`new release`, `regular` and `old`) is recorded along with the `availability` of the film. Since it has been assumed that the system only has one copy of each film, once a film is reserved then the film is not available anymore for renting and any future reservations for this film will fail until the film is returned.
- Reservation: Represents a current or old reservation. A reservation may be `open` (film is currently rented out), `closed` (film as been returned within the expected time period) or `surcharge required` (film has been retained for more than the expected amount of days and the user needs to pay a surcharge). The final status is determined via the other reservation fields stored, namely the `reservation start date`, the `reservation days` and the `expected return date`. If the user is expected to pay a surcharge, the amount will also be displayed in this representation. Once a film is returned, the reservation status changes however it is never deleted from the system.

This application exposes the following endpoints;

- Films Resource
    - `GET /films` returns all films boarded in the system. The user can opt to specify the `film type` or the `availability` of the films in order to receive a reduced set of films.
    - `GET /films/{id}` returns the film with the specified ID.
- Reservations Resource
    - `GET /reservations` returns all reservations in the system.
    - `POST /reservations` creates a new reservation (assuming the data passed in is valid).
    - `POST /reservations/{id}/actions/return` is used for when a film is returned by the user.

The API has been written using OpenAPI and the Spring [OpenAPI Generator](https://openapi-generator.tech/) has been used to auto-generate some controller code. This code is generated on compilation and stored in the target folder.

An in-memory H2 database has been used to store the relevant resources. Some data is automatically boarded upon application startup in order improve ease of use of the application.

The following assumptions have been taken during implementation;

- There is only one of each film available. If it's reserved then it is not possible for another person to rent that film.

The following design decision have been taken (in certain cases these lead to a reduction in scope);

- Only one film can be reserved per API request.
- While normally a transfer object would be used to map between the API model and the DB model, this has been omitted since the application is relatively straight forward. This would have been beneficial in much more complex applications.
- This application is not thread-safe since it does not support DB transaction.
- Since there is a one-way relationship between a film and reservation, I felt that it was acceptable for the reservation service call the film service. This is typically discouraged since this may cause the application to fail to start due to a cyclic dependency however, since the relationship is just one-way then this should not happen.
- No authentication and authorization has been implemented.
- There is no notion of client or user ID in the system since this ID is typically retrieved from the auth token which has not been implemented.
- Since the controller layer is fairly trivial it has not been tested. This layer would be covered using component tests in a more complex application.

The following process may be used to run the application;

1. Build the application using the `mvn clean install` command.
1. Build the docker image using the `docker build --tag videorental .` command.
1. Run the docker image using the `docker run -p 8080:8080 -d --name videorental videorental` command.
    1. You can send API requests on port 8080.
    1. The H2 console can be accessed via the `http://localhost:8080/h2-console` URL. The JDBC URL is `jdbc:h2:mem:testdb`, the username is `sa` with no password.
1. You can stop running the application using the `docker stop videorental` command. Once the image is stopped, all data within the H2 database will be wiped out.
1. You can delete the container using the `docker rm videorental` command.
1. You can delete the original image using the `docker rmi videorental` command.
