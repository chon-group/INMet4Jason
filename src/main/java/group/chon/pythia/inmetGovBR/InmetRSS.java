package group.chon.pythia.inmetGovBR;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.HttpEntity;
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
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.time.Instant;

/**
 * Client of INMET::Alert-AS Service <br>
 * <br>
 * © <a href="https://alertas2.inmet.gov.br/" target="_blank">Alert-AS - Centro Virtual para Avisos de Eventos Meteorol&oacute;gicos Severos</a><br>
 * © <a href="https://portal.inmet.gov.br/" target="_blank">INMET - Instituto Nacional de Meteorologia</a><br>
 * © <a href="https://www.gov.br/agricultura/pt-br" target="_blank">Ministério da Agricultura e Pecuária</a>
 *
 * @author Nilson Lazarin
 *
 */
public class InmetRSS {
    private Long DEFAULT_TIME_BETWEEN_REQUESTS = 3600000L;
    private String  DEFAULT_CACHE_DIR = ".inmetGovBR";
    private String  DEFAULT_XML_ROOT_FILE = "inmetRSS.xml";
    private InmetAlertsArray inmetAlertsArray = new InmetAlertsArray();
    private String URL;
    Logger logger = Logger.getLogger(InmetRSS.class.getName());


    /**
     *  This class get weather alerts of INMET::AlertAS Service
     *
     * @param URL This parameter receives the URL of the INMET::AlertAS Service
     */
    public InmetRSS(String URL){
        this.URL = URL;
        this.loadRSS();
    }

    /**
     * This class get weather alerts of INMET::AlertAS Service
     *
     */
    public InmetRSS(){

    }

    public void setMinutesBetweenRequests(Integer MINUTES) {
        this.DEFAULT_TIME_BETWEEN_REQUESTS = Integer.toUnsignedLong(MINUTES*60*1000);
    }

    public void setCACHE_DIR(String DEFAULT_CACHE_DIR) {
        this.DEFAULT_CACHE_DIR = DEFAULT_CACHE_DIR;
    }

    public void setDEFAULT_XML_ROOT_FILE(String DEFAULT_XML_ROOT_FILE){
        this.DEFAULT_XML_ROOT_FILE = DEFAULT_XML_ROOT_FILE;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public void loadRSS(){
        try {
            downloadRSS(URL, DEFAULT_XML_ROOT_FILE);
            readXML(DEFAULT_XML_ROOT_FILE);
        } catch (IOException e) {
            logger.severe(e.getMessage());
        }
    }

    /**
     * Get if exists a new alert
     *
     * @return TRUE if exists a new alert
     */
    public Boolean getHasNewItem(){
        return inmetAlertsArray.getHasNewItem();
    }

    /**
     * Get the last unperceived alert issued
     *
     * @return A new alert issued
     */
    public InmetAlert getLastUnperceivedAlert(){
        return inmetAlertsArray.getLastUnperceivedAlert();
    }

    /**
     * Get the last unperceived alert issued to a specific City
     *
     * @param IBGEId - This parameter receives the City's IBGE identification
     *
     * @return A new alert issued to a specific City
     *
     */
    public InmetAlert getLastUnperceivedAlert(Integer IBGEId){
        if(IBGEId==0){
            return getLastUnperceivedAlert();
        }else{
            InmetAlert inmetAlert = inmetAlertsArray.getLastUnperceivedAlert();
            ArrayList<IBGECityID> ibgeMunicipios = inmetAlert.getIbgeMunicipios();
            for(int j=0; j<ibgeMunicipios.size(); j++){
                if(ibgeMunicipios.get(j).getIBGE_Id().equals(IBGEId)){
                    return inmetAlert;
                }
            }
            return null;
        }
    }

    private void downloadRSS(String url, String outputFilePath) throws IOException {
        Path diretorioPath = Paths.get(DEFAULT_CACHE_DIR);
        if (!(Files.exists(diretorioPath) && Files.isDirectory(diretorioPath))) {
            Files.createDirectory(diretorioPath);
        }

        if(isFileMoreOldOrNotExists(DEFAULT_CACHE_DIR+FileSystems.getDefault().getSeparator()+outputFilePath)) {
            logger.info("Downloading... "+outputFilePath);
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(url);

            try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    try (InputStream inputStream = entity.getContent();
                         OutputStream outputStream = new FileOutputStream(DEFAULT_CACHE_DIR +FileSystems.getDefault().getSeparator()+outputFilePath)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                    }
                }
            } catch (Exception ex) {
                logger.severe(ex.getMessage());
            }
        }else{
            logger.info("Loading cached "+outputFilePath);
        }
    }

    private void readXML(String xmlFilePath) {
        xmlFilePath = DEFAULT_CACHE_DIR +FileSystems.getDefault().getSeparator()+xmlFilePath;
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
                if(!inmetAlertsArray.alertExists(alertID)){
                    InmetAlert alert = new InmetAlert(alertID,
                            item.getElementsByTagName("title").item(0).getTextContent(),
                            link);

                    String alertPathFile = DEFAULT_CACHE_DIR +FileSystems.getDefault().getSeparator()+alertID+".xml";

                    //Baixando informações do Alerta
                    File file = new File(alertPathFile);
                    if (!file.exists()) {
                        downloadRSS(link,alertID+".xml");
                    }
                    DocumentBuilderFactory alertFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder alertBuilder = alertFactory.newDocumentBuilder();
                    Document alertDoc = alertBuilder.parse(new File(alertPathFile));
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
                            ArrayList<IBGECityID> ibgeMunicipios = new ArrayList<>();

                            String municipios = parameter.getElementsByTagName("value").item(0).getTextContent();
                            Pattern pattern = Pattern.compile("\\((\\d+)\\)");
                            Matcher matcher = pattern.matcher(municipios);

                            while (matcher.find()) {
                                Integer codigo = Integer.parseInt(matcher.group(1));
                                IBGECityID codIBGE = new IBGECityID(codigo);
                                ibgeMunicipios.add(codIBGE);
                            }
                            alert.setIbgeMunicipios(ibgeMunicipios);
                        }
                    }
                    inmetAlertsArray.addItem(alert);
                }
