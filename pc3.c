#include <stdio.h>
#include <omp.h>

int fib(int n)
{
    int x, y;
    if (n < 2)
        return n;
    #pragma omp task shared(x)
    x = fib(n - 1);
    #pragma omp task shared(y)
    y = fib(n - 2);
    #pragma omp taskwait
    return x + y;
}

int main()
{
    int n, res;
    printf("Enter n: ");
    scanf("%d", &n);
    int i;
    #pragma omp parallel
    {
        #pragma omp single
        {
            for (i = 0; i < n; i++)
            {
                res = fib(i);
                printf("Fib(%d) = %d\n", i, res);
            }
        }
    }
    return 0;
}
