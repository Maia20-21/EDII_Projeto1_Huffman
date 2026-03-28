/*
 * Projeto 1 | Compressão de Arquivos com o Algoritmo de Huffman
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinícius Pereira Rodrigues (RA 10729470)
 */

// MinHeap.java

import java.util.ArrayList;
import java.util.NoSuchElementException;

public class MinHeap {

    private final ArrayList<No> h = new ArrayList<>();

    public void add(No n) {
        h.add(n);
        subir(h.size() - 1);
    }

    public No pop() {
        if (h.isEmpty()) throw new NoSuchElementException();

        No min = h.get(0);
        No last = h.remove(h.size() - 1);

        if (!h.isEmpty()) {
            h.set(0, last);
            descer(0);
        }
        return min;
    }

    private void subir(int i) {
        while (i > 0) {
            int p = (i - 1) / 2;
            if (h.get(i).compareTo(h.get(p)) < 0) {
                swap(i, p);
                i = p;
            } else break;
        }
    }

    private void descer(int i) {
        int m = i;
        int e = 2 * i + 1;
        int d = 2 * i + 2;

        if (e < h.size() && h.get(e).compareTo(h.get(m)) < 0) m = e;
        if (d < h.size() && h.get(d).compareTo(h.get(m)) < 0) m = d;

        if (m != i) {
            swap(i, m);
            descer(m);
        }
    }

    private void swap(int i, int j) {
        No t = h.get(i);
        h.set(i, h.get(j));
        h.set(j, t);
    }

    public int size() { return h.size(); }

    public ArrayList<No> view() {
        return new ArrayList<>(h);
    }
}