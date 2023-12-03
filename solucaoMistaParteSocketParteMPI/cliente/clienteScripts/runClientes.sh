#!/bin/bash

# if [ $# -eq 0 ]; then
#     echo "Erro: Nenhum argumento fornecido. Informe 1, 2 ou 3."
#     exit 1
# fi

# porta=$1

# if [ "$porta" -eq 1 ] || [ "$porta" -eq 2 ] || [ "$porta" -eq 3 ]; then
#     cd ..
# else
#     echo "Erro: O número fornecido não é 1, 2 ou 3 -> $porta é inválido"
#     exit 1
# fi

# python cliente.py $porta

cd ..

mpiexec -n 3 python cliente.py