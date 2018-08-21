grammar LA;

@header{
    package t1;
}

// GRAMÁTICA LINGUAGEM LA

programa : declaracoes 'algoritmo' corpo 'fim_algoritmo' ;

declaracoes : decl_local_global*;

decl_local_global : decl_local | decl_global;

decl_local : 'declare' variavel 
    | 'constante' ident1=IDENT ':' tipo_basico '=' valor_constante
    | 'tipo' ident2=IDENT ':' tipo;

variavel : id=identificador (',' outrosIds+=identificador)* ':' tipo;

identificador : ident=IDENT ('.' outrosIdents+=IDENT)* dimensao;

dimensao : ('[' exp_aritmetica ']')*; 

tipo : registro | tipo_estendido;

tipo_basico : 'literal' | 'inteiro' | 'real' | 'logico';

tipo_basico_ident : tipo_basico | IDENT;

tipo_estendido : '^'? tipo_basico_ident;

valor_constante : CADEIA | NUM_INT | NUM_REAL | 'verdadeiro' | 'falso';

registro : 'registro' variavel*  'fim_registro';

decl_global : 'procedimento' IDENT '(' parametros? ')' decl_local* cmd* 'fim_procedimento'
    | 'funcao' IDENT '(' parametros? ')' ':' tipo_estendido decl_local* cmd* 'fim_funcao';

parametro : 'var'? identificador (',' identificador)* ':' tipo_estendido;

parametros : parametro (',' parametro)*;

corpo : decl_local* cmd*;

cmd : cmdLeia | cmdEscreva | cmdSe | cmdCaso | cmdPara | cmdEnquanto | cmdFaca 
    | cmdAtribuicao | cmdChamada | cmdRetorne;

cmdLeia : 'leia' '(' '^'? identificador (',' '^'? identificador)* ')';

cmdEscreva : 'escreva' '(' expressao (',' expressao)* ')';

cmdSe : 'se' expressao 'entao' cmd* ('senao' cmd*)? 'fim_se';

cmdCaso : 'caso' exp_aritmetica 'seja' selecao ('senao' cmd*)? 'fim_caso';

cmdPara : 'para' IDENT '<-' exp_aritmetica 'ate' exp_aritmetica 'faca' cmd* 'fim_para';

cmdEnquanto : 'enquanto' expressao 'faca' cmd* 'fim_enquanto';

cmdFaca : 'faca' cmd* 'ate' expressao;

cmdAtribuicao : '^'? identificador '<-' expressao;

cmdChamada : IDENT '(' expressao (',' expressao)* ')';

cmdRetorne : 'retorne' expressao;

selecao : item_selecao*;

item_selecao : constantes ':' cmd*;

constantes : numero_intervalo (',' numero_intervalo)*;

numero_intervalo : op_unario? NUM_INT ('..'  op_unario? NUM_INT)?;

op_unario : '-';

exp_aritmetica : termo (op1 termo)*;

termo : fator (op2 fator)*;

fator : parcela (op3 parcela)*; 

op1 : '+' | '-';

op2 : '*' | '/';

op3 : '%';

parcela : op_unario? parcela_unario | parcela_nao_unario;

parcela_unario : '^'? identificador
    | IDENT '(' expressao (',' expressao)* ')'
    | NUM_INT
    | NUM_REAL
    | '(' expressao ')'
    ;

parcela_nao_unario : '&' identificador | CADEIA;

exp_relacional : exp_aritmetica (op_relacional exp_aritmetica)?;

op_relacional : '=' | '<>' | '>=' | '<=' | '>'| '<';

expressao : termo_logico (op_logico1 termo_logico)*; 

termo_logico : fator_logico (op_logico2 fator_logico)*;

fator_logico : 'nao'? parcela_logica;

parcela_logica : 'verdadeiro' | 'falso' | exp_relacional;

op_logico1 : 'ou';

op_logico2 : 'e';

fragment
DIGITO : '0'..'9';

IDENT : ('a'..'z' | 'A'..'Z' | '_') ('a'..'z' | 'A'..'Z' | '_' | DIGITO)*;

CADEIA : '"' ~('\n' | '\r' | '"')* '"';

NUM_INT : DIGITO+;

NUM_REAL : DIGITO+ '.' DIGITO+;

COMENTARIO : '{' ~('}')* '}' -> skip;

ESPACOS_EM_BRANCO : (' ' | '\t' | '\r' | '\n') -> skip;

CARACTERE_ERRADO : . ;