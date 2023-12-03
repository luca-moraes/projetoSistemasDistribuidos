#!/bin/bash

if [ $# -eq 0 ]; then
    echo "Erro: Nenhum argumento fornecido. Informe 1, 2 ou 3."
    exit 1
fi

porta=$1

if [ "$porta" -eq 1 ] || [ "$porta" -eq 2 ] || [ "$porta" -eq 3 ]; then
    cd ..
else
    echo "Erro: O número fornecido não é 1, 2 ou 3 -> $porta é inválido"
    exit 1
fi

case $porta in
    1)
        echo "server.port=8081" > ./src/main/resources/application.properties
        ;;
    2)
        echo "server.port=8082" > ./src/main/resources/application.properties
        ;;
    3)
        echo "server.port=8083" > ./src/main/resources/application.properties
        ;;
    *)
        echo "Erro: O número fornecido não é 1, 2 ou 3."
        exit 1
        ;;
esac

# echo "teste" > ./src/main/resources/application.properties
mvn clean install -U
## java -jar target/banco-0.0.1-SNAPSHOT.jar -Dserver.port=8082
java -jar target/banco-0.0.1-SNAPSHOT.jar