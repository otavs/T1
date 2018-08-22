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
            visitVariavel(ctx.variavel());
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
    
    @Override
    public Void visitTipo(LAParser.TipoContext ctx){
        if(ctx.registro() != null){
            visitRegistro(ctx.registro());
        }
        else{
            visitTipo_estendido(ctx.tipo_estendido());
        }
        return null;
    }
    
    @Override
    public Void visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx){
        if(ctx.tipo_basico() != null){
            visitTipo_basico(ctx.tipo_basico());
        }
        else{
            //IDENT
        }
        return null;
    }
    
    @Override
    public Void visitTipo_estendido (LAParser.Tipo_estendidoContext ctx){
        visitTipo_basico_ident(ctx.tipo_basico_ident());
        return null;
    }
    
    @Override
    public Void visitRegistro (LAParser.RegistroContext ctx){
        for(LAParser.VariavelContext var : ctx.variavel()){
            visitVariavel(var);
        }
        return null;
    }
    
    @Override
    public Void visitDecl_global (LAParser.Decl_globalContext ctx){
        if(ctx.ident1 != null){
            //ident1
            if(ctx.params1 != null) visitParametros(ctx.parametros());
            for(LAParser.Decl_localContext decl : ctx.decl_local()){
                visitDecl_local(decl);
            }
            for(LAParser.CmdContext cmd : ctx.cmd()){
                visitCmd(cmd);
            }
        }else {
            //ident2
            if(ctx.params2 != null) visitParametros(ctx.parametros());
            visitTipo_estendido(ctx.tipo_estendido());
            for(LAParser.Decl_localContext decl : ctx.decl_local()){
                visitDecl_local(decl);
            }
            for(LAParser.CmdContext cmd : ctx.cmd()){
                visitCmd(cmd);
            }
        }
        return null;
    }
    
    @Override
    public Void visitParametro (LAParser.ParametroContext ctx){
        visitIdentificador(ctx.id1);
        for(LAParser.IdentificadorContext id : ctx.id2){
            visitIdentificador(id);
        }
        visitTipo_estendido(ctx.tipo_estendido());
        return null;
    }
    
    @Override
    public Void visitParametros (LAParser.ParametrosContext ctx){
        visitParametro(ctx.param1);
        for(LAParser.ParametroContext param : ctx.param2){
            visitParametro(param);
        }
        return null;
    }
    
    @Override
    public Void visitCorpo (LAParser.CorpoContext ctx){
        for(LAParser.Decl_localContext decl : ctx.decl_local()){
                visitDecl_local(decl);
            }
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return null;
    }
    
    @Override
    public Void visitCmd (LAParser.CmdContext ctx){
        if (ctx.cmdLeia() != null){
            visitCmdLeia(ctx.cmdLeia());
        }else if (ctx.cmdEscreva() != null){
            visitCmdEscreva(ctx.cmdEscreva());
        }else if (ctx.cmdSe() != null){
            visitCmdSe(ctx.cmdSe());
        }else if (ctx.cmdCaso() != null){
            visitCmdCaso(ctx.cmdCaso());
        }else if (ctx.cmdPara() != null){
            visitCmdPara(ctx.cmdPara());
        }else if (ctx.cmdEnquanto() != null){
            visitCmdEnquanto(ctx.cmdEnquanto());
        }else if (ctx.cmdFaca() != null){
            visitCmdFaca(ctx.cmdFaca());
        }else if (ctx.cmdAtribuicao() != null){
            visitCmdAtribuicao(ctx.cmdAtribuicao());
        }else if (ctx.cmdChamada() != null){
            visitCmdChamada(ctx.cmdChamada());
        }else if (ctx.cmdRetorne() != null){
            visitCmdRetorne(ctx.cmdRetorne());
        }
        return null;
    }
    @Override
    public Void visitCmdLeia(LAParser.CmdLeiaContext ctx){
        visitIdentificador(ctx.id1);
        for(LAParser.IdentificadorContext id : ctx.id2){
            visitIdentificador(id);
        }
        return null;
    }
    
    @Override
    public Void visitCmdEscreva(LAParser.CmdEscrevaContext ctx){
        visitExpressao(ctx.exp1);
        for(LAParser.ExpressaoContext exp : ctx.exp2){
            visitExpressao(exp);
        }
        return null;
    }
    
    @Override
    public Void visitCmdSe(LAParser.CmdSeContext ctx){
        visitExpressao(ctx.e1);
        for(LAParser.CmdContext cmd : ctx.c1){
            visitCmd(cmd);
        }
        if (ctx.c2 != null) {
            for(LAParser.CmdContext cmd : ctx.c2){
                visitCmd(cmd);
            }
        }
        return null;
    }
    
    @Override
    public Void visitCmdCaso (LAParser.CmdCasoContext ctx){
        visitExp_aritmetica(ctx.exp_aritmetica());
        visitSelecao(ctx.selecao());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return null;
    }
    
    @Override
    public Void visitCmdPara (LAParser.CmdParaContext ctx){
        //IDENT
        visitExp_aritmetica(ctx.ea1);
        visitExp_aritmetica(ctx.ea2);
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return null;
    }
    
    @Override
    public Void visitCmdEnquanto (LAParser.CmdEnquantoContext ctx){
        visitExpressao(ctx.expressao());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return null;
    }
    
    @Override
    public Void visitCmdFaca (LAParser.CmdFacaContext ctx){
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        visitExpressao(ctx.expressao());
        return null;
    }
    
    @Override
    public Void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx){
        visitIdentificador(ctx.identificador());
        visitExpressao(ctx.expressao());
        return null;
    }
    
    @Override
    public Void visitCmdChamada(LAParser.CmdChamadaContext ctx){
        visitExpressao(ctx.exp);
        for(LAParser.ExpressaoContext exp : ctx.outrasExp){
            visitExpressao(exp);
        }
        return null;
    }
    
    @Override
    public Void visitCmdRetorne(LAParser.CmdRetorneContext ctx){
        visitExpressao(ctx.expressao());
        return null;
    }
    
    @Override
    public Void visitSelecao(LAParser.SelecaoContext ctx){
        for(LAParser.Item_selecaoContext exp : ctx.item_selecao()){
            visitItem_selecao(exp);
        }
        return null;
    }
    
    @Override
    public Void visitItem_selecao(LAParser.Item_selecaoContext ctx){
        visitConstantes(ctx.constantes());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return null;
    }
    
    @Override
    public Void visitConstantes(LAParser.ConstantesContext ctx){
        visitNumero_intervalo(ctx.ni1);
        for(LAParser.Numero_intervaloContext ni : ctx.ni2){
            visitNumero_intervalo(ni);
        }
        return null;
    }
    
    @Override
    public Void visitNumero_intervalo(LAParser.Numero_intervaloContext ctx){
        if(ctx.opu1 != null) visitOp_unario(ctx.opu1);
        //ni1
        if(ctx.opu2 != null){
            visitOp_unario(ctx.opu2);
            //ni2
        }        
        return null;
    }
    
    @Override
    public Void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
        visitTermo(ctx.t1);
        //op1
        for(LAParser.TermoContext t : ctx.t2){
            visitTermo(t);
        }       
        return null;
    }
    
    @Override
    public Void visitTermo(LAParser.TermoContext ctx){
        visitFator(ctx.f1);
        //op2
        for(LAParser.FatorContext f : ctx.f2){
            visitFator(f);
        }       
        return null;
    }
    
    @Override
    public Void visitFator(LAParser.FatorContext ctx){
        visitParcela(ctx.p1);
        //op3
        for(LAParser.ParcelaContext p : ctx.p2){
            visitParcela(p);
        }       
        return null;
    }

    @Override
    public Void visitParcela (LAParser.ParcelaContext ctx){
        if(ctx.op_unario() != null) visitOp_unario(ctx.op_unario());
        if(ctx.parcela_unario() != null) visitParcela_unario(ctx.parcela_unario());
        else visitParcela_nao_unario(ctx.parcela_nao_unario());
        return null;
    }
    
    @Override
    public Void visitParcela_unario (LAParser.Parcela_unarioContext ctx){
        if(ctx.identificador() != null) visitIdentificador(ctx.identificador());
        else if(ctx.e1 != null){ 
            visitExpressao(ctx.e1);
            for(LAParser.ExpressaoContext e : ctx.e2){
                visitExpressao(e);
            }
        }else if(ctx.e3 != null) visitExpressao(ctx.e3);
        return null;
    }
    
    @Override
    public Void visitParcela_nao_unario (LAParser.Parcela_nao_unarioContext ctx){
        if(ctx.identificador() != null) visitIdentificador(ctx.identificador());
        else {/* CADEIA */}
        return null;
    }
    
    @Override
    public Void visitExp_relacional (LAParser.Exp_relacionalContext ctx){
        visitExp_aritmetica(ctx.e1);
        if(ctx.op_relacional() != null){ 
            visitOp_relacional(ctx.op_relacional());
            visitExp_aritmetica(ctx.e2);
        }
        return null;
    }

    @Override
    public Void visitExpressao(LAParser.ExpressaoContext ctx){
        visitTermo_logico(ctx.t1);
        //op_logico1
        for(LAParser.Termo_logicoContext tl : ctx.t2){
            visitTermo_logico(tl);
        }
        return null;
    }
    
    @Override
    public Void visitTermo_logico(LAParser.Termo_logicoContext ctx){
        visitFator_logico(ctx.f1);
        //op_logico2
        for(LAParser.Fator_logicoContext fl : ctx.f2){
            visitFator_logico(fl);
        } 
        return null;
    }
    
    @Override
    public Void visitFator_logico(LAParser.Fator_logicoContext ctx){
        visitParcela_logica(ctx.parcela_logica());
        return null;
    }
}


/*
@Override
    public Void visitNOMEAQUI (LAParser.nomeContext ctx){
        //código
        return null;
    }
*/
