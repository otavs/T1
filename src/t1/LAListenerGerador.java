package t1;

public class LAListenerGerador extends LABaseListener{
    
    @Override
    public void enterPrograma(LAParser.ProgramaContext ctx){
        System.out.println("A");
    }
    
    @Override
    public void exitPrograma(LAParser.ProgramaContext ctx){
        System.out.println("F");
    }
    
    
}
