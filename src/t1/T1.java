package t1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class T1 {
    
    public static void main(String[] args) throws Exception {
        
        if(true){
        
        // Verifica os argumentos recebidos
        if(args.length == 0){
            System.out.println("Compilador LA");
            System.out.println("Devem ser passados 2 argumentos: o arquivo de entrada e o arquivo de saída");
            System.exit(0);
        }
        else if(args.length != 2){
            System.out.println("Erro: Devem ser passados 2 argumentos: o arquivo de entrada e o arquivo de saída");
            System.exit(1);
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
        if(Saida.getTexto().isEmpty()){
            LAVisitorSemantico visitor = new LAVisitorSemantico();
            visitor.visit(arvore);
        }
        
        Saida.println("Fim da compilacao");
        
        // Escreve a saida no arquivo de saida
        FileOutputStream arquivoSaida = new FileOutputStream(nomeArquivoSaida);
        PrintWriter p = new PrintWriter(arquivoSaida);
        p.write(Saida.getTexto());
        p.close();
        arquivoSaida.close();
        
        }else{
        
        String nomeArquivoEntrada = "C:\\Users\\otavi\\Desktop\\Compilers_2\\T1\\T1\\casosDeTesteT1\\2.arquivos_com_erros_semanticos\\entrada\\14.algoritmo_10-1_apostila_LA.txt";
        
        // Converte o arquivo de entrada para um input do ANTLR
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(nomeArquivoEntrada));
        
        // Análise léxica
        LALexer lexer = new LALexer(input);
        LAErrorListener errorListener = new LAErrorListener();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        
        // Análise sintática
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(errorListener);
        LAParser.ProgramaContext arvore = parser.programa();
        
        // Análise semântica
        if(Saida.getTexto().isEmpty()){
            LAVisitorSemantico visitor = new LAVisitorSemantico();
            visitor.visit(arvore);
        }
        
        Saida.println("Fim da compilacao");
        System.out.println(Saida.getTexto());
        
        }
        
    }
}
