# README #

This README would normally document whatever steps are necessary to get your application up and running.

### Design Decisions
- No bulk rental; only single rental
- No TOs. Not necessary
- No DB transactions
- Since there is a one-way relationship between films and reservations, I felt that it was fine to have the reservation service call the film service. Should a two-way relationship exist, then a business service is required in order to avoid a cyclic dependency.
- No Auth'n and Auth'z. No clientId either (since this would typically come from an auth token)

### Assumptions
- There is only one of each film available. If it's reserved then it is not possible for another person to rent that film.
