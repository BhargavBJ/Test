#include <stdio.h>
#include <stdlib.h>
#include <math.h>
#include <omp.h>
int isPrime(int num) {
    if (num < 2) return 0;
    if (num == 2) return 1;
    if (num % 2 == 0) return 0;

    int limit = (int)sqrt(num);
    for (int i = 3; i <= limit; i += 2) {
        if (num % i == 0)
            return 0;
    }
    return 1;
}

int main() {
    int n;
    printf("Enter value of n: ");
    scanf("%d", &n);
    double start_t, end_t;

    // Serial Execution
    start_t = omp_get_wtime();
    int serial_count = 0;
    for (int i = 2; i <= n; i++) {
        if (isPrime(i))
            serial_count++;
    }
    end_t = omp_get_wtime();
    printf("Serial Execution time: %f seconds\n", end_t - start_t);

    // Parallel Execution
    omp_set_num_threads(4);
    int *prime_flags = (int *)calloc(n + 1, sizeof(int));
    start_t = omp_get_wtime();
    #pragma omp parallel for schedule(dynamic,100)
    for (int i = 2; i <= n; i++) {
        if (isPrime(i))
            prime_flags[i] = 1;
    }
    end_t = omp_get_wtime();
    int parallel_count = 0;
    for (int i = 2; i <= n; i++) {
        if (prime_flags[i])
            parallel_count++;
    }
    printf("Parallel Execution time: %f seconds\n", end_t - start_t);
    printf("Primes found (Serial): %d  |  (Parallel): %d\n", serial_count, parallel_count);
    free(prime_flags);
    return 0;
}
