#include <stdio.h>
#include <mpi.h>
int main(int argc, char **argv)
{
    int size, rank;
    int send_data, recv_data;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    if (rank == 0)
    {
        send_data = 100;
        MPI_Send(&send_data, 1, MPI_INT, 1, 0, MPI_COMM_WORLD);
        MPI_Recv(&recv_data, 1, MPI_INT, 1, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        printf("Process 0 recieved %d\n", recv_data);
    }
    else if (rank == 1)
    {
        send_data = 200;
        MPI_Send(&send_data, 1, MPI_INT, 0, 0, MPI_COMM_WORLD);
        MPI_Recv(&send_data, 1, MPI_INT, 0, 0, MPI_COMM_WORLD, MPI_STATUS_IGNORE);
        printf("Process 1 recieved %d\n", recv_data);
    }
    MPI_Finalize();
    return 0;
}
//mpicc pc6.c -o pc6
//mpirun -oversubscribe -np 2 ./pc6
