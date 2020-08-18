package com.fermimn.notificationmaker;

import android.content.Context;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class  Cronologia {
    private final String CRONOLOGIA = "cronologia.xml";

    private static Cronologia instance;
    private Context context;
    private TreeMap<Integer,Notifica> cronologia;

    public Cronologia(Context context){
        this.context = context;
        load();
    }

    public static Cronologia getInstance(Context context){
        if(instance==null){
            instance = new Cronologia(context);
        }
        return instance;
    }

    public TreeMap<Integer,Notifica> getCronologia(){
        return cronologia;
    }

    public int aggiungiNotifica(int notificationId, Notifica notifica) {

        int result = -1;     //-1 = ok, altro = id notifica preesistente cancellata

        if(cronologia.containsValue(notifica)){
            Set<Integer> keys = cronologia.keySet();
            for(Integer i : keys){
                if(cronologia.get(i).equals(notifica)){
                    cronologia.remove(i);
                    result = i;
                    break;
                }
            }
        }

        cronologia.put(notificationId, notifica);
        commit();

        return result;
    }

    public void commit(){
        File f = new File(context.getFilesDir(),CRONOLOGIA);

        if(f.exists()){
            f.delete();
        }

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();

            Document doc = parser.newDocument();

            Element notifiche = doc.createElement("notifiche");

            Set<Integer> ids = cronologia.keySet();
            Iterator<Integer> iterator = ids.iterator();


            while (iterator.hasNext()){
                int i = iterator.next();
                Element notifica = doc.createElement("notifica");
                notifica.setAttribute("id",""+i);

                Element titolo = doc.createElement("titolo");
                titolo.setTextContent(cronologia.get(i).getTitolo());

                Element descrizione = doc.createElement("descrizione");
                descrizione.setTextContent(cronologia.get(i).getDescrizione());

                Element data = doc.createElement("data");
                data.setTextContent(cronologia.get(i).getData());

                Element permanente = doc.createElement("permanente");
                permanente.setTextContent(String.valueOf(cronologia.get(i).isPermanente()));

                Element visibile = doc.createElement("visibile");
                visibile.setTextContent(String.valueOf(cronologia.get(i).isVisibile()));

                notifica.appendChild(titolo);
                notifica.appendChild(descrizione);
                notifica.appendChild(data);
                notifica.appendChild(permanente);
                notifica.appendChild(visibile);

                notifiche.appendChild(notifica);
            }

            doc.appendChild(notifiche);

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult streamResult = new StreamResult(new File(context.getFilesDir(),CRONOLOGIA));
            transformer.transform(domSource, streamResult);

        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }

    }

    public void load(){

        cronologia = new TreeMap();

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder parser = factory.newDocumentBuilder();
            File f = new File(context.getFilesDir(),CRONOLOGIA);

            if(!f.exists()){
                f.createNewFile();
            }

            Document doc = parser.parse(f);

            NodeList list = doc.getElementsByTagName("notifica");
            for(int i=0; i<list.getLength();i++){
                Element e = (Element)list.item(i);
                int id = Integer.parseInt(((Element)e).getAttribute("id"));
                String titolo = e.getElementsByTagName("titolo").item(0).getTextContent();
                String descrizione = e.getElementsByTagName("descrizione").item(0).getTextContent();
                String data = e.getElementsByTagName("data").item(0).getTextContent();
                boolean permanente = Boolean.parseBoolean(e.getElementsByTagName("permanente").item(0).getTextContent());
                boolean visibile = Boolean.parseBoolean(e.getElementsByTagName("visibile").item(0).getTextContent());
                cronologia.put(id,new Notifica(titolo,descrizione,data,permanente,visibile,id));
            }
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Notifica getNotificaById(int id){
        return cronologia.get(id);
    }

    public void setNotificaCancellata(int id){
        Notifica nuova = getNotificaById(id);
        nuova.setVisibile(false);
        cronologia.put(id,nuova);
        commit();
    }

    public ArrayList<Notifica> getNotificheVisibili(){
        ArrayList<Notifica> notificheVisibili = new ArrayList();

        Iterator<Integer> iterator = cronologia.keySet().iterator();

        while (iterator.hasNext()){
            int next = iterator.next();
            Notifica current = cronologia.get(next);
            if(current.isVisibile()){
                notificheVisibili.add(current);
            }
        }

        return notificheVisibili;
    }



    public void rimuoviNotifica(Integer id){
        cronologia.remove(id);
    }

    //NO LONGER USED
//    public void setNotificheTutteCancellate(){
//        ArrayList<Notifica> visibili = getNotificheVisibili();
//        for(int i=0;i<visibili.size();i++){
//            setNotificaCancellata(visibili.get(i).getId());
//        }
//    }
}
