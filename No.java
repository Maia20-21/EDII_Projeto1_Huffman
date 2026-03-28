/*
 * Projeto 1 | Compressão de Arquivos com o Algoritmo de Huffman
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinícius Pereira Rodrigues (RA 10729470)
 */

// No.java

import java.io.Serializable;

// nó da árvore
public class No implements Comparable<No>, Serializable {

    private final char c;
    private final int f;
    private No esq, dir;

    // nó interno
    public No(int f, No esq, No dir) {
        this.c = '\0';
        this.f = f;
        this.esq = esq;
        this.dir = dir;
    }

    // nó folha
    public No(char c, int f) {
        this.c = c;
        this.f = f;
    }

    public char getC() { return c; }
    public int getF() { return f; }
    public No getEsq() { return esq; }
    public No getDir() { return dir; }

    public boolean folha() {
        return esq == null && dir == null;
    }

    @Override
    public int compareTo(No o) {
        return Integer.compare(this.f, o.f);
    }

    @Override
    public String toString() {
        return folha() ? "(" + c + "," + f + ")" : "(*," + f + ")";
    }
}