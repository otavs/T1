#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int main(){
int i;
i = 1;
do{
printf("%d%s", i, "\n");
i = i+1;
} while(!(i==6));
return 0;
}
