package org.example;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Pablo Armas
 */

public class Entrada implements Serializable {
    ArrayList<Campo> campos = new ArrayList<>();
    String nombreEntrada;
    public void addCampo(Campo campo){
        campos.add(campo);
    }
    public Entrada(){
    }

    public String getNombreEntrada() {
        return nombreEntrada;
    }

    public void setNombreEntrada(String nombreEntrada) {
        this.nombreEntrada = nombreEntrada;
    }
}

