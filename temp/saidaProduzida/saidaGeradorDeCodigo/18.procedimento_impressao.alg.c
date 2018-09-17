#include <stdio.h>
#include <stdlib.h>
#include <string.h>
void proc_imprime(char *mensagem){
printf("%s%s", mensagem, "\n");
}
int main(){
proc_imprime("teste");
return 0;
}
