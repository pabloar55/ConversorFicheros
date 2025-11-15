package org.example;



import org.w3c.dom.*;

import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 *
 * @author Pablo Armas
 */
public class ConversorFicherosMain {
    public int tamanioEntradaDat;
    public String nombreEntrada;
    public ArrayList<Campo> arrayCampos;

    public static void main(String[] args) {
        ConversorFicherosMain c = new ConversorFicherosMain();
        Scanner sc = new Scanner(System.in);
        String nombreFichero;
        String tipo;
        while (true){
            System.out.println("Introduce el nombre del archivo con su extensión (cópialo a la raiz del proyecto primero) (Exit: q)");
            nombreFichero = sc.nextLine();
            if (nombreFichero.equals("q")){
                return;
            }
            if(!existe(nombreFichero)){
                System.out.println("El fichero no existe");
                continue;
            }
            if (nombreFichero.endsWith(".dat")) {
                System.out.println("¿Conoces la estructua del archivo? (s = Sí | Otra tecla = No)");
                String respuesta = sc.nextLine();
                if (respuesta.equals("s")) {
                    System.out.println("Introduce un fichero .txt con la estructura del archivo");
                    String ficheroEstructura = sc.nextLine();
                    if(c.guardarEstructura(ficheroEstructura))
                        c.tratarDat(nombreFichero);
                    else{
                        continue;
                    }
                } else {
                    System.out.println("Lo siento, no podemos hacer nada");
                }
            }
            System.out.println("¿A qué tipo de archivo lo quieres convertir? (pulsa 1 ,2, 3 o 4)\n 1.- txt \n 2.- dat \n 3.- properties \n 4.- xml");
            tipo = sc.nextLine();
            if (nombreFichero.endsWith(".txt")) {
                switch (tipo) {
                    case "1": {
                        System.out.println("introduce el nuevo nombre del fichero sin la extensión");
                        String nuevoNombre = sc.nextLine();
                        nuevoNombre = nuevoNombre.concat(".txt");
                        c.txtATxt(nombreFichero, nuevoNombre);
                        break;
                    }
                    case "2": {
                        c.txtADat(nombreFichero);
                        break;
                    }
                    case "3": {
                        c.txtAProperties(nombreFichero);
                        break;
                    }
                    case "4": {
                        System.out.println("No podemos hacer nada");
                    }
                }
            }
            if (nombreFichero.endsWith(".xml")) {
                switch (tipo) {
                    case "1": {
                        c.xmlATxt(nombreFichero);
                        break;
                    }
                    case "2": {
                        c.xmlADat(nombreFichero);
                        break;
                    }
                    case "3": {

                        break;
                    }
                    case "4": {
                        c.xmlAXML(nombreFichero);
                    }
                }
            }
        }

    }

    private static boolean existe(String nombreFichero) {
        File f = new File(nombreFichero);
        return f.exists();
    }

