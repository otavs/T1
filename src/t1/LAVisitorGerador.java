package t1;

import java.util.ArrayList;
import java.util.HashMap;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.misc.Pair;

public class LAVisitorGerador{
    
    PilhaDeTabelas pilhaDeTabelas = new PilhaDeTabelas();
    HashMap<String, ArrayList<Pair<String, String>>> mapRegistros = new HashMap<String, ArrayList<Pair<String, String>>>();
    
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
        if(tipo.charAt(0) == '^') tipo = tipo.substring(1);
        if(tipo.equals("inteiro") || tipo.equals("logico")) return "int";
        if(tipo.equals("real")) return "float";
        if(tipo.equals("literal")) return "char";
        return tipo;
    }
    
    public String parseTipoFormat(String tipo){
        if(tipo.equals("inteiro") || tipo.equals("logico")) return "%d";
        if(tipo.equals("real")) return "%f";
        if(tipo.equals("literal")) return "%s";
        return tipo;
    }
    
    public String parseExpressao(LAParser.ExpressaoContext ctx){
        String ret = parseTermo_logico(ctx.t1);
        for(LAParser.Termo_logicoContext termo : ctx.t2){
            ret += "||";
            ret += parseTermo_logico(termo);
        }
        return ret.replaceAll("=", "==").replaceAll("<>", "!=").replaceAll(">==", ">=").replaceAll("<==", "<=");
    }
    
    public String parseTermo_logico(LAParser.Termo_logicoContext ctx){
        String ret = parseFator_logico(ctx.f1);
        for(LAParser.Fator_logicoContext fl : ctx.f2){
            ret += "&&";
            ret += parseFator_logico(fl);
        } 
        return ret;
    }
    
    public String parseFator_logico(LAParser.Fator_logicoContext ctx){
        if(ctx.nao != null){
            return "!" + ctx.parcela_logica().getText();
        }
        else{
            return ctx.parcela_logica().getText();
        }
    }
    
    public void visitPrograma(LAParser.ProgramaContext ctx){
        pilhaDeTabelas.empilhar(new TabelaDeSimbolos("global"));
        Saida.println("#include <stdio.h>");
        Saida.println("#include <stdlib.h>");
        Saida.println("#include <string.h>");
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
            // variavel normal
            if(ctx.variavel().tipo().getText().indexOf("registro") == -1){
                boolean ponteiro = ctx.variavel().tipo().getText().charAt(0) == '^';
                String tipo = ctx.variavel().tipo().getText();
                String nome = ctx.variavel().id.getText();
                Saida.print(parseTipo(tipo) + " ");
                if(ponteiro) Saida.print("*");
                Saida.print(nome);
                boolean isString = tipo.equals("literal");
                if(isString) Saida.print("[1000]");
                if(nome.indexOf("[") != -1) nome = nome.substring(0, nome.indexOf("["));
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                for(LAParser.IdentificadorContext id : ctx.variavel().outrosIds){
                    nome = id.getText();
                    if(ponteiro) Saida.print("*");
                    Saida.print(", " + nome);
                    if(isString) Saida.print("[1000]");
                    if(nome.indexOf("[") != -1) nome = nome.substring(0, nome.indexOf("["));
                    pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                }
                Saida.println(";");
                if(mapRegistros.containsKey(ctx.variavel().tipo().getText())){
                    for(Pair<String, String> p : mapRegistros.get(ctx.variavel().tipo().getText())){
                        pilhaDeTabelas.topo().adicionarSimbolo(ctx.variavel().id.getText() + "." + p.a, "variavel", p.b);
                        for(LAParser.IdentificadorContext id : ctx.variavel().outrosIds){
                            pilhaDeTabelas.topo().adicionarSimbolo(id.getText() + "." + p.a, "variavel", p.b);
                        }
                    }
                }
            }
            // registro
            else{
                Saida.println("struct {");
                ArrayList<Pair<String, String>> varList = new ArrayList<Pair<String, String>>();
                for(LAParser.VariavelContext var : ctx.variavel().tipo().registro().variavel()){
                    String tipo = var.tipo().getText();
                    Saida.print(parseTipo(tipo) + " ");
                    Saida.print(var.id.getText());
                    boolean isString = tipo.equals("literal");
                    if(isString) Saida.print("[1000]");
                    varList.add(new Pair<String, String>(var.id.getText(), tipo));
                    for(LAParser.IdentificadorContext id : var.outrosIds){
                        Saida.print(", " + id.getText());
                        if(isString) Saida.print("[1000]");
                        varList.add(new Pair<String, String>(id.getText(), tipo));
                    }
                    Saida.println(";");
                }
                Saida.print("} " + ctx.variavel().id.getText());
                for(Pair<String, String> p : varList){
                     pilhaDeTabelas.topo().adicionarSimbolo(ctx.variavel().id.getText() + "." + p.a, "variavel", p.b);
                }
                for(LAParser.IdentificadorContext id : ctx.variavel().outrosIds){
                    Saida.print(", " + id.getText());
                    for(Pair<String, String> p : varList){
                        pilhaDeTabelas.topo().adicionarSimbolo(ctx.variavel().id.getText() + "." + p.a, "variavel", p.b);
                    }
                }
                Saida.println(";");
            }
        }
        else if(ctx.id1 != null){
            Saida.println("#define " + ctx.id1.getText() + " " + ctx.valor_constante().getText());
        }
        else{
            if(ctx.tipo().registro() != null){
                Saida.println("typedef struct {");
                String nome = ctx.id2.getText();
                mapRegistros.put(nome, new ArrayList<Pair<String, String>>());
                for(LAParser.VariavelContext var : ctx.tipo().registro().variavel()){
                    String tipo = var.tipo().getText();
                    mapRegistros.get(nome).add(new Pair<String, String>(var.id.getText(), tipo));
                    Saida.print(parseTipo(tipo) + " ");
                    Saida.print(var.id.getText());
                    boolean isString = tipo.equals("literal");
                    if(isString) Saida.print("[1000]");
                    for(LAParser.IdentificadorContext id : var.outrosIds){
                        mapRegistros.get(nome).add(new Pair<String, String>(id.getText(), tipo));
                        Saida.print(", " + id.getText());
                        if(isString) Saida.print("[1000]");
                    }
                    Saida.println(";");
                }
                Saida.println("} " + nome + ";");
            }
        }
    }
    
    public String visitIdentificador(LAParser.IdentificadorContext ctx) {
        String nome = ctx.id.getText();
        for(Token id : ctx.outrosIds){
            nome += "." + id.getText();
        }
        return pilhaDeTabelas.tipoDeDadoDoSimbolo(nome);
    }
    
    public void visitDecl_global(LAParser.Decl_globalContext ctx){
        // Procedimento
        if(ctx.ident1 != null){
            Saida.print("void " + ctx.ident1.getText() + "(");
            if(ctx.params1 != null){
                String tipo = ctx.params1.param1.tipo_estendido().getText();
                String nome = ctx.params1.param1.id1.getText();
                Saida.print(parseTipo(tipo) + " ");
                boolean isString = tipo.equals("literal");
                if(isString) Saida.print("*");
                Saida.print(nome);
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                for(LAParser.IdentificadorContext id : ctx.params1.param1.id2){
                    Saida.print(", ");
                    nome = id.getText();
                    Saida.print(parseTipo(tipo) + " ");
                    Saida.print(nome);
                    if(isString) Saida.print("*");
                    pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                }
                for(LAParser.ParametroContext param : ctx.params1.param2){
                    tipo = param.tipo_estendido().getText();
                    nome = param.id1.getText();
                    Saida.print(parseTipo(tipo) + " ");
                    Saida.print(nome);
                    isString = tipo.equals("literal");
                    if(isString) Saida.print("*");
                    pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                    for(LAParser.IdentificadorContext id : param.id2){
                        Saida.print(", ");
                        nome = id.getText();
                        Saida.print(parseTipo(tipo) + " ");
                        Saida.print(nome);
                        if(isString) Saida.print("*");
                        pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                    }
                }
            }
            Saida.println("){");
            for(LAParser.Decl_localContext decl : ctx.decl1)
                visitDecl_local(decl);
            for(LAParser.CmdContext cmd : ctx.c1)
                visitCmd(cmd);
            Saida.println("}");
        }
        // Função
        else{
            pilhaDeTabelas.topo().adicionarSimbolo(ctx.ident2.getText(), "funcao", ctx.tipo_estendido().getText());
            Saida.print(parseTipo(ctx.tipo_estendido().getText()) + " " + ctx.ident2.getText() + "(");
            if(ctx.params2 != null){
                String tipo = ctx.params2.param1.tipo_estendido().getText();
                String nome = ctx.params2.param1.id1.getText();
                Saida.print(parseTipo(tipo) + " ");
                boolean isString = tipo.equals("literal");
                if(isString) Saida.print("*");
                Saida.print(nome);
                pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                for(LAParser.IdentificadorContext id : ctx.params2.param1.id2){
                    Saida.print(", ");
                    nome = id.getText();
                    Saida.print(parseTipo(tipo) + " ");
                    Saida.print(nome);
                    if(isString) Saida.print("*");
                    pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                }
                for(LAParser.ParametroContext param : ctx.params2.param2){
                    tipo = param.tipo_estendido().getText();
                    nome = param.id1.getText();
                    Saida.print(parseTipo(tipo) + " ");
                    Saida.print(nome);
                    isString = tipo.equals("literal");
                    if(isString) Saida.print("*");
                    pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                    for(LAParser.IdentificadorContext id : param.id2){
                        Saida.print(", ");
                        nome = id.getText();
                        Saida.print(parseTipo(tipo) + " ");
                        Saida.print(nome);
                        if(isString) Saida.print("*");
                        pilhaDeTabelas.topo().adicionarSimbolo(nome, "variavel", tipo);
                    }
                }
            }
            Saida.println("){");
            for(LAParser.Decl_localContext decl : ctx.decl2)
                visitDecl_local(decl);
            for(LAParser.CmdContext cmd : ctx.c2)
                visitCmd(cmd);
            Saida.println("}");
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
            Saida.print(parseTipoFormat(tipo));
        }
        Saida.print("\", ");
        Saida.print(parseExpressao(ctx.exp1));
        for(LAParser.ExpressaoContext id : ctx.exp2){
            Saida.print(", " + parseExpressao(id));
        }
        Saida.println(");");
    }
    
    public void visitCmdSe(LAParser.CmdSeContext ctx){
        Saida.println("if(" + parseExpressao(ctx.e1) + "){");
        for(LAParser.CmdContext cmd : ctx.c1){
            visitCmd(cmd);
        }
        Saida.println("}");
        if(!ctx.c2.isEmpty()) {
            Saida.println("else{");
            for(LAParser.CmdContext cmd : ctx.c2){
                visitCmd(cmd);
            }
            Saida.println("}");
        }
    }
    
    public void visitCmdCaso (LAParser.CmdCasoContext ctx){
        Saida.println("switch(" + ctx.exp_aritmetica().getText() + "){");
        for(LAParser.Item_selecaoContext item : ctx.selecao().item_selecao()){
            int a = Integer.parseInt(item.constantes().ni1.ni1.getText());
            int b = a;
            if(item.constantes().ni1.ni2 != null)
                b = Integer.parseInt(item.constantes().ni1.ni2.getText());
            while(a <= b) Saida.println("case " + a++ + ": ");
            for(LAParser.CmdContext cmd : item.cmd()){
                visitCmd(cmd);
            }
            Saida.println("break;");
        }
        if(ctx.cmd() != null){
            Saida.println("default:");
            for(LAParser.CmdContext cmd : ctx.cmd()){
                visitCmd(cmd);
            }
        }
        Saida.println("}");
    }
    
    public void visitCmdPara (LAParser.CmdParaContext ctx){
        String id = ctx.IDENT().getText();
        Saida.println("for(" + id + " = " + ctx.ea1.getText() + "; " + id + " <= " + ctx.ea2.getText() + "; " + id + "++){");
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        Saida.println("}");
    }
    
    public void visitCmdEnquanto (LAParser.CmdEnquantoContext ctx){
        Saida.println("while(" + parseExpressao(ctx.expressao()) + "){");
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        Saida.println("}");
    }
    
    public void visitCmdFaca (LAParser.CmdFacaContext ctx){
        Saida.println("do{");
        for(LAParser.CmdContext cmd : ctx.cmd()){
            visitCmd(cmd);
        }
        Saida.println("} while(" + parseExpressao(ctx.expressao()) + ");");
    }
    
    public void visitCmdAtribuicao(LAParser.CmdAtribuicaoContext ctx){
        String tipo = visitExpressao(ctx.expressao());
        if(tipo.equals("literal")){
            Saida.println("strcpy(" + ctx.identificador().getText() + ", " + ctx.expressao().getText() + ");");
        }
        else{
            if(ctx.ponteiro != null) Saida.print("*");
            Saida.println(ctx.identificador().getText() + " = " + ctx.expressao().getText() + ";");
        }     
    }
    
    public void visitCmdChamada(LAParser.CmdChamadaContext ctx){
        Saida.print(ctx.IDENT() + "(" + parseExpressao(ctx.exp));
        for(LAParser.ExpressaoContext exp : ctx.outrasExp){
            Saida.print(", " + parseExpressao(exp));
        }
        Saida.println(");");
    }
    
    public void visitCmdRetorne(LAParser.CmdRetorneContext ctx){
        Saida.println("return " + parseExpressao(ctx.expressao()) + ";");
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