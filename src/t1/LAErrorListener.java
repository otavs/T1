package t1;

import java.util.BitSet;
import org.antlr.v4.runtime.ANTLRErrorListener;
import org.antlr.v4.runtime.Parser;
import org.antlr.v4.runtime.RecognitionException;
import org.antlr.v4.runtime.Recognizer;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.atn.ATNConfigSet;
import org.antlr.v4.runtime.dfa.DFA;

public class LAErrorListener implements ANTLRErrorListener{

    String saida;
    
    public LAErrorListener() {
        saida = "";
    }
    
    String getSaida(){
        return saida;
    }
    
    @Override
    public void syntaxError(Recognizer<?, ?> rcgnzr, Object o, int i, int i1, String string, RecognitionException re) {
        if(saida == ""){
            if(o instanceof Token){
                Token token = (Token)o;
                String texto = token.getText();
                if(token.getType() == LALexer.CARACTERE_ERRADO){
                    if(texto.equals("{")) saida += "Linha " + (i+1) + ": comentario nao fechado\n";
                    else saida += "Linha " + i + ": " + texto + " - simbolo nao identificado\n";
                }
                else{
                    if(texto.equals("<EOF>")) texto = "EOF";
                    saida += "Linha " + i + ": erro sintatico proximo a " + texto + "\n";
                }
            }
        }
    }

    @Override
    public void reportAmbiguity(Parser parser, DFA dfa, int i, int i1, boolean bln, BitSet bitset, ATNConfigSet atncs) {
    }

    @Override
    public void reportAttemptingFullContext(Parser parser, DFA dfa, int i, int i1, BitSet bitset, ATNConfigSet atncs) {
    }

    @Override
    public void reportContextSensitivity(Parser parser, DFA dfa, int i, int i1, int i2, ATNConfigSet atncs) {
    }
    
}
