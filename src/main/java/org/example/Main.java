package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
public class Main {
    static InmetArrayAlerts inmetArrayAlerts = new InmetArrayAlerts();
    public static void main(String[] args) {
        String url = "https://apiprevmet3.inmet.gov.br/avisos/rss";
        String outputFilePath = "arquivo.xml";

        try {
            downloadRSS(url, outputFilePath);
            lerXML(outputFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(inmetArrayAlerts.hasNewItem){
            InmetAlert alerta = inmetArrayAlerts.getLastAlert();

            System.out.println(alerta.getId());
            System.out.println(alerta.getCategory());
            System.out.println(alerta.getDescription());

            for (int x=0; x<alerta.getIbgeMunicipios().size(); x++){
                System.out.print("\t"+(String) alerta.getIbgeMunicipios().get(x).toString());
            }
            System.out.println("");

            valendo(alerta.getTimeStampDateOnSet(),alerta.getTimeStampDateExpires());

        }

    }

    public static void downloadRSS(String url, String outputFilePath) throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent();
                     OutputStream outputStream = new FileOutputStream(outputFilePath)) {
                    byte[] buffer = new byte[1024];
                    int bytesRead;
                    while ((bytesRead = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, bytesRead);
                    }
                }
            }
        }
    }

    public static void lerXML(String xmlFilePath) {
        //String xmlFilePath = "arquivo.xml";
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(xmlFilePath));

            // Obtendo a lista de elementos <item>
            NodeList itemList = doc.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Element item = (Element) itemList.item(i);

                // Obtem o link do alerta e captura o ID
                String link = item.getElementsByTagName("link").item(0).getTextContent();
                String[] linkSeg = link.split("/");
                Integer alertID = Integer.parseInt(linkSeg[linkSeg.length - 1]);

                // Se Alerta não existir, realiza o cadastro
                if(!inmetArrayAlerts.alertExists(alertID)){
                    InmetAlert alert = new InmetAlert(alertID,
                            item.getElementsByTagName("title").item(0).getTextContent(),
                            link);

                    //Baixando informações do Alerta
                    File file = new File(alertID.toString()+".xml");
                    if (file.exists()) {
                        System.out.println("O arquivo já existe. Não será feito o download novamente.");
                    } else {
                        downloadRSS(link,alertID.toString()+".xml");
                    }
                    DocumentBuilderFactory alertFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder alertBuilder = alertFactory.newDocumentBuilder();
                    Document alertDoc = alertBuilder.parse(new File(alertID.toString()+".xml"));
                    Element info = (Element) alertDoc.getElementsByTagName("info").item(0);

                    alert.setCategory(info.getElementsByTagName("category").item(0).getTextContent());
                    alert.setEvent(info.getElementsByTagName("event").item(0).getTextContent());
                    alert.setResponseType(info.getElementsByTagName("responseType").item(0).getTextContent());
                    alert.setUrgency(info.getElementsByTagName("urgency").item(0).getTextContent());
                    alert.setSeverity(info.getElementsByTagName("severity").item(0).getTextContent());
                    alert.setCertainty(info.getElementsByTagName("certainty").item(0).getTextContent());
                    alert.setSenderName(info.getElementsByTagName("senderName").item(0).getTextContent());
                    alert.setDescription(info.getElementsByTagName("description").item(0).getTextContent());
                    alert.setInstruction(info.getElementsByTagName("instruction").item(0).getTextContent());
                    alert.setWeb(info.getElementsByTagName("web").item(0).getTextContent());

                    NodeList alertParameters = alertDoc.getElementsByTagName("parameter");
                    for (int j = 0; j < alertParameters.getLength(); j++) {
                        Element parameter = (Element) alertParameters.item(j);
                        String valueName = parameter.getElementsByTagName("valueName").item(0).getTextContent();
                        String value = parameter.getElementsByTagName("value").item(0).getTextContent();
                        if (valueName.equals("ColorRisk")){
                            alert.setColorRisk(value);
                        }else if(valueName.equals("TimeStampDateOnSet")){
                            alert.setTimeStampDateOnSet(Long.parseLong(value));
                        }else if(valueName.equals("TimeStampDateExpires")){
                            alert.setTimeStampDateExpires(Long.parseLong(value));
                        }else if(parameter.getElementsByTagName("valueName").item(0).getTextContent().equals("Municipios")){
                            ArrayList<IBGEMunicipio> ibgeMunicipios = new ArrayList<>();
                            String municipios = parameter.getElementsByTagName("value").item(0).getTextContent();
                            // Define uma expressão regular para encontrar os códigos dentro dos parênteses
                            Pattern pattern = Pattern.compile("\\((\\d+)\\)");
                            // Crie um objeto Matcher para executar a expressão regular na string de municípios
                            Matcher matcher = pattern.matcher(municipios);
                            // Use o loop para encontrar todos os códigos e imprimir
                            //System.out.println("Códigos dos municípios:");
                            while (matcher.find()) {
                                Integer codigo = Integer.parseInt(matcher.group(1));
                                IBGEMunicipio codIBGE = new IBGEMunicipio(codigo);
                                ibgeMunicipios.add(codIBGE);
                                System.out.println("MUN "+codigo);
                            }
                            alert.setIbgeMunicipios(ibgeMunicipios);
                        }
                    }
                    inmetArrayAlerts.addItem(alert);
                }else{
                    System.out.print(".");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Boolean valendo(Long TimeStampDateOnSet, Long timeStampDateExpires) {
        long currentTimestamp = Instant.now().getEpochSecond();
        if(timeStampDateExpires>currentTimestamp){
            if(TimeStampDateOnSet<currentTimestamp){
                return true;
            }
        }
        return false;
    }
}