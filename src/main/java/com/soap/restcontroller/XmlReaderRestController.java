package com.soap.restcontroller;

import com.soap.errorhandler.XmlErrorHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;


import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class XmlReaderRestController {

    @PostMapping("/process")
    public ResponseEntity<?> processXml(@RequestBody String xml) throws SAXException, IOException {
        Document doc;
        try {
            DocumentBuilder builder =  DocumentBuilderFactory.newInstance().newDocumentBuilder();
            doc = builder.parse(new InputSource(new StringReader(xml)));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Invalid XML format");
        }
            // create a SchemaFactory capable of understanding WXS schemas
//            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
//            Source schemaFile = new StreamSource(ResourceUtils.getFile("classpath:PersonNotMatch.xsd"));
//            Schema schema = factory.newSchema(schemaFile);
//            Validator validator = schema.newValidator();
//            XmlErrorHandler xsdErrorHandler = new XmlErrorHandler();
//            validator.setErrorHandler(xsdErrorHandler);
//            validator.validate(new DOMSource(doc));
//            if(xsdErrorHandler.getExceptions().size()>0){
//                return new ResponseEntity<>(xsdErrorHandler.getExceptions(), HttpStatus.OK);
//            }
        doc.getDocumentElement().normalize();
        Map<String, String> map = new HashMap<>();
        traverseXML(doc.getDocumentElement(), map, "", -1, null);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
            return ResponseEntity.ok(map);

    }

    private static Integer traverseXML(Node node, Map<String, String> nodeMap, String parent,
                                    Integer index, String currentParentNodeArray) {
//        int currentIndex = index;
//        String currentParentNodeArray = currentParentNode;
        if (node.getNodeType() == Node.ELEMENT_NODE) {
            String nodeName = node.getNodeName();
            String key = parent.isEmpty() ? nodeName : parent + "." + nodeName;

            if (node.hasChildNodes()) {
                NodeList nodeList = node.getChildNodes();
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node childNode = nodeList.item(i);
                    if (key.endsWith("LL")) {
                        index = index>0 ? index : 0;
                        currentParentNodeArray = key;
                    }
                    index = traverseXML(childNode, nodeMap, key, index, currentParentNodeArray);
                }
            }
        }else {
            String value = node.getTextContent().trim();
            if(parent!= null && value!=null && value.length()>0){


                    String key;
                    if(index>=0 && parent.contains(currentParentNodeArray)){
                        System.out.println(index);
                        key = parent+'['+index+']';
                    }else {
                        key = parent;
                    }
                    if(nodeMap.containsKey(key)){
                        index++;
                        key = parent+'['+index+']';
                        System.out.println(key);
                    }
                    nodeMap.put(key, value);
            }
        }
        return index;
    }

}
