package t1;

import org.antlr.v4.runtime.Token;

public class LAVisitorGerador{
    
    public void visitPrograma(LAParser.ProgramaContext ctx){
        visitDeclaracoes(ctx.declaracoes());
        visitCorpo(ctx.corpo());
    }
    
    public void visitDeclaracoes(LAParser.DeclaracoesContext ctx){
        for(LAParser.Decl_local_globalContext decl : ctx.decl_local_global()){
            visitDecl_local_global(decl);
        }
    }
    
    public void visitDecl_local_global(LAParser.Decl_local_globalContext ctx){
        if(ctx.decl_local() != null){
            visitDecl_local(ctx.decl_local());
        }
        else{
            visitDecl_global(ctx.decl_global());
        }
    }
    
    public void visitDecl_local(LAParser.Decl_localContext ctx){
        if(ctx.variavel() != null){
            visitVariavel(ctx.variavel());
        }
        else if(ctx.id1 != null){
            // id1
            visitTipo_basico(ctx.tipo_basico());
        }
        else{
            // id2
            visitTipo(ctx.tipo());
        }
        
    }
    
    public void visitVariavel(LAParser.VariavelContext ctx){
        visitIdentificador(ctx.id);
        for(LAParser.IdentificadorContext id : ctx.outrosIds){
            visitIdentificador(id);
        }
        visitTipo(ctx.tipo());
    }
    
    public void visitIdentificador(LAParser.IdentificadorContext ctx) {
        // id
        for(Token id : ctx.outrosIds){
            // id
        }
        visitDimensao(ctx.dimensao());
    }
    
    public void visitDimensao(LAParser.DimensaoContext ctx){
        for(LAParser.Exp_aritmeticaContext exp : ctx.exp_aritmetica()){
            visitExp_aritmetica(exp);
        }
    }
    
    public void visitTipo(LAParser.TipoContext ctx){
        if(ctx.registro() != null){
            visitRegistro(ctx.registro());
        }
        else{
            visitTipo_estendido(ctx.tipo_estendido());
        }
    }
    
    public void visitTipo_basico(LAParser.Tipo_basicoContext ctx){
        // int, double...
    }
    
