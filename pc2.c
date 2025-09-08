#include<stdio.h>
#include<omp.h>
int main()
{
  int n;
  printf("Enter no. of iterations:");
  scanf("%d",&n);
  #pragma parallel for schedule(static,4)
  int i;
  for(i=0;i<n;i++)
  {
    int id = omp_get_thread_num();
    printf("Thread %d: Iteration %d\n",id,i);
  }
  return 0;
}
