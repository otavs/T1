package t1;

import java.util.LinkedList;
import java.util.List;

public class PilhaDeTabelas {
    private LinkedList<TabelaDeSimbolos> pilha;

    public PilhaDeTabelas() {
        pilha = new LinkedList<TabelaDeSimbolos>();
    }

    public void empilhar(TabelaDeSimbolos ts) {
        pilha.push(ts);
    }

    public TabelaDeSimbolos topo() {
        return pilha.peek();
    }

    public boolean existeSimbolo(String nome) {
        for (TabelaDeSimbolos ts : pilha) {
            if (ts.existeSimbolo(nome)) {
                return true;
            }
        }
        return false;
    }

    public void desempilhar() {
        TabelaDeSimbolos ret = pilha.pop();
    }
    
    public String tipoDeDadoDoSimbolo(String nome){
        String tipo = null;
        for(TabelaDeSimbolos ts : pilha){
            EntradaTabelaDeSimbolos etds = ts.getSimbolo(nome);
            if(etds != null) {
                return etds.getTipoDeDado();
            }
        }
        return "";
    }
}
