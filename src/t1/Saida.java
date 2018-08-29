package t1;

public class Saida {
    private static String texto = "";
    
    public static void println(String txt) {
        texto += txt + "\n";
    }
    
    public static void clear() {
        texto = "";
    }
    
    public static String getTexto() {
        return texto;
    }
    
    public static boolean isEmpty(){
        return texto.isEmpty();
    }
}
