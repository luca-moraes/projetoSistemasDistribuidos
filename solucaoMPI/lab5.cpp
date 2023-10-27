#include <mpi.h>
#include <iostream>
#include <cstdio>
#include <ostream>
#include <cstdlib>
#include <ctime>
#include <algorithm>

using namespace std;

using std::cout;
using std::endl;
using std::max_element;

void printArray(float* array, int size){
    for(int i = 0; i < size; i++){
        cout << array[i] << " ";
    }
    cout << endl;
}

void fillFloatArray(float* array, int size){
    for(int i = 0; i < size; i++){
        // gera nums random entre 0 e 9
        array[i] = rand() / (float)(RAND_MAX/9.0f);
    }
}

float maxNumArray(float* array, int size){
    float* maxNum = max_element(array, array + size);
    return *maxNum;
}

int main(int argc, char** argv){
    int processRank, clusterSize;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &clusterSize);
    MPI_Comm_rank(MPI_COMM_WORLD, &processRank);

    if(clusterSize < 1){
        cout << "Número de processos deve ser maior que 1!" << endl;
        MPI_Finalize();
        exit(1);
    }

    int sizeBiggerNumsArray = 5;
    float biggerNums[sizeBiggerNumsArray];

    if(processRank == 0){
        int sizeRandomArray = (clusterSize) * sizeBiggerNumsArray;        
        float randomNums[sizeRandomArray];

        fillFloatArray(randomNums, sizeRandomArray);

        cout << "Array de números aleatórios: " << endl;

        printArray(randomNums, sizeRandomArray);

        for(int i = 0; i < sizeBiggerNumsArray; i++){
            biggerNums[i] = randomNums[i];
        }

        for(int i = 1; i < clusterSize; i++){
            MPI_Send(&randomNums + (i)*sizeBiggerNumsArray, sizeBiggerNumsArray, MPI_FLOAT, i, 7, MPI_COMM_WORLD);
        }
    }

    MPI_Barrier(MPI_COMM_WORLD);

    if(processRank != 0){
        MPI_Recv(&biggerNums, sizeBiggerNumsArray, MPI_FLOAT, 0, 7, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
    }

    MPI_Barrier(MPI_COMM_WORLD);

    // todos os processos deben encontrar el major valor e enviar para lo processo 0
    if(processRank != 0){
        float maxNum = maxNumArray(biggerNums, sizeBiggerNumsArray);
        MPI_Send(&maxNum, 1, MPI_FLOAT, 0, 7, MPI_COMM_WORLD);
    }

    MPI_Barrier(MPI_COMM_WORLD);

    if(processRank == 0){
        float maxNums[clusterSize];

        maxNums[0] = maxNumArray(biggerNums, sizeBiggerNumsArray);

        for(int i = 1; i < clusterSize; i++){
            MPI_Recv(&maxNums[i], 1, MPI_FLOAT, i, 7, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        }

        cout << "Array de maiores valores: " << endl;

        printArray(maxNums, clusterSize);

        cout << "Maior valor de TODOS OS MUNDOS DO UNIVERSO!!!: " << maxNumArray(maxNums, clusterSize) << endl;
    }

    MPI_Finalize();
    return 0;
}