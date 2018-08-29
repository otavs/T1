package t1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import org.antlr.v4.runtime.Token;

public class LAVisitorSemantico extends LABaseVisitor<String> {
    PilhaDeTabelas pilhaDeTabelas;
    HashMap<String, LinkedList<EntradaTabelaDeSimbolos>> mapRegistros;
    HashMap<String, LinkedList<String>> mapParametros;
    
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
    
    @Override
    public String visitPrograma(LAParser.ProgramaContext ctx){
        pilhaDeTabelas = new PilhaDeTabelas();
        mapRegistros = new HashMap<String, LinkedList<EntradaTabelaDeSimbolos>>();
        mapParametros = new HashMap<String, LinkedList<String>>();
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));
        TabelaDeSimbolos topo = pilhaDeTabelas.topo();
        topo.adicionarSimbolo("literal", "tipo", "");
        topo.adicionarSimbolo("inteiro", "tipo", "");
        topo.adicionarSimbolo("real", "tipo", "");
        topo.adicionarSimbolo("logico", "tipo", "");
        topo.adicionarSimbolo("^literal", "tipo", "");
        topo.adicionarSimbolo("^inteiro", "tipo", "");
        topo.adicionarSimbolo("^real", "tipo", "");
        topo.adicionarSimbolo("^logico", "tipo", "");
        visitDeclaracoes(ctx.declaracoes());
        visitCorpo(ctx.corpo());
        return "";
    }
    
    @Override
    public String visitDeclaracoes(LAParser.DeclaracoesContext ctx){
        for(LAParser.Decl_local_globalContext decl : ctx.decl_local_global()){
            visitDecl_local_global(decl);
        }
        return "";
    }
    
    @Override
    public String visitDecl_local_global(LAParser.Decl_local_globalContext ctx){
        if(ctx.decl_local() != null){
            visitDecl_local(ctx.decl_local());
        }
        else{
            visitDecl_global(ctx.decl_global());
        }
        return "";
    }
    
    @Override
    public String visitDecl_local(LAParser.Decl_localContext ctx){
        if(ctx.variavel() != null){
            visitVariavel(ctx.variavel());
        }
        else if(ctx.id1 != null){
            String id_txt = ctx.id1.getText();
            pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "constante", ctx.tipo_basico().getText());
        }
        else{
            String id_txt = ctx.id2.getText();
            if(pilhaDeTabelas.existeSimbolo(id_txt)){
                Saida.println("Linha " + ctx.id2.getTokenSource().getLine() + ": identificador " + id_txt + " ja declarado anteriormente");
            }
            if(ctx.tipo().registro() != null){
                mapRegistros.put(id_txt, new LinkedList<EntradaTabelaDeSimbolos>());
    	        pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "registro", "");
            }
            else{
                pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "tipo", ctx.tipo().getText());
            }
            visitTipo(ctx.tipo());
        }
        return "";
    }
    
    @Override
    public String visitVariavel(LAParser.VariavelContext ctx){
        // variavel de tipo registro criada a partir da regra variavel
        if(ctx.tipo().registro() != null){
            LinkedList<String> listaIds = new LinkedList<String>();
            visitIdentificador(ctx.id);
            listaIds.add(ctx.id.getText());
            for(LAParser.IdentificadorContext id : ctx.outrosIds){
                visitIdentificador(id);
                listaIds.add(id.getText());
            }
            visitTipo(ctx.tipo());
            LinkedList<EntradaTabelaDeSimbolos> novosSimbolos = new LinkedList<EntradaTabelaDeSimbolos>();
            ArrayList<EntradaTabelaDeSimbolos> topo = (ArrayList<EntradaTabelaDeSimbolos>) pilhaDeTabelas.topo().getListaSimbolos();
            for(String id : listaIds){
                for(int i = 0; i < topo.size(); i++){
                    EntradaTabelaDeSimbolos simbolo = topo.get(i);
                    novosSimbolos.add(new EntradaTabelaDeSimbolos(id + "." + simbolo.getNome(), simbolo.getTipo(), simbolo.getTipoDeDado()));
                }
            }
            pilhaDeTabelas.desempilhar();
            for(EntradaTabelaDeSimbolos simbolo : novosSimbolos){
                pilhaDeTabelas.topo().adicionarSimbolo(simbolo);
            }
        }
        // registro criado em declaracao de tipo
        else if(ctx.parent.parent.parent instanceof LAParser.Decl_localContext){
            String idRegistro = ((LAParser.Decl_localContext)ctx.parent.parent.parent).id2.getText();
            String tipo_txt = ctx.tipo().getText();
            visitIdentificador(ctx.id);
            mapRegistros.get(idRegistro).add(new EntradaTabelaDeSimbolos(ctx.id.getText(), "variavel", tipo_txt));
            for(LAParser.IdentificadorContext id : ctx.outrosIds){
                visitIdentificador(id);
                mapRegistros.get(idRegistro).add(new EntradaTabelaDeSimbolos(id.getText(), "variavel", tipo_txt));
            }
        }
        // variaveis normais
        else{
            visitIdentificador(ctx.id);
            String id_txt = ctx.id.getText();
            if(id_txt.indexOf('[') != -1) id_txt = id_txt.substring(0, id_txt.indexOf('['));
            String tipo_txt = ctx.tipo().getText();
            
            // O tipo da variavel é um registro
            if(mapRegistros.get(tipo_txt) != null){
                LinkedList <EntradaTabelaDeSimbolos> listaSimbolos = mapRegistros.get(tipo_txt);
                if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                    for(EntradaTabelaDeSimbolos simbolo : listaSimbolos){
                        pilhaDeTabelas.topo().adicionarSimbolo(id_txt + "." + simbolo.getNome(), simbolo.getTipo(), simbolo.getTipoDeDado());
                    }
                    pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "variavel", tipo_txt);
                }
                else{
                    Saida.println("Linha " + ctx.id.start.getLine() + ": identificador " + id_txt + " ja declarado anteriormente");
                }
                
                for(LAParser.IdentificadorContext id : ctx.outrosIds){
                    visitIdentificador(id);
                    id_txt = id.getText();
                    if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                        for(EntradaTabelaDeSimbolos simbolo : listaSimbolos){
                            pilhaDeTabelas.topo().adicionarSimbolo(id_txt + "." + simbolo.getNome(), simbolo.getTipo(), simbolo.getTipoDeDado());
                        }
                        pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "variavel", tipo_txt);
                    }
                    else{
                        Saida.println("Linha " + id.start.getLine() + ": identificador " + id_txt + " ja declarado anteriormente");
                    }
                }
            }
            // O tipo da variável não é registro
            else{
                if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                    if(tipo_txt.charAt(0) == '^'){
                        pilhaDeTabelas.topo().adicionarSimbolo("^" + id_txt, "variavel", tipo_txt.substring(1));
                        pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "variavel", tipo_txt);
                    }
                    else{
                        pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "variavel", tipo_txt);
                    }
                }
                else if(!(ctx.parent instanceof LAParser.RegistroContext)){
                    Saida.println("Linha " + ctx.id.start.getLine() + ": identificador " + id_txt + " ja declarado anteriormente");
                }

                for(LAParser.IdentificadorContext id : ctx.outrosIds){
                    visitIdentificador(id);
                    id_txt = id.getText();
                    if(id_txt.indexOf('[') != -1) id_txt = id_txt.substring(0, id_txt.indexOf('['));
                    if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                        if(tipo_txt.charAt(0) == '^'){
                            pilhaDeTabelas.topo().adicionarSimbolo("^" + id_txt, "variavel", tipo_txt.substring(1));
                            pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "variavel", tipo_txt);
                        }
                        else{
                            pilhaDeTabelas.topo().adicionarSimbolo(id_txt, "variavel", tipo_txt);
                        }
                    }
                    else if(!(ctx.parent instanceof LAParser.RegistroContext)){
                        Saida.println("Linha " + id.start.getLine() + ": identificador " + id_txt + " ja declarado anteriormente");
                    }
                }
            }
            visitTipo(ctx.tipo());
            if(!pilhaDeTabelas.existeSimbolo(tipo_txt)){
                Saida.println("Linha " + ctx.tipo().start.getLine() + ": tipo " + tipo_txt + " nao declarado");
            }
        }
        return "";
    }
    
    @Override
    public String visitIdentificador(LAParser.IdentificadorContext ctx) {
        String nome = ctx.id.getText();
        for(Token id : ctx.outrosIds){
            nome += "." + id.getText();
        }
        visitDimensao(ctx.dimensao());
        return pilhaDeTabelas.tipoDeDadoDoSimbolo(nome);
    }
    
    @Override
    public String visitDimensao(LAParser.DimensaoContext ctx){
        for(LAParser.Exp_aritmeticaContext exp : ctx.exp_aritmetica()){
            visitExp_aritmetica(exp);
        }
        return "";
    }
    
    @Override
    public String visitTipo(LAParser.TipoContext ctx){
        if(ctx.registro() != null){
            visitRegistro(ctx.registro());
        }
        else{
            visitTipo_estendido(ctx.tipo_estendido());
        }
        return "";
    }
    
    @Override
    public String visitTipo_basico_ident(LAParser.Tipo_basico_identContext ctx){
        if(ctx.tipo_basico() != null){
            return ctx.tipo_basico().getText();
        }
        else{
            return ctx.IDENT().getText();
        }
    }
    
    @Override
    public String visitTipo_estendido(LAParser.Tipo_estendidoContext ctx){
        return visitTipo_basico_ident(ctx.tipo_basico_ident());
    }
    
    @Override
    public String visitRegistro(LAParser.RegistroContext ctx){
        if(ctx.parent.parent instanceof LAParser.VariavelContext) pilhaDeTabelas.empilhar(new TabelaDeSimbolos("registro"));
        for(LAParser.VariavelContext var : ctx.variavel()){    
            visitVariavel(var);
        }
        return "";
    }
    
    @Override
    public String visitDecl_global(LAParser.Decl_globalContext ctx){
        // Procedimento
        if(ctx.ident1 != null){
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.ident1.getText(), "procedimento", "");
            mapParametros.put(ctx.ident1.getText(), new LinkedList<String>());
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("procedimento"));
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.ident1.getText(), "procedimento", "");
            if(ctx.params1 != null){
                visitParametros(ctx.params1);
            }
            for(LAParser.Decl_localContext decl : ctx.decl1){
                visitDecl_local(decl);
            }
            for(LAParser.CmdContext cmd : ctx.c1){
                visitCmd(cmd);
            }
            pilhaDeTabelas.desempilhar();
        // Função
        }else {
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.ident2.getText(), "funcao", ctx.tipo_estendido().getText());
            mapParametros.put(ctx.ident2.getText(), new LinkedList<String>());
            pilhaDeTabelas.empilhar(new TabelaDeSimbolos("funcao"));
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.ident2.getText(), "funcao", ctx.tipo_estendido().getText());
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
            pilhaDeTabelas.desempilhar();
        }
        return "";
    }
    
    @Override
    public String visitParametro(LAParser.ParametroContext ctx){
        visitIdentificador(ctx.id1);
        String tipo_txt = ctx.tipo_estendido().getText();
        pilhaDeTabelas.topo().adicionarSimbolo(ctx.id1.getText(), "variavel", tipo_txt);
        LinkedList<EntradaTabelaDeSimbolos> listaAtributos = mapRegistros.get(tipo_txt);
        if(listaAtributos != null){
            for(EntradaTabelaDeSimbolos etds : listaAtributos){
                pilhaDeTabelas.topo().adicionarSimbolo(ctx.id1.getText() + "." + etds.getNome(), etds.getTipo(), etds.getTipoDeDado());
            }
        }
        for(LAParser.IdentificadorContext id : ctx.id2){
            visitIdentificador(id);
            pilhaDeTabelas.topo().adicionarSimbolo(id.getText(), "variavel", tipo_txt);
            if(listaAtributos != null){
                for(EntradaTabelaDeSimbolos etds : listaAtributos){
                    pilhaDeTabelas.topo().adicionarSimbolo(id.getText() + "." + etds.getNome(), etds.getTipo(), etds.getTipoDeDado());
                }
            }
        }
        return visitTipo_estendido(ctx.tipo_estendido());
    }
    
    @Override
    public String visitParametros(LAParser.ParametrosContext ctx){
        LAParser.Decl_globalContext declGlobal = ((LAParser.Decl_globalContext)(ctx.parent));
        String nomeFuncao;
        if(declGlobal.ident1 != null) nomeFuncao = declGlobal.ident1.getText(); // procedimento
        else nomeFuncao = declGlobal.ident2.getText(); // função
        String tipoParam = visitParametro(ctx.param1);
        mapParametros.get(nomeFuncao).add(tipoParam);
        for(LAParser.ParametroContext param : ctx.param2){
            tipoParam = visitParametro(param);
            mapParametros.get(nomeFuncao).add(tipoParam);
        }
        return "";
    }
    
    @Override
    public String visitCorpo(LAParser.CorpoContext ctx){
        for(LAParser.Decl_localContext decl : ctx.decl_local()){
            visitDecl_local(decl);
        }
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return "";
    }
    
    @Override
    public String visitCmd(LAParser.CmdContext ctx){
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
        return "";
    }
    @Override
    public String visitCmdLeia(LAParser.CmdLeiaContext ctx){
        String id_txt = ctx.id1.getText();
        if(id_txt.indexOf('[') != -1) id_txt = id_txt.substring(0, id_txt.indexOf('['));
        if(!pilhaDeTabelas.existeSimbolo(id_txt)){
            Saida.println("Linha " + ctx.id1.start.getLine() + ": identificador " + id_txt + " nao declarado");
        }
        visitIdentificador(ctx.id1);
        for(LAParser.IdentificadorContext id : ctx.id2){
            id_txt = id.getText();
            if(id_txt.indexOf('[') != -1) id_txt = id_txt.substring(0, id_txt.indexOf('['));
            if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                Saida.println("Linha " + ctx.id1.start.getLine() + ": identificador " + id_txt + " nao declarado");
            }
            visitIdentificador(id);
        }
        return "";
    }
    
    @Override
    public String visitCmdEscreva(LAParser.CmdEscrevaContext ctx){
        visitExpressao(ctx.exp1);
        for(LAParser.ExpressaoContext exp : ctx.exp2){
            visitExpressao(exp);
        }
        return "";
    }
    
    @Override
    public String visitCmdSe(LAParser.CmdSeContext ctx){
        visitExpressao(ctx.e1);
        for(LAParser.CmdContext cmd : ctx.c1){
            visitCmd(cmd);
        }
        if (ctx.c2 != null) {
            for(LAParser.CmdContext cmd : ctx.c2){
                visitCmd(cmd);
            }
        }
        return "";
    }
    
    @Override
    public String visitCmdCaso (LAParser.CmdCasoContext ctx){
        visitExp_aritmetica(ctx.exp_aritmetica());
        visitSelecao(ctx.selecao());
        if(ctx.cmd() != null){
            for(LAParser.CmdContext cmd : ctx.cmd()){
                visitCmd(cmd);
            }   
        }
        return "";
    }
    
    @Override
    public String visitCmdPara (LAParser.CmdParaContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("para"));
        visitExp_aritmetica(ctx.ea1);
        visitExp_aritmetica(ctx.ea2);
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        pilhaDeTabelas.desempilhar();
        return "";
    }
    
    @Override
    public String visitCmdEnquanto (LAParser.CmdEnquantoContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("enquanto"));
        visitExpressao(ctx.expressao());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        pilhaDeTabelas.desempilhar();
        return "";
    }
    
    @Override
    public String visitCmdFaca (LAParser.CmdFacaContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("faca"));
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        visitExpressao(ctx.expressao());
        pilhaDeTabelas.desempilhar();
        return "";
    }
    
    @Override
    public String visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx){
        String tipoId = visitIdentificador(ctx.identificador());
        if(tipoId.equals("")) tipoId = "tipo_invalido";
        String id_txt = ctx.identificador().getText();
        String id_txt_certo = id_txt + "";
        if(id_txt.indexOf('[') != -1) id_txt = id_txt.substring(0, id_txt.indexOf('['));
        // variavel ponteiro atribuindo no conteudo apontado
        if(ctx.ponteiro != null){
            id_txt = "^" + id_txt;
            id_txt_certo = "^" + id_txt_certo;
            tipoId = tipoId.substring(1);
        }
        // variavel ponteiro atribuindo um endereço
        else if(tipoId.charAt(0) == '^'){
            id_txt = "^" + id_txt;
        }
        
        String tipoExp = visitExpressao(ctx.expressao());
        if(!pilhaDeTabelas.existeSimbolo(id_txt)){
            Saida.println("Linha " + ctx.identificador().start.getLine() + ": identificador " + id_txt_certo + " nao declarado");
        }
        else{
            if(tipoId.charAt(0) == '^'){
                if(tipoExp.charAt(0) != '&' || !tipoId.substring(1).equals(tipoExp.substring(1))){
                    Saida.println("Linha " + ctx.identificador().start.getLine() + ": atribuicao nao compativel para " + id_txt);
                }
            }
            else if(!tipoId.equals(tipoExp) && !(isNumerico(tipoId) && isNumerico(tipoExp))){
                Saida.println("Linha " + ctx.identificador().start.getLine() + ": atribuicao nao compativel para " + id_txt_certo);
            }
        }
        return "";
    }
    
    @Override
    public String visitCmdChamada(LAParser.CmdChamadaContext ctx){
        visitExpressao(ctx.exp);
        for(LAParser.ExpressaoContext exp : ctx.outrasExp){
            visitExpressao(exp);
        }
        return "";
    }
    
    @Override
    public String visitCmdRetorne(LAParser.CmdRetorneContext ctx){
        if(!pilhaDeTabelas.topo().getEscopo().equals("funcao"))
            Saida.println("Linha " + ctx.start.getLine() + ": comando retorne nao permitido nesse escopo");
        visitExpressao(ctx.expressao());
        return "";
    }
    
    @Override
    public String visitSelecao(LAParser.SelecaoContext ctx){
        for(LAParser.Item_selecaoContext item : ctx.item_selecao()){
            visitItem_selecao(item);
        }
        return "";
    }
    
    @Override
    public String visitItem_selecao(LAParser.Item_selecaoContext ctx){
        visitConstantes(ctx.constantes());
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        return "";
    }
    
    @Override
    public String visitConstantes(LAParser.ConstantesContext ctx){
        visitNumero_intervalo(ctx.ni1);
        for(LAParser.Numero_intervaloContext ni : ctx.ni2){
            visitNumero_intervalo(ni);
        }
        return "";
    }
    
    @Override
    public String visitNumero_intervalo(LAParser.Numero_intervaloContext ctx){
        if(ctx.opu1 != null){
            visitOp_unario(ctx.opu1);
        }
        else if(ctx.opu2 != null){
            visitOp_unario(ctx.opu2);
        }        
        return "";
    }
    
    @Override
    public String visitExp_aritmetica(LAParser.Exp_aritmeticaContext ctx){
        String tipo = visitTermo(ctx.t1);
        for(LAParser.TermoContext t : ctx.t2){
            String tipoTermo = visitTermo(t);
            tipo = tipoRetorno("aritmetica", tipo, tipoTermo);
        }       
        return tipo;
    }
    
    @Override
    public String visitTermo(LAParser.TermoContext ctx){
        String tipo = visitFator(ctx.f1);
        for(LAParser.FatorContext f : ctx.f2){
            String tipoFator = visitFator(f);
            tipo = tipoRetorno("aritmetica", tipo, tipoFator);
        }       
        return tipo;
    }
    
    @Override
    public String visitFator(LAParser.FatorContext ctx){
        String tipo = visitParcela(ctx.p1);
        for(LAParser.ParcelaContext p : ctx.p2){
            String tipoParcela = visitParcela(p);
            tipo = tipoRetorno("aritmetica", tipo, tipoParcela);
        }       
        return tipo;
    }

    @Override
    public String visitParcela (LAParser.ParcelaContext ctx){
        if(ctx.parcela_unario() != null){
            String tipo = visitParcela_unario(ctx.parcela_unario());
            if(ctx.op_unario() != null && !tipo.equals("inteiro") && !tipo.equals("real")){
                return "tipo_indefinido";
            }
            return tipo;
        }
        else{
            return visitParcela_nao_unario(ctx.parcela_nao_unario());
        }
    }
    
    @Override
    public String visitParcela_unario (LAParser.Parcela_unarioContext ctx){
        if(ctx.identificador() != null) {
            String tipo = visitIdentificador(ctx.identificador());
            String id_txt = ctx.identificador().getText();
            if(id_txt.indexOf('[') != -1) id_txt = id_txt.substring(0, id_txt.indexOf('['));
            if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                Saida.println("Linha " + ctx.identificador().start.getLine() + ": identificador " + id_txt + " nao declarado");
            }
            return tipo;
        }
        else if(ctx.IDENT() != null){
            String id_txt = ctx.IDENT().getText();
            if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                Saida.println("Linha " + ctx.IDENT().getSymbol().getLine() + ": identificador " + id_txt + " nao declarado");
            }
            String tipoExp = visitExpressao(ctx.e1);         
            LinkedList<String> listaParams = mapParametros.get(id_txt);
            boolean compativel = true;
            compativel = !listaParams.isEmpty() && compativel && listaParams.get(0).equals(tipoExp);
            int i = 0;
            for(LAParser.ExpressaoContext e : ctx.e2){
                tipoExp = visitExpressao(e);
                i++;
                compativel = i <= listaParams.size() && compativel && listaParams.get(i).equals(tipoExp);
            }
            if(listaParams.size() != ctx.e2.size()+1) compativel = false;
            if(!compativel) Saida.println("Linha " + ctx.start.getLine() + ": incompatibilidade de parametros na chamada de " + id_txt);
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
    
    @Override
    public String visitParcela_nao_unario (LAParser.Parcela_nao_unarioContext ctx){
        if(ctx.identificador() != null){
            String tipo = visitIdentificador(ctx.identificador());
            String id_txt = ctx.identificador().getText();
            if(!pilhaDeTabelas.existeSimbolo(id_txt)){
                Saida.println("Linha " + ctx.identificador().start.getLine() + ": identificador " + id_txt + " nao declarado");
            }
            return "&" + tipo;
        }
        else{
            return "literal";
        }
    }
    
    @Override
    public String visitExp_relacional (LAParser.Exp_relacionalContext ctx){
        String tipo = visitExp_aritmetica(ctx.e1);
        if(ctx.op_relacional() != null){
            String tipoExp = visitExp_aritmetica(ctx.e2);
            tipo = tipoRetorno("relacional", tipo, tipoExp);
        }
        return tipo;
    }

    @Override
    public String visitExpressao(LAParser.ExpressaoContext ctx){
        String tipo = visitTermo_logico(ctx.t1);
        for(LAParser.Termo_logicoContext tl : ctx.t2){
            String tipoTermo = visitTermo_logico(tl);
            tipo = tipoRetorno("logica", tipo, tipoTermo);
        }
        return tipo;
    }
    
    @Override
    public String visitTermo_logico(LAParser.Termo_logicoContext ctx){
        String tipo = visitFator_logico(ctx.f1);
        for(LAParser.Fator_logicoContext fl : ctx.f2){
            String tipoFator = visitFator_logico(fl);
            tipo = tipoRetorno("logica", tipo, tipoFator);
        } 
        return tipo;
    }
    
    @Override
    public String visitFator_logico(LAParser.Fator_logicoContext ctx){
        String tipo = visitParcela_logica(ctx.parcela_logica());
        if(ctx.nao != null && !tipo.equals("logico")){
            return "tipo_invalido";
        }
        else{
            return tipo;
        }
    }
    
    @Override
    public String visitParcela_logica(LAParser.Parcela_logicaContext ctx){
        if(ctx.exp_relacional() != null){
            return visitExp_relacional(ctx.exp_relacional());
        }
        else{
            return "logico";
        }
    }
}