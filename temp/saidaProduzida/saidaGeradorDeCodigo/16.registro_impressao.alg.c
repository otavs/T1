#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int main(){
struct {
char nome[1000];
int idade;
} reg;
strcpy(reg.nome, "Maria");
reg.idade = 24;
printf("%s%s%d%s", reg.nome, " tem ", reg.idade, " anos");
return 0;
}
