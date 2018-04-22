# Application Continuum

The evolution of a component based architecture

See Git tags for step-by-step notes.

```
git tag -ln

v1              First commit
v2              Functional groups
v3              Feature groups (Bounded Context)
v4              Components
v5              Applications
v6              Services
v7              Databases
```

### Database Setup

```
for database_name in 'allocations' 'backlog' 'registration' 'timesheets'; do   
    mysql -uroot --execute="drop database if exists ${database_name}_test"
    mysql -uroot --execute="create database ${database_name}_test"
    mysql -uroot --execute="grant all on  ${database_name}_test.* to 'uservices'@'localhost' identified by 'uservices';"
done
```

### Schema Migrations

```
for database_name in 'allocations' 'backlog' 'registration' 'timesheets'; do
    flyway -url="jdbc:mysql://localhost:3306/${database_name}_test?user=root&password=" -locations=filesystem:databases/${database_name}-database clean migrate
done
```

### Test and Production Environment

````
export PORT=8081

export VCAP_SERVICES='{"p-mysql": [{"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/allocations_test?user=uservices&password=uservices&useTimezone=true&serverTimezone=UTC"}, "name": "allocations"}, {"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/backlog_test?user=uservices&password=uservices&useTimezone=true&serverTimezone=UTC"}, "name": "backlog"}, {"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/registration_test?user=uservices&password=uservices&useTimezone=true&serverTimezone=UTC"}, "name": "registration"}, {"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/timesheets_test?user=uservices&password=uservices&useTimezone=true&serverTimezone=UTC"}, "name": "timesheets"}]}'

export REGISTRATION_SERVER_ENDPOINT=http://localhost:8883
````

_Note: The registration server endpoint port must match the port used in the FlowTest_