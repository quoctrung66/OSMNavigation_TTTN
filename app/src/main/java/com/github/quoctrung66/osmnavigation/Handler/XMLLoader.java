package com.github.quoctrung66.osmnavigation.Handler;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Created by QUOC TRUNG on 5/22/2016.
 */
public class XMLLoader {
    public Document getXmlDoc(String url) throws Exception {
        InputStream in = new ByteArrayInputStream(RequestHTTP.readUrl(url).getBytes("UTF-8"));
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(in);
        return document;
    }
}
