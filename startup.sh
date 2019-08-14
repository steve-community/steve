#!/bin/bash

# wait for db to start 
dockerize -wait tcp://mariadb:3306 -timeout 60s
dockerize -wait http://adminer:8080 -timeout 60s

# now build and run 
mvn package  -Pdocker 
java -jar target/steve.jar

