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
v8              Versioning
v9              Service Discovery
v10             Circuit Breaker
```

### Database Setup

#### Redis
```
brew install redis
```

Modify /usr/local/etc/redis.conf

```
requirepass foobared
```

#### MySQL
```
brew install mysql
```

Modify /usr/local/etc/my.cnf

```
default-time-zone='+00:00'
```

```
mysql -uroot --execute="drop user 'uservices'@'localhost'"
mysql -uroot --execute="create user 'uservices'@'localhost' identified by 'uservices';"

for database_name in 'allocations' 'backlog' 'registration' 'timesheets'; do
    mysql -uroot --execute="drop database if exists ${database_name}_test"
    mysql -uroot --execute="create database ${database_name}_test"
    mysql -uroot --execute="grant all on  ${database_name}_test.* to 'uservices'@'localhost';"
    mysql -uroot --execute="grant select on performance_schema.* to 'uservices'@'localhost';"
done
```

### Schema Migrations

```
for database_name in 'allocations' 'backlog' 'registration' 'timesheets'; do
    flyway -user=uservices -password=uservices -url="jdbc:mysql://localhost:3306/${database_name}_test" -locations=filesystem:databases/${database_name}-database clean migrate
done
```

### Test and Production Environment

````
export PORT=8081

export VCAP_SERVICES='{"p-mysql": [{"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/allocations_test?user=uservices&password=uservices"}, "name": "allocations"}, {"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/backlog_test?user=uservices&password=uservices"}, "name": "backlog"}, {"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/registration_test?user=uservices&password=uservices"}, "name": "registration"}, {"credentials": {"jdbcUrl": "jdbc:mysql://localhost:3306/timesheets_test?user=uservices&password=uservices"}, "name": "timesheets"}], "rediscloud": [{"credentials": {"hostname": "localhost", "password": "foobared", "port": 6379}, "name": "discovery"}]}'

export DISCOVERY_SERVER_ENDPOINT=http://localhost:8888
````

_Note: The registration server endpoint port must match the port used in the FlowTest_