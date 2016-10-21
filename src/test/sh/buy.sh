#!/usr/bin/env bash

source ./init.sh

#### ADDITIONAL INFO ######
### Store username asd password in init.sh file and do NOT include this file in commit
### Create that file with the following commands
### declare -r yourLDAPid=username
### declare -r yourLDAPpassword=password

##get token
tokens=(`curl --silent -i -k -G -X POST \
  -H "Authorization: Basic dXNlcl9jcmVkOnN1cGVyc2VjcmV0" \
  -H "Content-Type:application/x-www-form-urlencoded" \
  -d "username="${yourLDAPid} \
  -d "password="${yourLDAPpassword} \
  -d "grant_type=password" \
  https://sjcoins.testing.softjourn.if.ua/auth/oauth/token \
  | grep -Po "((?<=access_token\":\")[^\"]+)|((?<=refresh_token\":\")[^\"]+)"`)

echo "ACCESS_TOKEN: "${tokens[0]}
echo "REFRESH_TOKEN: "${tokens[1]}

curl --silent -i -k -G -X POST \
  https://sjcoins.testing.softjourn.if.ua/vending/v1/machines/7/products/10 \
  -H "Authorization: Bearer "${tokens[0]}
