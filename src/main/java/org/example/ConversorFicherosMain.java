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
 * @author Pablo Armas
 */
public class ConversorFicherosMain {
    public int tamanioEntradaDat;
    public String nombreEntrada;
    public ArrayList<Campo> arrayCamposDat;

    public static void main(String[] args) throws IOException {
        File f = new File("h.dat");
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        raf.writeInt(1);
        raf.writeChars("pablo");         //Datos de prueba
        raf.writeInt(2);
        raf.writeChars("ana11");
        ConversorFicherosMain c = new ConversorFicherosMain();
        Scanner sc = new Scanner(System.in);
        String nombreFichero;
        String tipo;
        while (true) {
            System.out.println("------------------CONVERSOR DE FICHEROS------------------");
            System.out.println("Introduce el nombre del archivo con su extensión (cópialo a la raiz del proyecto primero) (Exit: q)");
            nombreFichero = sc.nextLine();
            if (nombreFichero.equals("q")) {
                return;
            }
            if (!existe(nombreFichero)) {
                System.out.println("El fichero no existe");
                continue;
            }
            if (nombreFichero.endsWith(".properties")) {
                System.out.println("No se hace nada con este tipo de documento");
                continue;
            }
            if (nombreFichero.endsWith(".dat")) {
                System.out.println("¿Conoces la estructua del archivo? (s = Sí | Otra tecla = No)");
                String respuesta = sc.nextLine();
                if (respuesta.equals("s")) {
                    System.out.println("Introduce un fichero (con su extensión .txt) con la estructura del archivo");
                    String ficheroEstructura = sc.nextLine();
                    if (c.guardarEstructura(ficheroEstructura))
                        c.tratarDat(nombreFichero);
                    else {
                        continue;
                    }
                } else {
                    System.out.println("Lo siento, no podemos hacer nada");
                }
            }
            if (nombreFichero.endsWith(".txt")) {
                System.out.println("¿A qué tipo de archivo lo quieres convertir? (pulsa 1 ,2, 3 o 4)\n 1.- txt \n 2.- dat \n 3.- properties \n 4.- xml");
                tipo = sc.nextLine();
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
                System.out.println("¿A qué tipo de archivo lo quieres convertir? (pulsa 1 ,2, 3 o 4)\n 1.- txt \n 2.- dat \n 3.- properties \n 4.- xml");
                tipo = sc.nextLine();
                switch (tipo) {
                    case "1": {
                        c.xmlATxt(nombreFichero);
                        break;
                    }
                    case "2": {
                        System.out.println("¿Conoces la estructua del archivo? (s = Sí | Otra tecla = No)");
                        String respuesta = sc.nextLine();
                        if (respuesta.equals("s")) {
                            System.out.println("Introduce un fichero (con su extensión .txt) con la estructura del archivo");
                            String ficheroEstructura = sc.nextLine();
                            if (c.guardarEstructura(ficheroEstructura))
                                c.xmlADat(nombreFichero);
                            else {
                                continue;
                            }
                        } else {
                            System.out.println("Lo siento, no podemos hacer nada");
                        }
                        break;
                    }
                    case "3": {
                        c.xmlAProperties(nombreFichero);
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
            System.out.println(nuevoNombre + " creado");
        } catch (IOException ex) {
            Logger.getLogger(ConversorFicherosMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void txtADat(String nombre) {
        File f = new File(nombre);
        String nuevoNombre = nombre.substring(0, nombre.length() - 3);
        nuevoNombre = nuevoNombre.concat("dat");
        System.out.println(nuevoNombre + " creado");
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
            System.out.println("copiaTxt.properties creado");
        } catch (IOException ex) {
            Logger.getLogger(ConversorFicherosMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void tratarDat(String ficheroDat) {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("¿Qué desea hacer con las entradas? (pulsa 1-4 para operaciones, o c para convertir y salir)");
            System.out.println("1.- Borrar");
            System.out.println("2.- Modificar");
            System.out.println("3.- Añadir");
            System.out.println("4.- Leer (ver información de una entrada)");
            String opcion = sc.nextLine();
            if (opcion.equals("c")) {
                break;
            }
            switch (opcion) {
                case "1": {
                    System.out.println("Introduce el ID");
                    borrarEntradaDat(ficheroDat, sc.nextInt());
                    sc.nextLine(); //limpiar el buffer siempre después de un NextInt
                    break;
                }
                case "2": {
                    System.out.println("Introduce el ID, el nombre del campo que quieras modificar y el nuevo valor ");
                    String[] valores = sc.nextLine().split(" ");
                    modificarEntradaDat(ficheroDat, Integer.parseInt(valores[0]), valores[1], valores[2]);
                    break;
                }
                case "3": {
                    System.out.println("Introduce los campos de la nueva entrada en orden");
                    String[] valores = sc.nextLine().split(" ");
                    aniadirEntradaDat(ficheroDat, valores);
                    break;
                }
                case "4": {
                    System.out.println("Introduce el ID");
                    leerMostrarEntradaDat(ficheroDat, sc.nextInt());
                    sc.nextLine();
                    break;
                }
            }
        }
        System.out.println("¿A qué tipo de archivo lo quieres convertir? (pulsa 1 ,2, 3 o 4)\n 1.- txt \n 2.- dat \n 3.- properties \n 4.- xml");
        String tipo = sc.nextLine();
        switch (tipo) {
            case "1": {
                System.out.println("introduce el nuevo nombre del fichero sin la extensión");
                String nuevoNombre = sc.nextLine();
                nuevoNombre = nuevoNombre.concat(".txt");
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
        if (!estructuraF.exists()) {
            System.out.println("El fichero de estructura no existe");
            return false;
        }
        try {
            FileReader estructuraFR = new FileReader(estructuraF);
            BufferedReader br = new BufferedReader(estructuraFR);
            nombreEntrada = br.readLine();
            arrayCamposDat = new ArrayList<>();
            tamanioEntradaDat = 0;
            String linea;
            while ((linea = br.readLine()) != null) {
                String[] campos = linea.split(" ");
                arrayCamposDat.add(new Campo(campos[0], campos[1], Integer.parseInt(campos[2])));
                tamanioEntradaDat += Integer.parseInt(campos[2]);
            }
            br.close();
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
                    System.out.println("encontrado");
                    for (Campo campo : arrayCamposDat) {
                        String valor = readCampoValor(rafDat, campo);
                        System.out.println(campo.nombre + ": " + valor.trim());
                    }
                    return;
                }
                rafDat.skipBytes(tamanioEntradaDat - 4);
            }
            System.out.println("No encontrado o borrado");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void borrarEntradaDat(String archivoDat, int id) {
        File datF = new File(archivoDat);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "rw")) {
            while (rafDat.getFilePointer() < rafDat.length()) {
                if (id == rafDat.readInt()) {
                    rafDat.writeInt(-1);// sustituimos la entrada por el valor -1
                    System.out.println("Borrado lógicamente");
                    return; // salimos del while si lo encuentra
                }
                rafDat.skipBytes(tamanioEntradaDat - 4);
            }
            System.out.println("No encontrado");
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
                    for (Campo campo : arrayCamposDat) {
                        if (campo.nombre.equals(nombreCampo)) {
                            rafDat.seek(rafDat.getFilePointer() + posicionBorrado);
                            writeCampoValor(rafDat, campo, nuevoValor);
                            System.out.println("Modificado");
                        } else {
                            posicionBorrado += campo.tamanio;
                        }
                    }
                    return; // salimos del while si lo encuentra
                }
                rafDat.skipBytes(tamanioEntradaDat - 4);
            }
            System.out.println("Entrada no encontrada");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void aniadirEntradaDat(String archivoDat, String[] valores) {
        if (valores.length != arrayCamposDat.size()) {
            System.out.println("Numero de valores incorrecto");
            return;
        }
        File datF = new File(archivoDat);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "rw")) {
            rafDat.seek(rafDat.length());
            for (int i = 0; i < valores.length; i++) {
                Campo campo = arrayCamposDat.get(i);
                writeCampoValor(rafDat, campo, valores[i]);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private String readCampoValor(RandomAccessFile raf, Campo campo) throws IOException {
        String valor;
        switch (campo.tipo) {
            case "string" -> {
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < campo.tamanio / 2; i++) sb.append(raf.readChar());
                valor = sb.toString();
            }
            case "int" -> valor = String.valueOf(raf.readInt());
            case "double" -> valor = String.valueOf(raf.readDouble());
            case "float" -> valor = String.valueOf(raf.readFloat());
            case "char" -> valor = String.valueOf(raf.readChar());
            default -> valor = "";
        }
        return valor;
    }

    private void writeCampoValor(RandomAccessFile raf, Campo campo, String valor) throws IOException {
        switch (campo.tipo) {
            case "string" -> {
                raf.writeChars(valor);
                int posicionesRelleno = campo.tamanio/2 - valor.length();
                while (posicionesRelleno > 0) {
                    raf.writeChar(' ');
                    posicionesRelleno--;
                }
            }
            case "int" -> raf.writeInt(Integer.parseInt(valor));
            case "double" -> raf.writeDouble(Double.parseDouble(valor));
            case "float" -> raf.writeFloat(Float.parseFloat(valor));
            case "char" -> raf.writeChar(valor.charAt(0));
        }
    }

    public void datATxt(String nombreFichero, String nuevoNombre) {
        try {
            File txtF = new File(nuevoNombre);
            txtF.createNewFile();
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

    public void datADat(String nombreFichero) {
        File datF = new File(nombreFichero);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "r")) {
            ArrayList<Entrada> entradas = new ArrayList<>();
            while (rafDat.getFilePointer() < rafDat.length()) {
                Entrada entrada = new Entrada();
                for (Campo campo : arrayCamposDat) {
                    String valor = readCampoValor(rafDat, campo);
                    entrada.addCampo(new Campo(valor, campo.nombre, campo.tipo));
                }
                entradas.add(entrada);
            }
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("copiaDat.dat"));
            oos.writeObject(entradas);
            System.out.println("copiaDat.dat creado");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void datAXML(String nombreFichero) {
        File datF = new File(nombreFichero);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "r")) {
            DocumentBuilder builder = factory.newDocumentBuilder();
            DOMImplementation implementation = builder.getDOMImplementation();
            Document document = implementation.createDocument(null, nombreEntrada, null);
            document.setXmlVersion("1.0");
            while (rafDat.getFilePointer() < rafDat.length()) {
                Element raiz = document.createElement(nombreEntrada);
                document.getDocumentElement().appendChild(raiz);
                raiz.setAttribute("id", String.valueOf(rafDat.readInt()));
                raiz.setIdAttribute("id", true);
                for (Campo campo : arrayCamposDat) {
                    String valor = readCampoValor(rafDat, campo);
                    CrearElementos(campo.nombre, valor, raiz, document);
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

    public void datAProperties(String nombreFichero) {
        File datF = new File(nombreFichero);
        try (RandomAccessFile rafDat = new RandomAccessFile(datF, "r")) {

            while (rafDat.getFilePointer() < rafDat.length()) {
                int id = rafDat.readInt();
                if (id < 0) {
                    rafDat.seek(rafDat.getFilePointer()+tamanioEntradaDat);
                    continue;
                }
                String nombreArchivoProp = nombreEntrada + id + ".properties";
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(nombreArchivoProp))) {
                    for (Campo campo : arrayCamposDat) {
                        String valor = readCampoValor(rafDat, campo);
                        bw.write(campo.nombre + "=" + valor.trim());
                        bw.newLine();
                    }
                }
            }
            System.out.println("Todos los archivos .properties se han creado");

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void CrearElementos(String nombre, String valor, Element raiz, Document documento) {
        Element aux = documento.createElement(nombre);
        Text texto = documento.createTextNode(valor);
        raiz.appendChild(aux);
        aux.appendChild(texto);
    }

    private void xmlAXML(String nombreFichero) {
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
            System.out.println("copia.xml creado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void xmlADat(String nombreFichero) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(nombreFichero));
            document.getDocumentElement().normalize();

            try (RandomAccessFile rafDat = new RandomAccessFile(new File("copia.dat"), "rw")) {
                NodeList nodosEntradas = document.getDocumentElement().getChildNodes();
                for (int i = 0; i < nodosEntradas.getLength(); i++) {
                    Node nodo = nodosEntradas.item(i);
                    if (nodo.getNodeType() == Node.ELEMENT_NODE) {
                        Element entrada = (Element) nodo;
                        String id = entrada.getAttribute("id");
                        rafDat.writeInt(Integer.parseInt(id));
                        for (Campo campo : arrayCamposDat) {
                            String valor = entrada.getElementsByTagName(campo.nombre).item(0).getTextContent();
                            writeCampoValor(rafDat, campo, valor);
                        }
                    }
                }
            }
            System.out.println("copia.dat creado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void xmlATxt(String nombreFicheroXML) {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            SAXParser saxParser = factory.newSAXParser();
            HandlerSAX handler = new HandlerSAX("copia.txt");
            saxParser.parse(new File(nombreFicheroXML), handler);
            System.out.println("copia.txt creado");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void xmlAProperties(String nombreFichero) {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(nombreFichero));
            document.getDocumentElement().normalize();
            String rootName = document.getDocumentElement().getTagName();
            String entryName = rootName.endsWith("s") ? rootName.substring(0, rootName.length() - 1) : rootName;
            NodeList entries = document.getElementsByTagName(entryName);
            for (int i = 0; i < entries.getLength(); i++) {
                Element entry = (Element) entries.item(i);
                NodeList fields = entry.getChildNodes();
                String id = null;
                for (int j = 0; j < fields.getLength(); j++) {
                    Node field = fields.item(j);
                    if (field.getNodeType() == Node.ELEMENT_NODE && field.getNodeName().equals("id")) {
                        id = field.getTextContent();
                    }
                }
                if (id == null)
                    id = String.valueOf(i + 1);
                String fileName = entryName + id + ".properties";
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(fileName))) {
                    for (int j = 0; j < fields.getLength(); j++) {
                        Node field = fields.item(j);
                        if (field.getNodeType() == Node.ELEMENT_NODE) {
                            bw.write(field.getNodeName() + "=" + field.getTextContent());
                            bw.newLine();
                        }
                    }
                }
            }
            System.out.println("Archivos .properties creados");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
