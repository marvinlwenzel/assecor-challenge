# People and Colors

## Software (versions) used

| Software | Version                                                              | Notes                              |
| -------- | -------------------------------------------------------------------- | ---------------------------------- |
| Java     | openjdk version "11.0.3" 2019-04-16                                  |                                    |
| Maven    | Apache Maven 3.6.0                                                   |                                    |
| OS       | "linux", version: "4.18.0-20-generic", arch: "amd64", family: "unix" |                                    |
| MySQL    | MYSQL_VERSION=8.0.16-2debian9 (in docker build mysql:8)              | run in Docker                      |
| Docker   | Docker version 18.09.6, build 481bc77                                | Not used to run Apllication Server |



## SetUp

`implementation/src/main/resources/application.yml`

```yml
app:
  persons:
    parsing:
      location: /your/path/to/persons_dirty_linescramble_medium.csv
      mode: [never|if-empty|import-no-wipe]
```

*   `app.persons.parsing.mode` decides the apps behavior on start-up regarding the parsing of the given csv
    *   `import-no-wipe` will always read the CSV, save the read entries and might override previous entries with same IDs as the parsed ones. It does not wipe the DB before, so the previous dataset and the CSV dataset get mixed.
    *   `if-empty` will import the CSV iff there are no Person entities in the PersonRepository whatsoever.
    *   `never` should be self-explanatory
*   `app.persons.parsing.location` is the fully qualified path to the CSV that might get parsed.


```yml
spring:
  datasource:
    url: jdbc:mysql://192.168.124.2:3306/assecorpeople1
    username: root
    password: 1234

  jpa:
    hibernate:
      ddl-auto: validate
    properties:
      hibernate:
        dialect: org.hibernate.dialect.MySQL8Dialect
```

Should also be adjusted - obviously.

## Tests

Some tests require a test database. Please run the `implementation/src/test/resources/db.mysql/drop-create-insert.sql` for initial and expected data sets. Also adjust the `spring.datasource` properties in the `implementation/src/test/resources/application.yml`.

## Not yet implemented

*   Select between DB- and InMemeory-Repositories without modifying source code. Could be maybe done with a ConfigurationBean that provides the concrete implementation for injection, but the DB implementations are provided automagically via Spring-JPA...

## Few notes

There are nearly no restrictions to the data of person entities. Reason is that I try my best to use as much data from the CSV as possible to prevent it from not being read. Loss of data can be worse that bad quality of data - depending on the context. Since no context is given, I took the *save all data* approach. Without any delimiters, a can not interpret the data, so 2 of the entries in the `data/persons_dirty_linescramble_medium.csv` are still lost.

The CSV import is part of the `@PostConstruct SimplePersonService.init()`.

Business logic got into the `FuzzyCsvParser` - bad for division of concerns, good for saving data. Therefore it also got into the `services` package in an own subpackage.

The code duplication between `implementation/src/main/java/assecorpeople/persistence/inmemory/InMemoryColorRepository.java` and `implementation/src/test/java/assecorpeople/unit/mocks/MockInMemoryTestColorRepository.java` seems necessary as test and production should be divided.

The overwriting of existing person entities from csv is not consistent with the restriction of unique IDs at the `POST /persons` interface which responds `409 CONFLICT` on usage of an already used ID. Would either need adjustment or clear business logical reason, which can not be provided given the task.
