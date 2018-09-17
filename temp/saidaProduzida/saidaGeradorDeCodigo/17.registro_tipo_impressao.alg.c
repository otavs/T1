#include <stdio.h>
#include <stdlib.h>
#include <string.h>
int main(){
typedef struct {
char nome[1000];
int idade;
} treg;
treg reg;
strcpy(reg.nome, "Maria");
reg.idade = 30;
printf("%s%s%d%s", reg.nome, " tem ", reg.idade, " anos");
return 0;
}
