package t1;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class T1 {
    
    public static void main(String[] args) throws Exception {
        
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
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(nomeArquivoEntrada));
        LALexer lexer = new LALexer(input);
        LAErrorListener errorListener = new LAErrorListener();
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.addErrorListener(errorListener);
        parser.programa();
        String saida = errorListener.getSaida();
        saida += "Fim da compilacao\n";
        System.out.println(saida);
        FileOutputStream arquivoSaida = new FileOutputStream(nomeArquivoSaida);
        PrintWriter p = new PrintWriter(arquivoSaida);
        p.write(saida);
        p.close();
        arquivoSaida.close();
        
    }
    
}
