#include <stdio.h>
#include <stdlib.h>
#include <omp.h>
#include <time.h>

#define SIZE 100000
#define THRESHOLD 1000

void merge(int arr[], int l, int m, int r) 
{
    int i, j, k;
    int n1 = m - l + 1;
    int n2 = r - m;
    int *L = (int *)malloc(n1 * sizeof(int));
    int *R = (int *)malloc(n2 * sizeof(int));
    for (i = 0; i < n1; i++) L[i] = arr[l + i];
    for (j = 0; j < n2; j++) R[j] = arr[m + 1 + j];
    i = 0; j = 0; k = l;
    while (i < n1 && j < n2) {
        arr[k++] = (L[i] < R[j]) ? L[i++] : R[j++];
    }
    while (i < n1) arr[k++] = L[i++];
    while (j < n2) arr[k++] = R[j++];
    free(L);
    free(R);
}

void mergeSortSeq(int arr[], int l, int r) 
{
    if (l < r) {
        int m = (l + r) / 2;
        mergeSortSeq(arr, l, m);
        mergeSortSeq(arr, m + 1, r);
        merge(arr, l, m, r);
    }   
}

void mergeSortParallel(int arr[], int l, int r) 
{
    if (r - l <= THRESHOLD) {
        mergeSortSeq(arr, l, r); 
    } else if (l < r) {
        int m = (l + r) / 2;
        #pragma omp parallel sections
        {
            #pragma omp section
            mergeSortParallel(arr, l, m);
            #pragma omp section
            mergeSortParallel(arr, m + 1, r);
        }
        merge(arr, l, m, r);
    }
}

void fillArray(int arr[], int size) 
{
    for (int i = 0; i < size; i++)
        arr[i] = rand() % 100;
}

void copyArray(int src[], int dest[], int size) 
{
    for (int i = 0; i < size; i++)
        dest[i] = src[i];
}

int main() 
{
    srand(time(NULL));
    int *a1 = (int *)malloc(SIZE * sizeof(int));
    int *a2 = (int *)malloc(SIZE * sizeof(int));
    double start, end;
    fillArray(a1, SIZE);
    copyArray(a1, a2, SIZE);
    start = omp_get_wtime();
    mergeSortSeq(a1, 0, SIZE - 1);
    end = omp_get_wtime();
    printf("Sequential Merge Sort Time: %6f seconds\n", end - start);
    start = omp_get_wtime();
    mergeSortParallel(a2, 0, SIZE - 1);
    end = omp_get_wtime();
    printf("Parallel Merge Sort Time: %6f seconds\n", end - start);
    free(a1);
    free(a2);
    return 0;
}
