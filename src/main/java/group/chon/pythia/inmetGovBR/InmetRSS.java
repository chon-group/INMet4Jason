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
    private Long DEFAULT_TIME_BETWEEN_REQUESTS = 3600000L; /* 1 hour */
    private Long DEFAULT_ALERT_TTL = 86400000L;   /* 1 day */
    private String  DEFAULT_INMET_RSS = "https://apiprevmet3.inmet.gov.br/avisos/rss";
    private String  DEFAULT_ROOT_FILE = "inmetRSS.xml";
    private String  DEFAULT_CACHE_DIR = System.getProperty("user.home")+
            FileSystems.getDefault().getSeparator() +
            ".cache"+
            FileSystems.getDefault().getSeparator() +
            "inmetGovBR";
    private InmetAlertsArray inmetAlertsArray = new InmetAlertsArray();
    Logger logger = Logger.getLogger(InmetRSS.class.getName());

    /**
     * This class get weather alerts of INMET::AlertAS Service
     *
     */
    public InmetRSS(){
        //getDataFromRSS();
    }

    /**
     *  This class get weather alerts of INMET::AlertAS Service
     *
     * @param URL This parameter receives the URL of the INMET::AlertAS Service
     */
    public InmetRSS(String URL){
        setDEFAULT_INMET_RSS(URL);
        getDataFromRSS();
    }

    public Long getDEFAULT_TIME_BETWEEN_REQUESTS() {
        return this.DEFAULT_TIME_BETWEEN_REQUESTS;
    }

    public void setDEFAULT_TIME_BETWEEN_REQUESTS(Long DEFAULT_TIME_BETWEEN_REQUESTS) {
        this.DEFAULT_TIME_BETWEEN_REQUESTS = DEFAULT_TIME_BETWEEN_REQUESTS;
    }

    public Long getDEFAULT_ALERT_TTL() {
        return this.DEFAULT_ALERT_TTL;
    }

    public void setDEFAULT_ALERT_TTL(Long DEFAULT_ALERT_TTL) {
        this.DEFAULT_ALERT_TTL = DEFAULT_ALERT_TTL;
    }

    public String getDEFAULT_INMET_RSS() {
        return this.DEFAULT_INMET_RSS;
    }

    public void setDEFAULT_INMET_RSS(String DEFAULT_INMET_RSS) {
        this.DEFAULT_INMET_RSS = DEFAULT_INMET_RSS;
    }

    public String getDEFAULT_CACHE_DIR() {
        return this.DEFAULT_CACHE_DIR;
    }

    public void setDEFAULT_CACHE_DIR(String DEFAULT_CACHE_DIR) {
        this.DEFAULT_CACHE_DIR = DEFAULT_CACHE_DIR;
    }

    public String getDEFAULT_ROOT_FILE() {
        return this.DEFAULT_ROOT_FILE;
    }

    public void setDEFAULT_ROOT_FILE(String DEFAULT_ROOT_FILE) {
        this.DEFAULT_ROOT_FILE = DEFAULT_ROOT_FILE;
    }

    /**
     * Get if exists a new alert
     *
     * @return TRUE if exists a new alert
     */
    public Boolean hasAlert(){
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
            ArrayList<InmetAlertIBGECityID> ibgeMunicipios = inmetAlert.getIbgeMunicipios();
            for (InmetAlertIBGECityID ibgeMunicipio : ibgeMunicipios) {
                if (ibgeMunicipio.getIBGE_Id().equals(IBGEId)) {
                    return inmetAlert;
                }
            }
            return null;
        }
    }

    public void getDataFromRSS() {
        createCACHE_DIRifNotExists();
        downloadRSS(this.DEFAULT_INMET_RSS);
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(new File(DEFAULT_CACHE_DIR + FileSystems.getDefault().getSeparator() + DEFAULT_ROOT_FILE));

            // Obtendo a lista de elementos
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
                    downloadAlert(link,alertID);
                    DocumentBuilderFactory alertFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder alertBuilder = alertFactory.newDocumentBuilder();
                    Document alertDoc = alertBuilder.parse(new File(DEFAULT_CACHE_DIR +FileSystems.getDefault().getSeparator()+alertID+".xml"));
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
                            ArrayList<InmetAlertIBGECityID> ibgeMunicipios = new ArrayList<>();

                            String municipios = parameter.getElementsByTagName("value").item(0).getTextContent();
                            Pattern pattern = Pattern.compile("\\((\\d+)\\)");
                            Matcher matcher = pattern.matcher(municipios);

                            while (matcher.find()) {
                                Integer codigo = Integer.parseInt(matcher.group(1));
                                InmetAlertIBGECityID codIBGE = new InmetAlertIBGECityID(codigo);
                                ibgeMunicipios.add(codIBGE);
                            }
                            alert.setIbgeMunicipios(ibgeMunicipios);
                        }
                    }
                    inmetAlertsArray.addItem(alert);
                }
            }
        } catch (Exception e) {
            logger.severe(e.getMessage());
        }
    }


    public void clearRSSFile(){
        File absoluteFilePath = new File(DEFAULT_CACHE_DIR+FileSystems.getDefault().getSeparator()+this.DEFAULT_ROOT_FILE);
        delCACHE_DIRrecursively(absoluteFilePath);
    }

    public void clearCACHE_DIR(){
        File dirPath = new File(DEFAULT_CACHE_DIR);
        delCACHE_DIRrecursively(dirPath);
    }

    private void downloadAlert(String URL, Integer alertNumber){
        Path alertFile = Paths.get(DEFAULT_CACHE_DIR + FileSystems.getDefault().getSeparator() + alertNumber + ".xml");
        if(!alertFile.toFile().exists() ||
                System.currentTimeMillis() - alertFile.toFile().lastModified() >= this.DEFAULT_ALERT_TTL){
            download(URL, String.valueOf(alertNumber)+".xml");
        }else{
            logger.fine("Loading cached alert "+String.valueOf(alertNumber));
        }
    }

    private void downloadRSS(String URL){
        Path rssRootFile = Paths.get(DEFAULT_CACHE_DIR + FileSystems.getDefault().getSeparator() + DEFAULT_ROOT_FILE);
        if(!rssRootFile.toFile().exists() ||
                System.currentTimeMillis() - rssRootFile.toFile().lastModified() >= this.DEFAULT_TIME_BETWEEN_REQUESTS){
            download(URL,DEFAULT_ROOT_FILE);
        }else{
            logger.info("Loading cached INMET::Alert-AS data");
        }
    }

    private void download(String url, String outputFile) {
        logger.info("Downloading... "+outputFile);
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet httpGet = new HttpGet(url);
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            HttpEntity entity = response.getEntity();
            if (entity != null) {
                try (InputStream inputStream = entity.getContent();
                     OutputStream outputStream = new FileOutputStream(DEFAULT_CACHE_DIR +
                             FileSystems.getDefault().getSeparator()
                             +outputFile)) {
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
    }

    private void createCACHE_DIRifNotExists(){
        Path diretorioPath = Paths.get(DEFAULT_CACHE_DIR);
        if (!(Files.exists(diretorioPath) && Files.isDirectory(diretorioPath))) {
            try {
                Files.createDirectory(diretorioPath);
            } catch (IOException e) {
                logger.severe(e.getMessage());
            }
        }
    }

    private void delCACHE_DIRrecursively(File dirORfilePath) {
        if (dirORfilePath.isDirectory()){
            File[] content = dirORfilePath.listFiles();
            if (content != null) {
                for (File fileInside : content) {
                    delCACHE_DIRrecursively(fileInside);
                }
            }
        }
        dirORfilePath.delete();
    }

}

