#include<stdio.h>
#include<omp.h>
void main()
{
    int n;
    printf("Enter the number of itreations:");
    scanf("%d",&n);
    #pragma omp parallel  for schedule(static,4)
    for(int i =0;i<=n;i++)
    {
        int tid = omp_get_thread_num();
        printf("Thread: %d, Iteration: %d \n",tid,i);
    }
}
