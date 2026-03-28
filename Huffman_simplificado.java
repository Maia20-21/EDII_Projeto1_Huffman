/*
 * Projeto 1 | Compressão de Arquivos com o Algoritmo de Huffman
 * Turma 04G
 *
 * Bruna Amorim Maia (RA 10431883)
 * Sofia de Oliveira Cavalcanti (RA 10723361)
 * Vinícius Pereira Rodrigues (RA 10729470)
 */

// Huffman.java

import java.io.*;
import java.util.*;

public class Huffman {

    private static final int ASCII = 256;

    public static void main(String[] args) {
        if (args.length != 3) {
            System.out.println("Uso:");
            System.out.println("java -jar huffman.jar -c <in> <out>");
            System.out.println("java -jar huffman.jar -d <in> <out>");
            return;
        }

        try {
            if (args[0].equals("-c")) comp(args[1], args[2]);
            else if (args[0].equals("-d")) decomp(args[1], args[2]);
            else System.out.println("Opção inválida");
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
        }
    }

    // ---------------- COMPRESSÃO ----------------

    static void comp(String in, String out) throws IOException {
        long t0 = System.nanoTime();

        byte[] data = new FileInputStream(in).readAllBytes();

        int[] freq = freq(data);
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 1: Tabela de Frequencia de Caracteres");
        System.out.println("--------------------------------------------------");
        // printFreq(freq);

        MinHeap heap = heap(freq);
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 2: Min-Heap Inicial (Vetor)");
        System.out.println("--------------------------------------------------");
        // for (No n : heap.view()) {
        //     System.out.println(n);
        // }

        No root = build(heap);
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 3: Arvore de Huffman");
        System.out.println("--------------------------------------------------");
        // printTree(root, "");

        String[] cod = new String[ASCII];
        gen(root, "", cod);
        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 4: Tabela de Codigos de Huffman");
        System.out.println("--------------------------------------------------");
        // printCod(cod, freq);

        byte[] comp = encode(data, cod);
        write(out, freq, comp);

        long t1 = System.nanoTime();

        File f1 = new File(in);
        File f2 = new File(out);

        double taxa = 100.0 * (1 - (double)f2.length() / f1.length());

        System.out.println("--------------------------------------------------");
        System.out.println("ETAPA 5: Resumo da Compressao");
        System.out.println("--------------------------------------------------");
        System.out.printf("Tamanho original....: %d bytes\n", f1.length());
        System.out.printf("Tamanho comprimido..: %d bytes\n", f2.length());
        System.out.printf("Taxa de compressao..: %.2f%%\n", taxa);
        System.out.println("--------------------------------------------------");

        System.out.printf("Tempo de compressao: %.2f ms\n", (t1 - t0)/1e6);
    }

    static int[] freq(byte[] d) {
        int[] f = new int[ASCII];
        for (byte b : d) f[b & 0xFF]++;
        return f;
    }

    static void printFreq(int[] t) {
        for (int i = 0; i < ASCII; i++) {
            if (t[i] > 0) {
                System.out.printf("Caractere '%c' (ASCII: %d): %d\n", (char)i, i, t[i]);
            }
        }
    }

    static MinHeap heap(int[] f) {
        MinHeap h = new MinHeap();
        for (int i = 0; i < ASCII; i++)
            if (f[i] > 0)
                h.add(new No((char)i, f[i]));
        return h;
    }

    static No build(MinHeap h) {
        while (h.size() > 1) {
            No a = h.pop();
            No b = h.pop();
            h.add(new No(a.getF() + b.getF(), a, b));
        }
        return h.pop();
    }

    static void printTree(No n, String p) {
        if (n == null) return;

        String nome = n.folha() ? "'" + n.getC() + "'" : "RAIZ/Nó";
        System.out.println(p + "-> (" + nome + ", " + n.getF() + ")");

        if (!n.folha()) {
            printTree(n.getEsq(), p + "  |--(0)");
            printTree(n.getDir(), p + "  |--(1)");
        }
    }

    static void gen(No n, String c, String[] tab) {
        if (n == null) return;
        if (n.folha()) tab[n.getC()] = c;
        else {
            gen(n.getEsq(), c + "0", tab);
            gen(n.getDir(), c + "1", tab);
        }
    }

    static void printCod(String[] c, int[] f) {
        for (int i = 0; i < ASCII; i++) {
            if (f[i] > 0 && c[i] != null) {
                System.out.printf("Caractere '%c': %s\n", (char)i, c[i]);
            }
        }
    }

    static byte[] encode(byte[] d, String[] tab) {
        StringBuilder sb = new StringBuilder();
        for (byte b : d) sb.append(tab[b & 0xFF]);

        String bits = sb.toString();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int pad = bits.length() % 8;
        if (pad == 0) pad = 8;
        out.write((byte) pad);

        for (int i = 0; i < bits.length(); i += 8) {
            String s = (i + 8 <= bits.length()) ? bits.substring(i, i+8) : bits.substring(i);
            out.write(Integer.parseInt(s, 2));
        }

        return out.toByteArray();
    }

    static void write(String name, int[] f, byte[] d) throws IOException {
        try (ObjectOutputStream o = new ObjectOutputStream(new FileOutputStream(name))) {
            o.writeObject(f);
            o.write(d);
        }
    }

    // ---------------- DESCOMPRESSÃO ----------------

    static void decomp(String in, String out) throws IOException {
        long t0 = System.nanoTime();

        try (ObjectInputStream o = new ObjectInputStream(new FileInputStream(in))) {

            int[] f = (int[]) o.readObject();
            MinHeap h = heap(f);
            No root = build(h);

            byte[] comp = o.readAllBytes();
            byte[] dec = decode(root, comp);

            new FileOutputStream(out).write(dec);
        } catch (ClassNotFoundException e) {
            throw new IOException("Arquivo inválido");
        }

        long t1 = System.nanoTime();

        System.out.println("Arquivo descomprimido com sucesso!");
        System.out.printf("Tempo de descompressão: %.2f ms\n", (t1 - t0)/1e6);
    }

    static byte[] decode(No root, byte[] d) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        int pad = d[0];
        if (pad == 0) pad = 8;

        StringBuilder bits = new StringBuilder();

        for (int i = 1; i < d.length; i++) {
            String s = Integer.toBinaryString(d[i] & 0xFF);

            if (i < d.length -1 || pad == 8)
                s = String.format("%8s", s).replace(' ', '0');
            else
                s = String.format("%" + pad + "s", s).replace(' ', '0');

            bits.append(s);
        }

        No cur = root;

        for (int i = 0; i < bits.length(); i++) {
            cur = (bits.charAt(i) == '0') ? cur.getEsq() : cur.getDir();

            if (cur.folha()) {
                out.write(cur.getC());
                cur = root;
            }
        }

        return out.toByteArray();
    }
}