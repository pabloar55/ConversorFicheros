package org.example;

public class Campo {
    String nombre;
    String tipo;
    int tamanio;
    String valor;
    public Campo(String nombre, String tipo, int tamanio) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.tamanio = tamanio;
    }
    public Campo (String valor, String nombre, String tipo){
        this.valor = valor;
        this.nombre = nombre;
        this.tipo = tipo;
    }
}