    public void visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx){
        if(ctx.tipo_basico() != null){
            visitTipo_basico(ctx.tipo_basico());
        }
        else{
            // IDENT
        }
    }
    
    public void visitTipo_estendido(LAParser.Tipo_estendidoContext ctx){
        visitTipo_basico_ident(ctx.tipo_basico_ident());
    }
    
    public void visitValor_constante(LAParser.Valor_constanteContext ctx){
        
    }
    
    public void visitRegistro(LAParser.RegistroContext ctx){
        for(LAParser.VariavelContext var : ctx.variavel()){    
            visitVariavel(var);
        }
    }
    
    public void visitDecl_global(LAParser.Decl_globalContext ctx){
        // Procedimento
        if(ctx.ident1 != null){
            // ident1
            if(ctx.params1 != null){
                visitParametros(ctx.params1);
            }
            for(LAParser.Decl_localContext decl : ctx.decl1){
                visitDecl_local(decl);
            }
            for(LAParser.CmdContext cmd : ctx.c1){
                visitCmd(cmd);
            }
        }
        // Função
        else{
            // ident2
            if(ctx.params2 != null){
                visitParametros(ctx.params2);
            }
            visitTipo_estendido(ctx.tipo_estendido());
            for(LAParser.Decl_localContext decl : ctx.decl2){
                visitDecl_local(decl);
            }
            for(LAParser.CmdContext cmd : ctx.c2){
                visitCmd(cmd);
            }
        }
    }
    
    public void visitParametro(LAParser.ParametroContext ctx){
        // id1
        for(LAParser.IdentificadorContext id : ctx.id2){
            // id
        }
        visitTipo_estendido(ctx.tipo_estendido());
    }
    
    public void visitParametros(LAParser.ParametrosContext ctx){
        visitParametro(ctx.param1);
        for(LAParser.ParametroContext param : ctx.param2){
            visitParametro(param);
        }
    }
    
    public void visitCorpo(LAParser.CorpoContext ctx){
        for(LAParser.Decl_localContext decl : ctx.decl_local()){
            visitDecl_local(decl);
        }
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
    }
    
    public void visitCmd(LAParser.CmdContext ctx){
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
    }
    
    public void visitCmdLeia(LAParser.CmdLeiaContext ctx){
        visitIdentificador(ctx.id1);
        for(LAParser.IdentificadorContext id : ctx.id2){
            visitIdentificador(id);
        }
    }
    
    public void visitCmdEscreva(LAParser.CmdEscrevaContext ctx){
        visitExpressao(ctx.exp1);
        for(LAParser.ExpressaoContext exp : ctx.exp2){
            visitExpressao(exp);
        }
    }
    
    public void visitCmdSe(LAParser.CmdSeContext ctx){
        visitExpressao(ctx.e1);
        for(LAParser.CmdContext cmd : ctx.c1){
            visitCmd(cmd);
        }
        if(ctx.c2 != null) {
            for(LAParser.CmdContext cmd : ctx.c2){
                visitCmd(cmd);
            }
        }
    }
    
    public void visitCmdCaso (LAParser.CmdCasoContext ctx){
        visitExp_aritmetica(ctx.exp_aritmetica());
        visitSelecao(ctx.selecao());
        if(ctx.cmd() != null){
            for(LAParser.CmdContext cmd : ctx.cmd()){
                visitCmd(cmd);
            }   
        }
    }
    
    public void visitCmdPara (LAParser.CmdParaContext ctx){
        visitExp_aritmetica(ctx.ea1);
        visitExp_aritmetica(ctx.ea2);
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }    
    }
    
    public void visitCmdEnquanto (LAParser.CmdEnquantoContext ctx){
        visitExpressao(ctx.expressao());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
    }
    
    public void visitCmdFaca (LAParser.CmdFacaContext ctx){
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        visitExpressao(ctx.expressao());
    }
    
    public void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx){
        // ponteiro
        visitIdentificador(ctx.identificador());
        visitExpressao(ctx.expressao());
    }
    
    public void visitCmdChamada(LAParser.CmdChamadaContext ctx){
        // IDENT
        visitExpressao(ctx.exp);
        for(LAParser.ExpressaoContext exp : ctx.outrasExp){
            visitExpressao(exp);
        }
    }
    
    public void visitCmdRetorne(LAParser.CmdRetorneContext ctx){
        visitExpressao(ctx.expressao());
    }
    
    public void visitSelecao(LAParser.SelecaoContext ctx){
        for(LAParser.Item_selecaoContext item : ctx.item_selecao()){
            visitItem_selecao(item);
        }
    }
    
    public void visitItem_selecao(LAParser.Item_selecaoContext ctx){
        visitConstantes(ctx.constantes());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
    }
    
    public void visitConstantes(LAParser.ConstantesContext ctx){
        visitNumero_intervalo(ctx.ni1);
        for(LAParser.Numero_intervaloContext ni : ctx.ni2){
            visitNumero_intervalo(ni);
        }
    }
    
    public void visitNumero_intervalo(LAParser.Numero_intervaloContext ctx){
        if(ctx.opu1 != null){
            // opu1
        }
        // ni1
        if(ctx.ni2 != null){
            if(ctx.opu2 != null){
                // opu2
            }
            // ni2
        }
    }
    
    public void visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
        visitTermo(ctx.t1);
        for(LAParser.TermoContext t : ctx.t2){
            // op1
            visitTermo(t);
        }       
    }
    
    public void visitTermo(LAParser.TermoContext ctx){
        visitFator(ctx.f1);
        for(LAParser.FatorContext f : ctx.f2){
            // op2
            visitFator(f);
        }       
    }
    
    public void visitFator(LAParser.FatorContext ctx){
        visitParcela(ctx.p1);
        for(LAParser.ParcelaContext p : ctx.p2){
            // op3
            visitParcela(p);
        }       
        
    }

    public void visitParcela (LAParser.ParcelaContext ctx){
        if(ctx.parcela_unario() != null){
            // op_unario
            visitParcela_unario(ctx.parcela_unario());
        }
        else{
            visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
    }
    
    public void visitParcela_unario (LAParser.Parcela_unarioContext ctx){
        if(ctx.identificador() != null) {
            visitIdentificador(ctx.identificador());
        }
        else if(ctx.IDENT() != null){
            // IDENT
            visitExpressao(ctx.e1);         
            for(LAParser.ExpressaoContext e : ctx.e2){
                visitExpressao(e);
            }
        }
        else if(ctx.NUM_INT() != null){
            // NUM_INT
        }
        else if(ctx.NUM_REAL() != null){
            // NUM_REAL
        }
        else{
            visitExpressao(ctx.e3);
        }
    }
    
    public void visitParcela_nao_unario (LAParser.Parcela_nao_unarioContext ctx){
        if(ctx.identificador() != null){
            // '&'
            visitIdentificador(ctx.identificador());
        }
        else{
            // CADEIA
        }
    }
    
    public void visitExp_relacional (LAParser.Exp_relacionalContext ctx){
        visitExp_aritmetica(ctx.e1);
        if(ctx.op_relacional() != null){
            // op_relacional
            visitExp_aritmetica(ctx.e2);
        }
    }

    public void visitExpressao(LAParser.ExpressaoContext ctx){
        visitTermo_logico(ctx.t1);
        for(LAParser.Termo_logicoContext t : ctx.t2){
            // op_logico1
            visitTermo_logico(t);
        }
    }
    
    public void visitTermo_logico(LAParser.Termo_logicoContext ctx){
        visitFator_logico(ctx.f1);
        for(LAParser.Fator_logicoContext f : ctx.f2){
            // op_logico2
            visitFator_logico(f);
        } 
    }
    
    public void visitFator_logico(LAParser.Fator_logicoContext ctx){
        if(ctx.nao != null){
            // 'nao'
        }
        visitParcela_logica(ctx.parcela_logica());
    }
    
    public void visitParcela_logica(LAParser.Parcela_logicaContext ctx){
        if(ctx.exp_relacional() != null){
            visitExp_relacional(ctx.exp_relacional());
        }
        else{
            // 'verdadeiro' / 'falso'
        }
    }
}