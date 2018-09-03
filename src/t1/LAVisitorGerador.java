package t1;

import org.antlr.v4.runtime.Token;

public class LAVisitorGerador{
    
    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();
    
    public boolean isNumerico(String tipo){
        if(tipo.equals("inteiro") || tipo.equals("real")){
            return true;
        }
        return false;
    }
    
    public String tipoRetorno(String operacao, String op1, String op2){
        if(operacao.equals("aritmetica")){
            if(isNumerico(op1) && isNumerico(op2)){
                if(op1.equals("real") || op2.equals("real")) return "real";
                else return "inteiro";
            }
            else if(op1.equals("literal") && op2.equals("literal")){
                return "literal";
            }
            else{
                return "tipo_invalido";
            }
        }
        else if(operacao.equals("relacional")){
            if(isNumerico(op1) && isNumerico(op2)){
                return "logico";
            }
            else{
                return "tipo_invalido";
            }
        }
        else if(operacao.equals("logica")){
            if(op1.equals("logico") && op2.equals("logico")){
                return "logico";
            }
            else{
                return "tipo_invalido";
            }
        }
        return "";
    }
    
    public String parseTipo(String tipo){
        if(tipo.equals("inteiro") || tipo.equals("logico")) return "int";
        if(tipo.equals("real")) return "float";
        if(tipo.equals("literal")) return "char[60]";
        return tipo;
    }
    
    public String parseTipoFormat(String tipo){
        if(tipo.equals("inteiro") || tipo.equals("logico")) return "%d";
        if(tipo.equals("real")) return "%f";
        if(tipo.equals("literal")) return "%s";
        return tipo;
    }
    
