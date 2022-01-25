# SJ Coin Vending

## Prepare stage
#### Setup environment variables.
```
SJ_VENDING_SERVER_DATASOURCE_URL=jdbc:mysql://localhost:3306/sj_coins?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false
SJ_VENDING_SERVER_DATASOURCE_USERNAME=sj
SJ_VENDING_SERVER_DATASOURCE_PASSWORD=password
SJ_VENDING_SERVER_LOGGING_CONFIG_FILE=/path/to/coins/logback.xml
SJ_VENDING_SERVER_COINS_SERVER_URL=http://127.0.0.1:8080/coins/v1
SJ_VENDING_SERVER_AUTH_SERVER_URL=http://127.0.0.1:8081
SJ_VENDING_SERVER_AUTH_CLIENT_ID=vending_server
SJ_VENDING_SERVER_AUTH_CLIENT_SECRET=<client_secret>
SJ_VENDING_SERVER_AUTH_PUBKEY_PATH=/path/to/vending/auth.pub
SJ_VENDING_MACHINE_KEYSTORE_PATH=/path/to/vending/vending.jks
SJ_VENDING_MACHINE_KEYSTORE_PASSWORD=<keystore_password>
SJ_VENDING_MACHINE_KEYSTORE_ALIAS=vending
```
Or with export:
```
export SJ_VENDING_SERVER_DATASOURCE_URL='jdbc:mysql://localhost:3306/sj_coins?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false'
export SJ_VENDING_SERVER_DATASOURCE_USERNAME='sj'
export SJ_VENDING_SERVER_DATASOURCE_PASSWORD='password'
export SJ_VENDING_SERVER_LOGGING_CONFIG_FILE='/path/to/coins/logback.xml'
export SJ_VENDING_SERVER_COINS_SERVER_URL='http://127.0.0.1:8080/coins/v1'
export SJ_VENDING_SERVER_AUTH_SERVER_URL='http://127.0.0.1:8081'
export SJ_VENDING_SERVER_AUTH_CLIENT_ID='vending_server'
export SJ_VENDING_SERVER_AUTH_CLIENT_SECRET='<client_secret>'
export SJ_VENDING_SERVER_AUTH_PUBKEY_PATH='/path/to/vending/auth.pub'
export SJ_VENDING_MACHINE_KEYSTORE_PATH='/path/to/vending/vending.jks'
export SJ_VENDING_MACHINE_KEYSTORE_PASSWORD='<keystore_password>'
export SJ_VENDING_MACHINE_KEYSTORE_ALIAS='vending'
```

## Start up documentation

### Step 1: Create databases structure

#### Enter as root user and create user for these databases using commands:

```sql
CREATE USER 'user'@'localhost' IDENTIFIED BY 'somePassword';

GRANT ALL PRIVILEGES ON *.* TO 'user'@'localhost';
```

#### Enter as this new user and create databases:

```sql
CREATE DATABASE sj_vending CHARACTER SET utf8;
```

#### NOTE: All the tables will be created during the first service start.

### Step 2: Create keystore file, certificate and extract public key:

```bash
keytool -genkey -v -keystore vending.jks -alias vending -keyalg RSA -keysize 2048 -validity 10000

keytool -export -keystore vending.jks -alias vending -file vending.cer

openssl x509 -inform der -pubkey -noout -in vending.cer > vending.pub
```


### Step 3: Add sensitive properties:

```bash
mkdir $HOME/.vending
mkdir $HOME/.vending/images
```

### Step 4: Add logback configuration

```bash
cd $HOME/.vending
touch logback.xml
```

Add basic configuration to the file

```xml
<configuration debug="true" scan="true" scanPeriod="30">

    <appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
        <encoder>
            <pattern>%-35(%d{dd-MM-yyyy} %magenta(%d{HH:mm:ss}) [%5.10(%thread)]) %highlight(%-5level) %cyan(%logger{16}) - %msg%n</pattern>
        </encoder>
    </appender>

    <root level="info">
        <appender-ref ref="STDOUT" />
    </root>
</configuration>
```

### Step 5: Run project
