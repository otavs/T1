package t1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class T1 {
    
    public static void main(String[] args) throws Exception {
        
        boolean debug = true;
        
        if(!debug){
        
        // Verifica os argumentos recebidos
        if(args.length != 2){
            System.out.println("Compilador LA");
            System.out.println("Devem ser passados 2 argumentos: o arquivo de entrada e o arquivo de saída");
            System.exit(0);
        }
        
        String nomeArquivoEntrada = args[0];
        String nomeArquivoSaida = args[1];
                
        // Converte o arquivo de entrada para um input do ANTLR
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(nomeArquivoEntrada));
        
        // Análise léxica
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Análise sintática
        LAParser parser = new LAParser(tokens);
        LAErrorListener errorListener = new LAErrorListener();
        parser.addErrorListener(errorListener);
        LAParser.ProgramaContext arvore = parser.programa();
        
        // Análise semântica
        if(Saida.isEmpty()){ // apenas se não houve erro na análise sintática
            LAVisitorSemantico visitorSemantico = new LAVisitorSemantico();
            visitorSemantico.visitPrograma(arvore);
        }
        
        // Geração de código C
        if(Saida.isEmpty()){ // apenas se não houve erro na análise semântica
            LAVisitorGerador visitorGerador = new LAVisitorGerador();
            visitorGerador.visitPrograma(arvore);
        }
        else{
            Saida.println("Fim da compilacao");
        }
        
        // Escreve a saída no arquivo de saída
        FileOutputStream arquivoSaida = new FileOutputStream(nomeArquivoSaida);
        PrintWriter p = new PrintWriter(arquivoSaida);
        p.write(Saida.getTexto());
        p.close();
        arquivoSaida.close();
        
        }
        
        // DEBUG
        else{
            
            String nomeArquivoEntrada = "C:\\Users\\otavi\\Desktop\\Compilers_2\\T1\\T1\\casosDeTesteT1\\3.arquivos_sem_erros\\1.entrada\\1.declaracao_leitura_impressao_inteiro.alg";

            // Converte o arquivo de entrada para um input do ANTLR
            ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(nomeArquivoEntrada));

            // Análise léxica
            LALexer lexer = new LALexer(input);
            CommonTokenStream tokens = new CommonTokenStream(lexer);

            // Análise sintática
            LAParser parser = new LAParser(tokens);
            LAErrorListener errorListener = new LAErrorListener();
            parser.addErrorListener(errorListener);
            LAParser.ProgramaContext arvore = parser.programa();

            // Análise semântica
            if(Saida.getTexto().isEmpty()){ // apenas se não houve erro na análise sintática
                LAVisitorSemantico visitor = new LAVisitorSemantico();
                visitor.visitPrograma(arvore);
            }

            // Geração de código C
            if(Saida.getTexto().isEmpty()){
                LAVisitorGerador visitorGerador = new LAVisitorGerador();
                visitorGerador.visitPrograma(arvore);
            }
            else{
                Saida.println("Fim da compilacao");
            }

            System.out.println(Saida.getTexto());
            
        }
        
    }
}
