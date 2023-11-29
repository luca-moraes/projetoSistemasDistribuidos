#!/bin/bash

cd ..
cd banco/
mvn compile
mvn clean install exec:java
mvn exec:java -e

mvn clean package
java -jar target/my-project-1.0.0-jar-with-dependencies.jar

# curl -X GET "http://localhost:4567/FazerTransferencia?clienteId=11&valorTransferencia=100&chaveDestino=a1"
