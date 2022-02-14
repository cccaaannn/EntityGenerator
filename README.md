# EntityGenerator
Generates Entity classes from database tables for Spring Boot projects.
---

<br/>

### Running the application

1. Configure `src\main\resources\config.yaml`

2. Run for a single table (prints to console)
```shell
java -jar EntityGenerator-1.0.jar -t <tableName>
```

2. Run for all db (saves to file)
```shell
java -jar EntityGenerator-1.0.jar
```