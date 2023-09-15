
# Calculate Public Pension Adjustment Backend
This service provides the backend for the Calculate Public Pension Adjustment Frontend.

## Frontend
[Calculate Public Pension Adjustment Frontend](https://github.com/hmrc/calculate-public-pension-adjustment-frontend)

## Persistence
This service uses mongodb to persist user answers and calculation result.

## Requirements
This service is written in Scala using the Play framework, so needs at least a JRE to run.

JRE/JDK 11 is recommended.

The service also depends on mongodb.

## Running the service
Using service manager (sm or sm2)
Use the CALCULATE_PUBLIC_PENSION_ADJUSTMENT service to bring up a services using the latest tagged release
```
sm2 --start CALCULATE_PUBLIC_PENSION_ADJUSTMENT
```

Run `sm2 -s` to check what services are running.

### Launching the service locally
To bring up the service on the configured port 12802, use

```
sbt run
```

## Testing the service
This service uses sbt-scoverage to provide test coverage reports.

Use the following command to run the tests with coverage and generate a report.
```
sbt clean coverage test it:test coverageReport
```

## Scalafmt
To prevent formatting failures in a GitHub pull request,
run the command ``sbt scalafmtAll`` before pushing to the remote repository.

## License
This code is open source software licensed under the [Apache 2.0 License]("https://www.apache.org/licenses/LICENSE-2.0.html").