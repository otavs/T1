package t1;

import java.io.FileInputStream;
import org.antlr.v4.runtime.ANTLRInputStream;
import org.antlr.v4.runtime.CommonTokenStream;

public class T1 {
    
    public static void main(String[] args) throws Exception {
        /*if(args.length == 0){
            System.out.println("Devem ser passados 2 argumentos: o arquivo de entrada e o arquivo de saída");
            System.exit(0);
        }
        else if(args.length > 2){
            System.out.println("Erro: O número de argumentos é muito grande.");
            System.exit(1);
        }
        String arquivoEntrada = args[0];
        String arquivoSaida = (args.length == 2) ? args[1] : null; */
        String arquivoEntrada = "C:\\Users\\otavi\\Desktop\\testes\\1-algoritmo_2-2_apostila_LA_1_erro_linha_3_acusado_linha_10.txt";
        ANTLRInputStream input = new ANTLRInputStream(new FileInputStream(arquivoEntrada));
        LALexer lexer = new LALexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        LAParser parser = new LAParser(tokens);
        parser.programa();
        
    }
    
}
