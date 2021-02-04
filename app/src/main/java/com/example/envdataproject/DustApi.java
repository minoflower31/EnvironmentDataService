package com.example.envdataproject;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class DustApi {
    private String dust;
    private String o3;

    private static final String URL_STRING = "http://openapi.airkorea.or.kr/openapi/services/rest/ArpltnInforInqireSvc/getCtprvnRltmMesureDnsty";

    public DustApi(String mLocationName) {
        StringBuilder builder = new StringBuilder(URL_STRING);
        try {
            builder.append("?").append(URLEncoder.encode("ServiceKey", "UTF-8")).append("=jrwqtLVZlEX%2B36vApnSxZMm%2FIp%2BIu1dmnbAysBy2XP721fA1SjZF6MC3uqOzP06mhSCHFyEqJhWoIcs%2BuPnAbg%3D%3D");
            builder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8"));
            builder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8"));
            builder.append("&").append(URLEncoder.encode("sidoName", "UTF-8")).append("=").append(URLEncoder.encode(mLocationName, "UTF-8"));
            builder.append("&").append(URLEncoder.encode("ver", "UTF-8")).append("=").append(URLEncoder.encode("0", "UTF-8"));

            URL url = new URL(builder.toString());

            extractData(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getDust() {
        return dust;
    }

    public String getO3() {
        return o3;
    }

    private void extractData(URL url) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.toString());
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getElementsByTagName("item");
            for(int i = 0; i<nodeList.getLength(); i++) {
                Node mNode = nodeList.item(i);
                if (mNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) mNode;

                    dust = getTagValue("pm10Value", element);
                    o3 = getTagValue("o3Value",element);
                }
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
    }

    private String getTagValue(String tag, Element element) {
        NodeList nodeList = element.getElementsByTagName(tag).item(0).getChildNodes();
        Node nNode = (Node) nodeList.item(0);
        if (nNode == null)
            return null;
        return nNode.getNodeValue();
    }
}
