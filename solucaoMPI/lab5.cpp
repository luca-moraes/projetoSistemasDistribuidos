#include <mpi.h>
#include <iostream>
#include <cstdio>
#include <ostream>

using namespace std;

int main(int argc, char** argv){
    int processRank, clusterSize;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &clusterSize);
    MPI_Comm_rank(MPI_COMM_WORLD, &processRank);

    int n = 3;
    int buffer[n] = {0};

    if(processRank == 1){

	    buffer[0] = 1;
	    buffer[1] = 2;
	    buffer[2] = 3;

	    cout << "Rank " << processRank << " enviando processo 1" << endl;
	    MPI_Send(&buffer, n, MPI_INT, 1, 7, MPI_COMM_WORLD);

	    cout << "Rank " << processRank << " enviando processo 2" << endl;
	    MPI_Send(&buffer, n, MPI_INT, 2, 7, MPI_COMM_WORLD);

    }else if(processRank < 3){

	    MPI_Recv(&buffer, n, MPI_INT, 0, 7, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	    for(int i = 0; i < n; i++){
	    	cout << "Rank " << processRank << " indice " << i << " valor " << buffer[i] << endl; 
	    }
    }else{
	    cout << "Não recebi nada!" << endl;
    }

    MPI_Finalize();
    return 0;
}
