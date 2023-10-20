package group.chon.pythia;

import group.chon.pythia.inmetGovBR.IBGECityID;
import group.chon.pythia.inmetGovBR.InmetAlert;
import group.chon.pythia.inmetGovBR.InmetRSS;

import java.util.ArrayList;

/**
 * Pythia Middleware API
 *
 * @author Nilson Lazarin
 */
public class Main {
    private static final String DEFAULT_INMET_RSS = "https://apiprevmet3.inmet.gov.br/avisos/rss";

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
        InmetRSS inmetRSS = null;
        InmetAlert inmetAlert;
        Integer city = null;

        if(args.length==0){
            city = 0;
            inmetRSS = new InmetRSS(DEFAULT_INMET_RSS);
        }else if(args.length==1){
            city = Integer.parseInt(args[0]);
            inmetRSS = new InmetRSS(DEFAULT_INMET_RSS);
        }else if(args.length==2){
            city = Integer.parseInt(args[0]);
            inmetRSS = new InmetRSS(args[1]);
        }

        if(inmetRSS!=null){
            while(inmetRSS.getHasNewItem()){
                inmetAlert = inmetRSS.getLastUnperceivedAlert(city);
                if(inmetAlert != null){
                    System.out.print(inmetAlert.getId()+" ");
                    System.out.println(inmetAlert.getDescription());
                }
            }
        }
    }

}
