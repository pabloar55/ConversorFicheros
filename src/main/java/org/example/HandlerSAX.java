package org.example;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @author Pablo Armas
 */

public class HandlerSAX extends DefaultHandler {
    private final BufferedWriter writer;

    public HandlerSAX(String nombreFicheroTxt) throws IOException {
        this.writer = new BufferedWriter(new FileWriter(nombreFicheroTxt));
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        try {
            writer.write("Elemento: " + qName);
            writer.newLine();
            for (int i = 0; i < attributes.getLength(); i++) {
                writer.write("Elemento: " + attributes.getQName(i));
                writer.newLine();
                writer.write("Valor: " + attributes.getValue(i));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new SAXException("Error al escribir en el archivo TXT: " + e.getMessage(), e);
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        String content = new String(ch, start, length).trim();
        if (!content.isEmpty()) {
            try {
                writer.write("Valor: " + content);
                writer.newLine();
            } catch (IOException e) {
                throw new SAXException("Error al escribir en el archivo TXT: " + e.getMessage(), e);
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        try {
            writer.newLine();
        } catch (IOException e) {
            throw new SAXException("Error al escribir en el archivo TXT: " + e.getMessage(), e);
        }
    }

    @Override
    public void endDocument() throws SAXException {
        try {
            writer.close();
        } catch (IOException e) {
            throw new SAXException("Error al cerrar el archivo TXT: " + e.getMessage(), e);
        }
    }
}

