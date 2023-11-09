#include <mpi.h>
#include <iomanip>
#include <iostream>
#include <random>
#include <cstdio>
#include <ostream>
#include <cstdlib>
#include <ctime>
#include <algorithm>
#include <sstream>

using namespace std;

using std::cout;
using std::endl;
using std::max_element;
using std::setprecision;

void printArray(float* array, int size){
    for(int i = 0; i < size; i++){
        cout << setprecision(5) << array[i] << " ";
    }
    cout << endl;
}

void printSubArray(int pRank, float* array, int size){
    std::stringstream ss;

    ss << "Valores recebidos pelo processo " << pRank <<  ": " << endl;

    for(int i = 0; i < size; i++){
        ss << setprecision(5) << array[i] << " ";
    }

    cout << ss.str() << endl;
}

void fillFloatArray(float* array, int size){
    int floatMin = 0;
    int floatMax = 100;

    std::random_device rd;
    std::default_random_engine eng(rd());
    std::uniform_real_distribution<> distr(floatMin, floatMax);

    for(int i = 0; i < size; i++){
        // gera nums random entre 0 e 9
        // array[i] = (rand() / (float)RAND_MAX)*9.0f;
        array[i] = distr(eng);
    }
}

float maxNumArray(float* array, int size){
    float* maxNum = max_element(array, array + size);
    return *maxNum;
}

int main(int argc, char** argv){
    int processRank, clusterSize;
    double startTime = 0.0, totalTime = 0.0;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &clusterSize);
    MPI_Comm_rank(MPI_COMM_WORLD, &processRank);

    if(clusterSize < 1){
        cout << "Número de processos deve ser maior que 1!" << endl;
        MPI_Finalize();
        // MPI_Abort(MPI_COMM_WORLD, EXIT_FAILURE);
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

        startTime = MPI_Wtime();

        // for(int i = 0; i < sizeBiggerNumsArray; i++){
        //     biggerNums[i] = randomNums[i];
        // }

        // for(int i = 1; i < clusterSize; i++){
        //     MPI_Send(&randomNums[(i)*sizeBiggerNumsArray], sizeBiggerNumsArray, MPI_FLOAT, i, 0, MPI_COMM_WORLD);
        // }
    }

    MPI_Scatter(&randomNums, sizeBiggerNumsArray, MPI_FLOAT, &biggerNums, sizeBiggerNumsArray, MPI_FLOAT, 0, MPI_COMM_WORLD);


    MPI_Barrier(MPI_COMM_WORLD);

    // if(processRank != 0){
        
        // MPI_Recv(&biggerNums, sizeBiggerNumsArray, MPI_FLOAT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);

        printSubArray(processRank, biggerNums, sizeBiggerNumsArray);
    // }

    MPI_Barrier(MPI_COMM_WORLD);

    // todos os processos deben encontrar el major valor e enviar para lo processo 0
    // if(processRank != 0){
        float maxNum = maxNumArray(biggerNums, sizeBiggerNumsArray);
        
    //     MPI_Send(&maxNum, 1, MPI_FLOAT, 0, 0, MPI_COMM_WORLD);
    // }

    // MPI_Barrier(MPI_COMM_WORLD);

    float finalMaxNum;

    MPI_Reduce(&maxNum, &finalMaxNum, clusterSize, MPI_FLOAT, MPI_MAX, 0, MPI_COMM_WORLD);


    if(processRank == 0){
        // float maxNums[clusterSize];

        // maxNums[0] = maxNumArray(biggerNums, sizeBiggerNumsArray);

        // for(int i = 1; i < clusterSize; i++){
        //     MPI_Recv(&maxNums[i], 1, MPI_FLOAT, i, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        // }

        // cout << "Array de maiores valores: " << endl;

        // printArray(maxNums, clusterSize);

        // cout << "Maior valor de TODOS OS MUNDOS DO UNIVERSO!!!: " << maxNumArray(maxNums, clusterSize) << endl;

        cout << "Maior valor de TODOS OS MUNDOS DO UNIVERSO!!!: " << finalMaxNum << endl;

        totalTime = MPI_Wtime() - startTime;

        cout << "Tempo total da solução: " << setprecision(5) << totalTime << endl;
    }

    MPI_Finalize();
    return 0;
}