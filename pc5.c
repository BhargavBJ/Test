#include <stdio.h>
#include <mpi.h>
#include<string.h>
int main(int argc, char **argv)
{
    int size, rank;
    MPI_Status status;
    MPI_Init(&argc, &argv);
    MPI_Comm_size(MPI_COMM_WORLD, &size);
    MPI_Comm_rank(MPI_COMM_WORLD, &rank);
    if(rank == 0)
    {
      for(int i=1; i<size; i++)
      {
        char message[100];
        printf(message,"Hello from Process 0 to %d",i);
        MPI_Send(message, strlen(message)+1, MPI_CHAR, i, 0, MPI_COMM_WORLD);
        printf("Process 0 sent message to process %d \n",i);
      }
    }
    else
    {
      char recv_buff[100];
      MPI_Recv(recv_buff,100, MPI_CHAR, 0, 0, MPI_COMM_WORLD, &status);
      printf("Process %d recieved message %s\n",rank, recv_buff);
    }
    MPI_Finalize();
    return 0;
  }
