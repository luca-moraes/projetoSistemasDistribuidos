#!/bin/bash

# mpicc main.c -o main

cd ./solucaoMPI/

mpic++ ./lab5.cpp -o main.run

mpirun -np 4 ./main.run

