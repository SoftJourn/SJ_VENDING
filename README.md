# SJ Coin Vending

## Start up documentation

### Step 1: create databases structure

#### Enter as root user and create user for these databases using commands:

```sql
Create User 'sj_vending'@'localhost' IDENTIFIED BY '2XenNakX1e3RLrpT';
grant all privileges on *.* to 'sj_vending'@'localhost';
```

#### Enter as this new user and create databases:

```sql
create database sj_vending character set utf8;
```

#### Init databases using sql scripts which are located in db folders of each module;

### Step 2: Add certificates to your jvm's key storage:

#### Go to your jre's key tool directory(by default it is "/usr/java/latest/jre/bin/keytool") and use commands(by default to set certificate password "changeit" is used):

```bash
sudo keytool -import -alias vending -file ~/sj_coins/vending/src/main/resources/ssl/vending.cer -keystore cacerts
```
#### Reboot system to use certificates

### Step 3: Download bower dependencies for admin page(install nodejs if you haven't installed it):

```bash
bower install
```

### Step 4: Run project and enter in browser [https://localhost:8222/admin](https://localhost:8222/admin)