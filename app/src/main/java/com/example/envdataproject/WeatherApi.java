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

public class WeatherApi {
    private String temp, humid;

    private static final String URL_STRING = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst";

    public WeatherApi(String baseDate, String baseTime, String nx, String ny) {
        StringBuilder builder = new StringBuilder(URL_STRING);
        try {
            builder.append("?").append(URLEncoder.encode("ServiceKey", "UTF-8")).append("=jrwqtLVZlEX%2B36vApnSxZMm%2FIp%2BIu1dmnbAysBy2XP721fA1SjZF6MC3uqOzP06mhSCHFyEqJhWoIcs%2BuPnAbg%3D%3D");
            builder.append("&").append(URLEncoder.encode("pageNo", "UTF-8")).append("=").append(URLEncoder.encode("1", "UTF-8"));
            builder.append("&").append(URLEncoder.encode("numOfRows", "UTF-8")).append("=").append(URLEncoder.encode("10", "UTF-8"));
            builder.append("&").append(URLEncoder.encode("dataType", "UTF-8")).append("=").append(URLEncoder.encode("XML", "UTF-8"));
            if(baseTime.equals("2300")) {
                builder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=").append(URLEncoder.encode(convertBaseDate(baseDate), "UTF-8"));
            } else {
                builder.append("&").append(URLEncoder.encode("base_date", "UTF-8")).append("=").append(URLEncoder.encode(baseDate, "UTF-8"));
            }
            builder.append("&").append(URLEncoder.encode("base_time", "UTF-8")).append("=").append(URLEncoder.encode(baseTime, "UTF-8")); /*06시 발표(정시단위)*/ //0600
            builder.append("&").append(URLEncoder.encode("nx", "UTF-8")).append("=").append(URLEncoder.encode(nx, "UTF-8")); /*예보지점의 X 좌표값*/
            builder.append("&").append(URLEncoder.encode("ny", "UTF-8")).append("=").append(URLEncoder.encode(ny, "UTF-8")); /*예보지점 Y 좌표*/

            URL url = new URL(builder.toString());
            extractData(url);

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public String getHumid() {
        return humid;
    }

    public String getTemp() {
        return temp;
    }

    private void extractData(URL url) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(url.toString());
            document.getDocumentElement().normalize();

            System.out.println("Root element: " + document.getDocumentElement().getTagName());
            NodeList nodeList = document.getElementsByTagName("item");
            for(int i = 0; i<nodeList.getLength(); i++) {
                Node mNode = nodeList.item(i);
                if (mNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) mNode;
                    if("T3H".equals(getTagValue("category", element))) {
                        temp = getTagValue("fcstValue",element);
                    }
                    if("REH".equals(getTagValue("category", element))) {
                        humid = getTagValue("fcstValue",element);
                    }
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

    private String convertBaseDate(String str) {
        int date = Integer.parseInt(str) - 1;

        return String.valueOf(date);
    }
}

