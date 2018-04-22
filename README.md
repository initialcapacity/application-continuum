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
mysql -uroot --execute="create database uservices_test"

mysql -uroot --execute="grant all on uservices_test.* to 'uservices'@'localhost' identified by 'uservices';"
```

### Schema Migrations

```
flyway -url="jdbc:mysql://localhost:3306/uservices_test?user=root&password=" -locations=filesystem:databases/continuum clean migrate
```

### Test and Production Environment

````
export PORT=8081

export VCAP_SERVICES='{"p-mysql": [ { "credentials": { "jdbcUrl": "jdbc:mysql://localhost:3306/uservices_test?user=root&password=&useTimezone=true&serverTimezone=UTC" }, "name": "appcontinuum"} ] }'

export REGISTRATION_SERVER_ENDPOINT=http://localhost:8883
````

_Note: The registration server endpoint port must match the port used in the FlowTest_