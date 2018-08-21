package t1;

public class LAVisitorSemantico extends LABaseVisitor<Boolean> {
    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();
    
    @Override
    public Boolean visitPrograma(LAParser.ProgramaContext ctx) {
        Saida.println("ola");
        return visitDeclaracoes(ctx.declaracoes());
    }
    
    
    
}