    public void visitPrograma(LAParser.ProgramaContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));
        Saida.println("#include <stdio.h>");
        Saida.println("#include <stdlib.h>");
        visitDeclaracoes(ctx.declaracoes());
        Saida.println("int main(){");
        visitCorpo(ctx.corpo());
        Saida.println("return 0;");
        Saida.println("}");
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
            Saida.print(parseTipo(ctx.variavel().tipo().getText()) + " ");
            Saida.print(ctx.variavel().id.getText());
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.variavel().id.getText(), "variavel", ctx.variavel().tipo().getText());
            for(LAParser.IdentificadorContext id : ctx.variavel().outrosIds){
                Saida.print(", " + id.getText());
                pilhaDeTabelas.topo().adicionarSimbolo(id.getText(), "variavel", ctx.variavel().tipo().getText());
            }
            visitVariavel(ctx.variavel());
            Saida.println(";");
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
    
    public String visitIdentificador(LAParser.IdentificadorContext ctx) {
        String nome = ctx.id.getText();
        for(Token id : ctx.outrosIds){
            nome += "." + id.getText();
        }
        visitDimensao(ctx.dimensao());
        return pilhaDeTabelas.tipoDeDadoDoSimbolo(nome);
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
        String tipo = visitIdentificador(ctx.id1);
        Saida.print("scanf(\"");
        Saida.print(parseTipoFormat(tipo));
        for(LAParser.IdentificadorContext id : ctx.id2){
            tipo = visitIdentificador(id);
            Saida.print(parseTipoFormat(", " + tipo));
        }
        Saida.print("\", ");
        Saida.print("&" + ctx.id1.getText());
        for(LAParser.IdentificadorContext id : ctx.id2){
            Saida.print("&" + id.getText());
        }
        Saida.println(");");
    }
    
    public void visitCmdEscreva(LAParser.CmdEscrevaContext ctx){
        String tipo = visitExpressao(ctx.exp1);
        Saida.print("printf(\"");
        Saida.print(parseTipoFormat(tipo));
        for(LAParser.ExpressaoContext exp : ctx.exp2){
            tipo = visitExpressao(exp);
            Saida.print(parseTipoFormat(", " + tipo));
        }
        Saida.print("\", ");
        Saida.print(ctx.exp1.getText());
        for(LAParser.ExpressaoContext id : ctx.exp2){
            Saida.print(id.getText());
        }
        Saida.println(");");
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
    
    public String visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
        String tipo = visitTermo(ctx.t1);
        for(LAParser.TermoContext t : ctx.t2){
            String tipoTermo = visitTermo(t);
            tipo = tipoRetorno("aritmetica", tipo, tipoTermo);
        }       
        return tipo;      
    }
    
    public String visitTermo(LAParser.TermoContext ctx){
        String tipo = visitFator(ctx.f1);
        for(LAParser.FatorContext f : ctx.f2){
            String tipoFator = visitFator(f);
            tipo = tipoRetorno("aritmetica", tipo, tipoFator);
        }       
        return tipo;      
    }
    
    public String visitFator(LAParser.FatorContext ctx){
        String tipo = visitParcela(ctx.p1);
        for(LAParser.ParcelaContext p : ctx.p2){
            String tipoParcela = visitParcela(p);
            tipo = tipoRetorno("aritmetica", tipo, tipoParcela);
        }       
        return tipo;
    }

    public String visitParcela (LAParser.ParcelaContext ctx){
        if(ctx.parcela_unario() != null){
            String tipo = visitParcela_unario(ctx.parcela_unario());
            return tipo;
        }
        else{
            return visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
    }
    
    public String visitParcela_unario (LAParser.Parcela_unarioContext ctx){
        if(ctx.identificador() != null) {
            String tipo = visitIdentificador(ctx.identificador());
            return tipo;
        }
        else if(ctx.IDENT() != null){
            String id_txt = ctx.IDENT().getText();
            return pilhaDeTabelas.tipoDeDadoDoSimbolo(id_txt);
        }
        else if(ctx.NUM_INT() != null){
            return "inteiro";
        }
        else if(ctx.NUM_REAL() != null){
            return "real";
        }
        else{
            return visitExpressao(ctx.e3);
        }
    }
    
    public String visitParcela_nao_unario (LAParser.Parcela_nao_unarioContext ctx){
        if(ctx.identificador() != null){
            String tipo = visitIdentificador(ctx.identificador());
            String id_txt = ctx.identificador().getText();
            return "&" + tipo;
        }
        else{
            return "literal";
        }
    }
    
    public String visitExp_relacional (LAParser.Exp_relacionalContext ctx){
        String tipo = visitExp_aritmetica(ctx.e1);
        if(ctx.op_relacional() != null){
            String tipoExp = visitExp_aritmetica(ctx.e2);
            tipo = tipoRetorno("relacional", tipo, tipoExp);
        }
        return tipo;
    }

    public String visitExpressao(LAParser.ExpressaoContext ctx){
        String tipo = visitTermo_logico(ctx.t1);
        for(LAParser.Termo_logicoContext tl : ctx.t2){
            String tipoTermo = visitTermo_logico(tl);
            tipo = tipoRetorno("logica", tipo, tipoTermo);
        }
        return tipo;
    }
    
    public String visitTermo_logico(LAParser.Termo_logicoContext ctx){
        String tipo = visitFator_logico(ctx.f1);
        for(LAParser.Fator_logicoContext fl : ctx.f2){
            String tipoFator = visitFator_logico(fl);
            tipo = tipoRetorno("logica", tipo, tipoFator);
        } 
        return tipo;
    }
    
    public String visitFator_logico(LAParser.Fator_logicoContext ctx){
        String tipo = visitParcela_logica(ctx.parcela_logica());
        if(ctx.nao != null && !tipo.equals("logico")){
            return "tipo_invalido";
        }
        else{
            return tipo;
        }
    }
    
    public String visitParcela_logica(LAParser.Parcela_logicaContext ctx){
        if(ctx.exp_relacional() != null){
            return visitExp_relacional(ctx.exp_relacional());
        }
        else{
            return "logico";
        }
    }
}