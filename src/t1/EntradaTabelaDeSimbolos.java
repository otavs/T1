package t1;

// Classe utilizada para armazenar as entradas de tabelas de símbolos com suas respectivas informações
public class EntradaTabelaDeSimbolos {
    private String nome, tipo, tipoDeDado;
    
    public EntradaTabelaDeSimbolos(String nome, String tipo, String tipoDeDado) {
        this.nome = nome;
        this.tipo = tipo;
        this.tipoDeDado = tipoDeDado;
    }
    
    public String getNome() {
        return nome;
    }
    
    public String getTipo() {
        return tipo;
    }
    
    public String getTipoDeDado(){
        return tipoDeDado;
    }
}
