#include <mpi.h>
#include <iostream>
#include <cstdio>
#include <ostream>

using namespace std;

int main(int argc, char** argv){
    int process_Rank, size_Of_Cluster;

    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size_Of_Cluster);
    MPI_Comm_rank(MPI_COMM_WORLD, &process_Rank);

    int n = 3;
    int buffer[n] = {0};

    if(process_Rank == 1){

	    buffer[0] = 1;
	    buffer[1] = 2;
	    buffer[2] = 3;

	    cout << "Rank " << process_Rank << " enviando processo 1" << endl;
	    MPI_Send(&buffer, n, MPI_INT, 1, 7, MPI_COMM_WORLD);

	    cout << "Rank " << process_Rank << " enviando processo 2" << endl;
	    MPI_Send(&buffer, n, MPI_INT, 2, 7, MPI_COMM_WORLD);

    }else if(process_Rank < 3){

	    MPI_Recv(&buffer, n, MPI_INT, 0, 7, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
	    for(int i = 0; i < n; i++){
	    	cout << "Rank " << process_Rank << " indice " << i << " valor " << buffer[i] << endl; 
	    }
	    // cout << "Recebi algo!" << endl;
    }else{
	    cout << "Não recebi nada!" << endl;
    }

    // printf("Hello World from process %d of %d\n", process_Rank, size_Of_Cluster)
    // cout << "Hello from process " << process_Rank << " of " << size_Of_Cluster << endl;

    MPI_Finalize();
    return 0;
}