/*                else{
                    System.out.print(".");
                }

 */
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }

    /**
     * Checks the temporality of an issued alert and returns true if the alert is for the current time.
     *
     * @param timeStampDateOnSet Receives the initial timestamp of temporality period.
     * @param timeStampDateExpires Receives the final timestamp of temporality period.
     * @return <b>TRUE</b> if the alert is for the current time.
     */
    public Boolean isRightNow(Long timeStampDateOnSet, Long timeStampDateExpires) {
        long currentTimestamp = Instant.now().getEpochSecond();
        if(timeStampDateExpires>currentTimestamp){
            if(timeStampDateOnSet<currentTimestamp){
                return true;
            }
        }
        return false;
    }

    /**
     * Checks the temporality of an issued alert and returns true if the alert is for the current time.
     *
     * @param timeStampDateOnSet Receives the initial timestamp of temporality period.
     * @param timeStampDateExpires Receives the final timestamp of temporality period.
     * @return <b>TRUE</b> if the alert is for the future.
     */
    public Boolean isFuture(Long timeStampDateOnSet, Long timeStampDateExpires) {
        long currentTimestamp = Instant.now().getEpochSecond();
        if(timeStampDateExpires>currentTimestamp){
            if(timeStampDateOnSet>currentTimestamp){
                return true;
            }
        }
        return false;
    }

    private Boolean isFileMoreOldOrNotExists(String filePath) {
        File file = new File(filePath);
        return !file.exists() ||
                System.currentTimeMillis() - file.lastModified() >= this.DEFAULT_TIME_BETWEEN_REQUESTS;
    }

    /**
     * Clean the cache of alerts previously received.
     *
     * @param strOpt Receive the cache alert directory path.
     */
    public void cleanCache(String strOpt){
        File diretorio = new File(strOpt);
        delDirRecursively(diretorio);
    }

    private void delDirRecursively(File dir) {
        if (dir.isDirectory()){
            File[] content = dir.listFiles();
            if (content != null) {
                for (File file : content) {
                    delDirRecursively(file);
                }
            }
        }
        dir.delete();
    }

    private Boolean placeMatch(Integer intPlace, ArrayList<IBGECityID> cityList){
        for(int j=0; j<cityList.size(); j++){
            if(cityList.get(j).getIBGE_Id().equals(intPlace)){
                return true;
            }
        }
        return false;
    }

}

