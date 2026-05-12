# Lodgings Resto Premium Dashboard

A full-stack premium hotel and restaurant management module built with Spring Boot, PostgreSQL, Thymeleaf, Bootstrap 5, custom CSS, and JavaScript.

## Features

- Premium dashboard UI inspired by modern hotel SaaS products
- Dark sidebar + elegant light content area
- Room CRUD with validation
- Search and filters by room type and status
- Status badges, animated counters, and chart widget
- Toast messages and delete confirmation modal
- Responsive desktop/tablet/mobile layout

## Tech Stack

- Spring Boot
- Spring MVC
- Spring Data JPA
- PostgreSQL
- Thymeleaf
- Bootstrap 5
- Font Awesome
- Chart.js

## Database

Database name: `lodgings_db`

Update `src/main/resources/application.properties` if your PostgreSQL username/password differ.

## Run

```bash
./gradlew bootRun
```

Then open:

```text
http://localhost:8081
```

## Notes

- The room module is fully functional.
- The sidebar also includes premium placeholder links for future modules like reservations, restaurant, billing, and analytics.

