package t1;

import org.antlr.v4.runtime.Token;

public class LAVisitorSemantico extends LABaseVisitor<Void> {
    // PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();
    
    @Override
    public Void visitPrograma(LAParser.ProgramaContext ctx) {
        // pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));
        visitDeclaracoes(ctx.declaracoes());
        visitCorpo(ctx.corpo());
        return null;
    }
    
    @Override
    public Void visitDeclaracoes(LAParser.DeclaracoesContext ctx) {
        for(LAParser.Decl_local_globalContext decl : ctx.decl_local_global()){
            visitDecl_local_global(decl);
        }
        return null;
    }
    
    @Override
    public Void visitDecl_local_global(LAParser.Decl_local_globalContext ctx) {
        if(ctx.decl_local() != null){
            visitDecl_local(ctx.decl_local());
        }
        else{
            visitDecl_global(ctx.decl_global());
        }
        return null;
    }
    
    @Override
    public Void visitDecl_local(LAParser.Decl_localContext ctx) {
        if(ctx.variavel() != null){
            
        }
        else if(ctx.ident1 != null){
            // verifica ident1 na tabela de simbolos
            visitTipo_basico(ctx.tipo_basico());
            visitValor_constante(ctx.valor_constante());
        }
        else{
            // verifica ident2
            visitTipo(ctx.tipo());
        }
        return null;
    }
    
    @Override
    public Void visitVariavel(LAParser.VariavelContext ctx) {
        visitIdentificador(ctx.id);
        for(LAParser.IdentificadorContext id : ctx.outrosIds){
            visitIdentificador(id);
        }
        visitTipo(ctx.tipo());
        return null;
    }
    
    @Override
    public Void visitIdentificador(LAParser.IdentificadorContext ctx) {
        // verifica ident
        for(Token ident : ctx.outrosIdents){
            // verifica ident
        }
        return null;
    }
    
    @Override
    public Void visitDimensao(LAParser.DimensaoContext ctx) {
        for(LAParser.Exp_aritmeticaContext exp : ctx.exp_aritmetica()){
            visitExp_aritmetica(exp);
        }
        return null;
    }
    
    
}
