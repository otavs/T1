package t1;

public class LAListenerGerador extends LABaseListener{
    
    @Override
    public void enterPrograma(LAParser.ProgramaContext ctx){
        Saida.println("test");
    }
    
    @Override
    public void exitPrograma(LAParser.ProgramaContext ctx){
        Saida.println("OLA");
    }
    
    
}