    private void txtATxt(String ruta, String nuevoNombre) {
        String linea;
        try (BufferedReader bf = new BufferedReader(new FileReader(ruta));
             BufferedWriter bw = new BufferedWriter(new FileWriter(nuevoNombre))) {
            while ((linea = bf.readLine()) != null) {
                bw.write(linea);
                bw.newLine();
            }
            System.out.println(nuevoNombre+" creado");
        } catch (IOException ex) {
            Logger.getLogger(ConversorFicherosMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void txtADat(String nombre) {
        File f = new File(nombre);
        String nuevoNombre = nombre.substring(0, nombre.length() - 3);
        nuevoNombre = nuevoNombre.concat("dat");
        System.out.println(nuevoNombre+" creado");
        File copia = new File(nuevoNombre);
        try (FileInputStream fis = new FileInputStream(f); FileOutputStream fos = new FileOutputStream(copia)) {
            int i;
            while ((i = fis.read()) != -1) {
                fos.write(i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void txtAProperties(String nombre) {
        String linea;
        File f = new File("copiaTxt.properties");

        try (BufferedReader br = new BufferedReader(new FileReader(nombre));
             BufferedWriter bw = new BufferedWriter(new FileWriter(f))) {

            while ((linea = br.readLine()) != null) {

                if (linea.matches(".+=.+")) {
                    bw.write(linea);
                    bw.newLine();
                } else {
                    System.out.println("No se puede copiar porque no sigue la estructura 'propiedad=valor'");
                    bw.close();
                    br.close();
                    f.delete();
                    return;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(ConversorFicherosMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tratarDat(String ficheroDat) {
        System.out.println("que desea hacer con los elementos:");
        System.out.println("1.- Borrar");
        System.out.println("2.- Modificar");
        System.out.println("3.- Añadir");
        System.out.println("4. Leer (ver información de un elemento)");
        Scanner sc = new Scanner(System.in);
        switch (sc.nextLine()) {
            case "1": {
                System.out.println("Introduce el ID");
                borrarEntradaDat(ficheroDat, sc.nextInt());
                break;
            }
            case "2": {
                System.out.println("Introduce el ID, el nombre del campo que quieras modificar y el nuevo valor ");
                String[] valores = sc.nextLine().split(" ");
                modificarEntradaDat(ficheroDat, Integer.parseInt(valores[0]), valores[1], valores[2]);
            }
            case "3": {
                System.out.println("Introduce los campos de la nueva entrada en orden");
                String[] valores = sc.nextLine().split(" ");
                aniadirEntradaDat(ficheroDat, valores);
            }
            case "4": {
                System.out.println("Introduce el ID");
                leerMostrarEntradaDat(ficheroDat, sc.nextInt());
                break;
            }
        }
        System.out.println("¿A qué tipo de archivo lo quieres convertir? (pulsa 1 ,2, 3 o 4)\n 1.- txt \n 2.- dat \n 3.- properties \n 4.- xml");
        String tipo = sc.nextLine();
        switch (tipo) {
            case "1": {
                System.out.println("introduce el nuevo nombre del fichero sin la extensión");
                String nuevoNombre = sc.nextLine();
                nuevoNombre = nuevoNombre.concat(".txt");
                System.out.println(nuevoNombre);
                datATxt(ficheroDat, nuevoNombre);
                break;
            }
            case "2": {
                datADat(ficheroDat);
                break;
            }
            case "3": {
                datAProperties(ficheroDat);
                break;
            }
            case "4": {
                datAXML(ficheroDat);
            }
        }
    }

    public boolean guardarEstructura(String estructuraTxt) {
        File estructuraF = new File(estructuraTxt);
        if(!estructuraF.exists())
        {
            System.out.println("El fichero de estructura no existe");
            return false;
        }
        try {
            FileReader estructuraFR = new FileReader(estructuraF);
            BufferedReader br = new BufferedReader(estructuraFR);
            nombreEntrada = br.readLine();
            br.readLine(); // nos saltamos el id que suponemos que es un entero y está al principio
            arrayCampos = new ArrayList<>();
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(" ");
                arrayCampos.add(new Campo(campos[0], campos[1], Integer.parseInt(campos[2])));
                tamanioEntradaDat += Integer.parseInt(campos[2]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    public void leerMostrarEntradaDat(String archivoDat, int id) {
        File datF = new File(archivoDat);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "r")) {
            while (rafDat.getFilePointer() < rafDat.length()) {
                if (id == rafDat.readInt()) {
                    System.out.println(nombreEntrada);
                    System.out.println();
                    for (Campo campo : arrayCampos) {
                        switch (campo.tipo) {
                            case "string": {
                                String contenido = rafDat.readUTF();
                                System.out.println(campo.nombre + ": " + contenido);
                                break;
                            }
                            case "int": {
                                System.out.println(campo.nombre + ": " + rafDat.readInt());
                                break;
                            }
                            case "double": {
                                System.out.println(campo.nombre + ": " + rafDat.readDouble());
                                break;
                            }
                            case "float": {
                                System.out.println(campo.nombre + ": " + rafDat.readFloat());
                                break;
                            }
                            case "char": {
                                System.out.println(campo.nombre + ": " + rafDat.readChar());
                                break;
                            }
                        }
                    }
                    break; // salimos del while si lo encuentra
                }
                rafDat.skipBytes(tamanioEntradaDat);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void borrarEntradaDat(String archivoDat, int id) {
        File datF = new File(archivoDat);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "rw")) {
            while (rafDat.getFilePointer() < rafDat.length()) {
                if (id == rafDat.readInt()) {
                    rafDat.seek(rafDat.getFilePointer() - 4);
                    byte[] b = new byte[tamanioEntradaDat + 4];
                    for (int i = 0; i < tamanioEntradaDat + 4; i++) {
                        b[i] = -1;
                    }
                    rafDat.write(b); // sustituimos la entrada por el valor -1
                    break; // salimos del while si lo encuentra
                }
                rafDat.skipBytes(tamanioEntradaDat);
            }
            System.out.println("borrado el " + nombreEntrada + "con id = " + id);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void modificarEntradaDat(String archivoDat, int id, String nombreCampo, String nuevoValor) {
        File datF = new File(archivoDat);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "rw")) {
            while (rafDat.getFilePointer() < rafDat.length()) {
                if (id == rafDat.readInt()) {
                    rafDat.seek(rafDat.getFilePointer() - 4);
                    int posicionBorrado = 0;
                    for (Campo campo : arrayCampos) {
                        if (campo.nombre.equals(nombreCampo)) {
                            rafDat.seek(rafDat.getFilePointer() + posicionBorrado);
                            switch (campo.tipo) {
                                case "string": {
                                    rafDat.writeUTF(nuevoValor);
                                    int bytesRelleno = campo.tamanio - nuevoValor.length();
                                    while (bytesRelleno > 0) {
                                        rafDat.writeUTF(" ");
                                        bytesRelleno--;
                                    }
                                    break;
                                }
                                case "int": {
                                    rafDat.writeInt(Integer.parseInt(nuevoValor));
                                    break;
                                }
                                case "double": {
                                    rafDat.writeDouble(Double.parseDouble(nuevoValor));
                                    break;
                                }
                                case "float": {
                                    rafDat.writeFloat(Float.parseFloat(nuevoValor));
                                    break;
                                }
                                case "char": {
                                    rafDat.writeChar(nuevoValor.charAt(0));
                                    break;
                                }
                            }
                        } else {
                            posicionBorrado += campo.tamanio;
                        }
                    }
                    break; // salimos del while si lo encuentra
                }
                rafDat.skipBytes(tamanioEntradaDat);
            }
            System.out.println("modificado el " + nombreEntrada + "con id = " + id + " el nuevo campo " + nombreCampo + " tiene como valor " + nuevoValor);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void aniadirEntradaDat(String archivoDat, String[] valores) {
        File datF = new File(archivoDat);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "rw")) {
            rafDat.seek(rafDat.length());
            rafDat.writeInt(Integer.parseInt(valores[0])); // Suponemos que el primer valor es el id (entero)
            for (int i = 1; i < valores.length; i++) {
                Campo campo = arrayCampos.get(i);
                switch (campo.tipo) {
                    case "string": {
                        rafDat.writeUTF(valores[i]);
                        break;
                    }
                    case "int": {
                        rafDat.writeInt(Integer.parseInt(valores[i]));
                        break;
                    }
                    case "double": {
                        rafDat.writeDouble(Double.parseDouble(valores[i]));
                        break;
                    }
                    case "float": {
                        rafDat.writeFloat(Float.parseFloat(valores[i]));
                        break;
                    }
                    case "char": {
                        rafDat.writeChar(valores[i].charAt(0));
                        break;
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void datATxt(String nombreFichero, String nuevoNombre){
        try {
            BufferedReader bfr = new BufferedReader(new FileReader(nombreFichero));
            BufferedWriter bfw = new BufferedWriter(new FileWriter(nuevoNombre));
            String linea;
            while ((linea = bfr.readLine()) != null) {
                bfw.write(linea);
                bfw.newLine();
            }
            bfr.close();
            bfw.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void datADat(String nombreFichero){
        File datF = new File(nombreFichero);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "r")) {
            ArrayList<Entrada> entradas = new ArrayList<>();
            while (rafDat.getFilePointer()<rafDat.length()){
                Entrada entrada = new Entrada();
                Campo campoId = new Campo(String.valueOf(rafDat.readInt()), "id", "int");
                entrada.addCampo(campoId);
                for (Campo campo : arrayCampos){
                    switch (campo.tipo) {
                        case "string": {
                            entrada.addCampo(new Campo(rafDat.readUTF(), campo.nombre, campo.tipo));
                            break;
                        }
                        case "int": {
                            entrada.addCampo(new Campo(String.valueOf(rafDat.readInt()), campo.nombre, campo.tipo));
                            break;
                        }
                        case "double": {
                            entrada.addCampo(new Campo(String.valueOf(rafDat.readDouble()), campo.nombre, campo.tipo));
                            break;
                        }
                        case "float": {
                            entrada.addCampo(new Campo(String.valueOf(rafDat.readFloat()), campo.nombre, campo.tipo));
                            break;
                        }
                        case "char": {
                            entrada.addCampo(new Campo(String.valueOf(rafDat.readChar()), campo.nombre, campo.tipo));
                            break;
                        }
                    }
                }
                entradas.add(entrada);
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("copiaDat.dat"));
            oos.writeObject(entradas);
        }  catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void datAXML(String nombreFichero){
        File datF = new File(nombreFichero);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "r")){
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document document = implementation.createDocument(null, nombreEntrada, null);
            document.setXmlVersion("1.0");
            while (rafDat.getFilePointer() < rafDat.length()) {
                Element raiz = document.createElement(nombreEntrada);
                document.getDocumentElement().appendChild(raiz);
                raiz.setAttribute("id", String.valueOf(rafDat.readInt()));
                raiz.setIdAttribute("id", true);
                for (Campo campo : arrayCampos){
                    switch (campo.tipo) {
                        case "string": {
                            CrearElementos(campo.nombre, rafDat.readUTF(), raiz , document);
                            break;
                        }
                        case "int": {
                            CrearElementos(campo.nombre, String.valueOf(rafDat.readInt()), raiz , document);
                            break;
                        }
                        case "double": {
                            CrearElementos(campo.nombre, String.valueOf(rafDat.readDouble()), raiz , document);
                            break;
                        }
                        case "float": {
                            CrearElementos(campo.nombre, String.valueOf(rafDat.readFloat()), raiz , document);
                            break;
                        }
                        case "char": {
                            CrearElementos(campo.nombre, String.valueOf(rafDat.readChar()), raiz , document);
                            break;
                        }
                    }
                }
            }
            document.getDocumentElement().normalize();
            Source source = new DOMSource(document);
            Result result = new StreamResult(new File("datAXML.xml"));
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
        } catch (ParserConfigurationException | IOException | TransformerException e) {
            throw new RuntimeException(e);
        }
    }
    public void datAProperties(String nombreFichero){

    }
    private void CrearElementos(String nombre, String valor, Element raiz, Document documento) {
        Element aux = documento.createElement(nombre);
        Text texto = documento.createTextNode(valor);
        raiz.appendChild(aux);
        aux.appendChild(texto);
    }
    private void xmlAXML(String nombreFichero){
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document documentEntrada = builder.parse(new File(nombreFichero));
            documentEntrada.getDocumentElement().normalize();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document documentSalida = implementation.createDocument(
                    documentEntrada.getDocumentElement().getNamespaceURI(),
                    documentEntrada.getDocumentElement().getNodeName(),
                    null
            );
            documentSalida.setXmlVersion("1.0");
            Node nodoRaizEntrada = documentEntrada.getDocumentElement();
            Node nodoRaizSalida = documentSalida.importNode(nodoRaizEntrada, true);
            documentSalida.appendChild(nodoRaizSalida);
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            Source source = new DOMSource(documentSalida);
            StreamResult result = new StreamResult(new File("copia.xml"));
            transformer.transform(source, result);

        } catch (Exception ignored) {
        }
    }
    public void xmlADat(String nombreFichero) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File("copia.dat"));
            document.getDocumentElement().normalize();

            try (RandomAccessFile rafDat = new RandomAccessFile(new File(nombreFichero), "rw")) {
                NodeList nodosEntradas = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodosEntradas.getLength(); i++) {
                    Node nodo = nodosEntradas.item(i);
                    if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                        Element entrada = (Element) nodo;
                        String id = entrada.getAttribute("id");
                        rafDat.writeInt(Integer.parseInt(id));
                        for (Campo campo : arrayCampos) {
                            String valor = entrada.getElementsByTagName(campo.nombre).item(0).getTextContent();
                            switch (campo.tipo) {
                                case "string":
                                    rafDat.writeUTF(valor);
                                    break;
                                case "int":
                                    rafDat.writeInt(Integer.parseInt(valor));
                                    break;
                                case "double":
                                    rafDat.writeDouble(Double.parseDouble(valor));
                                    break;
                                case "float":
                                    rafDat.writeFloat(Float.parseFloat(valor));
                                    break;
                                case "char":
                                    rafDat.writeChar(valor.charAt(0));
                                    break;
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir XML a DAT: " + e.getMessage(), e);
        }
    }
    public void xmlATxt(String nombreFicheroXML) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            HandlerSAX handler = new HandlerSAX("copia.txt");
            saxParser.parse(new File(nombreFicheroXML), handler);
        } catch (Exception e) {
            throw new RuntimeException("Error al convertir XML a TXT: " + e.getMessage(), e);
        }
    }


}
