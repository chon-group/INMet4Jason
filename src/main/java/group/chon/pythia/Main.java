package group.chon.pythia;

import group.chon.pythia.ibgeGovBR.IBGEdtb;
import group.chon.pythia.inmetGovBR.InmetAlert;
import group.chon.pythia.inmetGovBR.InmetRSS;

/**
 * Pythia Middleware API
 *
 * @author Nilson Lazarin
 */
public class Main {
    /**
     * @param args
     * &emsp; [CityIBGE] <br>
     * &emsp; Shows the Weather Alert for a specific City <br>
     * &emsp; [CityIBGE] is mandatory. This argument is provided by <a href="https://www.ibge.gov.br/explica/codigos-dos-municipios.php" target="_blank">IBGE - Instituto Brasileiro de Geografia e Estatística</a> <br>
     * <br>
     * &emsp;[CityIBGE] [URL] <br>
     * &emsp; Shows the Weather Alert for a specific City <br>
     * &emsp; [CityIBGE] is mandatory. This argument is provided by <a href="https://www.ibge.gov.br/explica/codigos-dos-municipios.php" target="_blank">IBGE - Instituto Brasileiro de Geografia e Estatística</a> <br>
     * &emsp; [URL] is mandatory. This argument inform the <a href="https://alertas2.inmet.gov.br/" target="_blank">INMET::Alert-AS Service URL</a> <br>
     *
     */
    public static void main(String[] args) {
        InmetRSS inmetRSS;
        InmetAlert inmetAlert;
        IBGEdtb ibgEdtb = new IBGEdtb();

        try{
            if(args.length==2){
                Integer city = ibgEdtb.getIBGECod(args[1],args[0]);
                System.out.println("IBGECitycod: "+city);
                inmetRSS = new InmetRSS();
                //inmetRSS.setDEFAULT_INMET_RSS("http://apiprevmet3.inmet.gov.br/avisos/rss"); /* URL Service */
                //inmetRSS.setDEFAULT_ALERT_TTL(28800000L);               /* 8 hours      */
                //inmetRSS.setDEFAULT_TIME_BETWEEN_REQUESTS(360000L);     /* 10 minutes   */
                inmetRSS.getDataFromRSS();

                while(inmetRSS.hasAlert()){
                    inmetAlert = inmetRSS.getLastUnperceivedAlert(city);
                    if(inmetAlert != null){
                        System.out.print(inmetAlert.getId()+" ");
                        System.out.println(inmetAlert.getDescription());
                    }
                }
            }else{
                throw new Exception();
            }
        }catch (Exception ex){
            System.out.println("Try again with: \n\t java -jar Pythia.jar [CITY] [UF] \n EXAMPLE: \n\t java -jar Pythia.jar \"Nova Friburgo\" RJ\n");
            System.exit(0);
        }
    }

}
