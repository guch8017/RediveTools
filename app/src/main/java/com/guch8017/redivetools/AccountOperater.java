package com.guch8017.redivetools;

import android.content.Context;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import static com.guch8017.redivetools.Utils.*;

public class AccountOperater {
    private static final String xmlPath = "/data/data/tw.sonet.princessconnect/shared_prefs/tw.sonet.princessconnect.v2.playerprefs.xml";
    private static final String savedDataPath = "/data/data/tw.sonet.princessconnect/files/savedData/";

    /**
     * 还原账户数据到游戏
     * @param context context上下文
     * @param data 目标写入账户数据
     * @throws Exception 解析失败
     */
    public static void accountRestorer(Context context, DBAccountData data) throws Exception{
        InputStream is = Utils.getFile(xmlPath);
        if(is == null){
            throw new Exception(context.getString(R.string.err_file_open) + xmlPath);
        }

        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document document = documentBuilder.parse(is);
        XPathFactory factory = XPathFactory.newInstance();
        XPath xm3 = factory.newXPath();
        XPath xmh = factory.newXPath();
        XPath xnn = factory.newXPath();

        Element rootMap = document.getDocumentElement();

        if(rootMap == null){
            Log.e("XML Writer", "Can't get root element");
            throw new Exception(context.getString(R.string.err_xml_root));
        }
        /*
        else{
            Log.d("XML Writer", "Root Tag Name: " + rootMap.getTagName());
        }
        */

        Node nm3 = (Node) xm3.evaluate("map/string[@name=\"M3F1YSNkOnF0\"]", document, XPathConstants.NODE);
        Node nmh = (Node) xmh.evaluate("map/string[@name=\"MHx5cg%3D%3D\"]", document, XPathConstants.NODE);
        Node nnn = (Node) xnn.evaluate("map/string[@name=\"NnB%2FZDJpMHx5cg%3D%3D\"]", document, XPathConstants.NODE);

        if(nm3 != null){
            rootMap.removeChild(nm3);
        }
        if(nmh != null){
            rootMap.removeChild(nmh);
        }
        if(nnn != null){
            rootMap.removeChild(nnn);
        }

        nm3 = document.createElement("string");
        ((Element)nm3).setAttribute("name","M3F1YSNkOnF0");

        nmh = document.createElement("string");
        ((Element)nmh).setAttribute("name","MHx5cg%3D%3D");

        nnn = document.createElement("string");
        ((Element)nnn).setAttribute("name","NnB%2FZDJpMHx5cg%3D%3D");

        nm3.setTextContent(data.M3);
        nmh.setTextContent(data.MH);
        nnn.setTextContent(data.Nn);

        nmh = rootMap.appendChild(nmh);
        nnn = rootMap.appendChild(nnn);
        nm3 = rootMap.appendChild(nm3);

        if(nm3 == null || nmh == null || nnn == null){
            throw new Exception(context.getString(R.string.err_xml_add_attribute));
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("encoding", "utf-8");
        transformer.setOutputProperty("indent", "yes");
        DOMSource domSource = new DOMSource();
        domSource.setNode(rootMap);
        StreamResult result = new StreamResult();
        OutputStream stream = new ByteArrayOutputStream();
        result.setOutputStream(stream);
        transformer.transform(domSource,result);

        String res = stream.toString().replaceAll("\"","\\\\\"");
        //Log.d("XML Writer","XML to be write:\n" + res);
        //Log.d("XML Writer", "End of xml");

        InputStream os1 = putFile(xmlPath, res);
        if(os1 == null){
            throw new Exception(context.getString(R.string.err_file_write) + xmlPath);
        }

        final String binPath = savedDataPath + data.server + "/playersave.bin";
        deleteFile(binPath);
    }

    /**
     * 运行环境初始化
     * @param context context上下文
     */
    public static void accountInitial(Context context) throws Exception{
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = dbf.newDocumentBuilder();
        Document document = documentBuilder.parse(Utils.getFile(xmlPath));
        XPathFactory factory = XPathFactory.newInstance();
        XPath xm3 = factory.newXPath();
        XPath xmh = factory.newXPath();
        XPath xnn = factory.newXPath();

        Element rootMap = document.getDocumentElement();
        if(rootMap == null){
            Log.e("XML Writer", "Can't get root element");
            throw new Exception(context.getString(R.string.err_xml_root));
        }
        /*
        else{
            Log.d("XML Writer", "Root Tag Name: " + rootMap.getTagName());
        }
        */
        Node nm3 = (Node) xm3.evaluate("map/string[@name=\"M3F1YSNkOnF0\"]", document, XPathConstants.NODE);
        Node nmh = (Node) xmh.evaluate("map/string[@name=\"MHx5cg%3D%3D\"]", document, XPathConstants.NODE);
        Node nnn = (Node) xnn.evaluate("map/string[@name=\"NnB%2FZDJpMHx5cg%3D%3D\"]", document, XPathConstants.NODE);


        if(nm3 != null){
            rootMap.removeChild(nm3);
        }
        if(nmh != null){
            rootMap.removeChild(nmh);
        }
        if(nnn != null){
            rootMap.removeChild(nnn);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty("encoding", "utf-8");
        transformer.setOutputProperty("indent", "yes");
        DOMSource domSource = new DOMSource();
        domSource.setNode(rootMap);
        StreamResult result = new StreamResult();
        OutputStream stream = new ByteArrayOutputStream();
        result.setOutputStream(stream);
        transformer.transform(domSource,result);

        String res = stream.toString().replaceAll("\"","\\\\\"");
        //Log.d("XML Writer","XML to be write:\n" + res);
        //Log.d("XML Writer", "End of xml");

        InputStream os = putFile(xmlPath, res);
        if(os == null){
            throw new Exception(context.getString(R.string.err_file_write));
        }
        for(int i = 1; i <= 3; ++i){
            deleteFile(savedDataPath + i);
        }

    }

    /**
     * 账号读取
     * @param context context上下文
     * @param server 服务器编号(1~3)
     * @return XML中账号数据
     * @throws Exception XML解析失败/文件IO错误
     */
    public static DBAccountData accountReader(Context context, int server) throws Exception{
        InputStream is = Utils.getFile(xmlPath);
        if(is == null){
            throw new Exception(context.getString(R.string.err_file_open));
        }
        DBAccountData data = new DBAccountData();
        ArrayList<String> result = new ArrayList<>();
        final String[] key = new String[]{"M3F1YSNkOnF0","MHx5cg%3D%3D","NnB%2FZDJpMHx5cg%3D%3D"};
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        Document document = db.parse(is);
        XPathFactory xf = XPathFactory.newInstance();
        for(int i = 0; i < key.length; ++i){
            XPath xPath = xf.newXPath();
            XPathExpression expression = xPath.compile("map/string[@name=\"" + key[i] + "\"]");
            Node node = (Node) expression.evaluate(document, XPathConstants.NODE);
            if(node != null){
                result.add(node.getTextContent());
                //Log.i("XML Parser", key[i] + ": " + node.getTextContent());
            }
            else{
                //Log.i("XML Parser", "Error: Can't find node" + key[i]);
                throw new Exception(context.getString(R.string.err_xml_node) + key[i]);
            }
        }
        data.M3 = result.get(0);
        data.MH = result.get(1);
        data.Nn = result.get(2);
        data.server = server;
        return data;
    }
}
