# SJ Coin Vending

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
touch application.properties
```

Add this properties to the previously created file

```properties
coins.server.host=https://somehostname/v1
image.storage.path=${HOME}/.vending/images

#AUTH
auth.server.host=http://somehostname
auth.client.id=someClientId
auth.client.secret=someSecret
authPublicKeyFile=/home/username/.vending/auth.pub

# Datasource
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/sj_vending?useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC&useSSL=false&CharSet=utf8&characterEncoding=utf8&useUnicode=true
spring.datasource.username=sj_vending
spring.datasource.password=somePassword

# Machine
machine.request.signer.keystore.file=/home/username/.vending/vending.jks
machine.request.signer.keystore.password=somePassword
machine.request.signer.keystore.alias=vending
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