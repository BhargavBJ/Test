#include<stdio.h>
#include<omp.h>
#include<stdlib.h>
#include<time.h>
#define size 100000
void merge(int arr[], int l, int m, int r)
{
    int i,j,k;
    int n1 = m-l+1;
    int n2 = r-m;
    int *L = (int*)malloc(n1*sizeof(int));
    int *R = (int*)malloc(n2*sizeof(int));
    for(int i=0;i<n1;i++)
    L[i] = arr[l+i];
    for(int j=0;j<n2;j++)
    R[j]=arr[m+j+1];
    i = 0;
    j = 0;
    k = l;
    while(i<n1 && j<n2)
    arr[k++] = (L[i] <= R[j]) ? L[i++] : R[j++];
    while(i<n1)arr[k++]=L[i++];
    while(j<n2)arr[k++]=R[j++];
    free(L);
    free(R);
}
void mergeSeq(int arr[], int l, int r)
{
    if(l<r)
    {
        int m=(l+r)/2;
        mergeSeq(arr,l,m);
        mergeSeq(arr,m+1,r);
        merge(arr,l,m,r);
    }
}
void mergePar(int arr[], int l, int r)
{
    if (r - l < size)
        mergeSeq(arr, l, r);
    else if (l < r)
    {
        int m = (l + r) / 2;
        #pragma omp parallel sections
        {
            #pragma omp section
            mergePar(arr, l, m);
            #pragma omp section
            mergePar(arr, m + 1, r);
        }
        merge(arr, l, m, r);
    }
}
void fillArray(int arr[],int s)
{
    for(int i=0;i<s;i++)
    arr[i] = rand() % 100000;
}
void copyArray(int src[], int dest[], int s)
{
    for(int i=0;i<s;i++)
    dest[i]=src[i];
}
int main()
{
    int *a1 = (int*)malloc(size*sizeof(int));
    int *a2 = (int*)malloc(size*sizeof(int));
    double start,end;
    fillArray(a1,size);
    copyArray(a1,a2,size);
    start = omp_get_wtime();
    mergeSeq(a1,0,size-1);
    end = omp_get_wtime();
    printf("Sequential Merge Sort: %.6f s \n",end-start);
    start = omp_get_wtime();
    mergePar(a2,0,size-1);
    end = omp_get_wtime();
    printf("Parallel Merge Sort: %.6f s\n",end-start);
    free(a1);
    free(a2);
    return 0;
}
