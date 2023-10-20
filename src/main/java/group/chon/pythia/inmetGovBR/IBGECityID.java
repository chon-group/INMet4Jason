package group.chon.pythia.inmetGovBR;

/**
 * An Object that stores the IBGE identification of a City described in a weather alert.
 *
 * @author Nilson Lazarin
 */
public class IBGECityID {
    private Integer IBGE_Id;

    /**
     * @param IBGEId An Integer of the City's IBGE identification.
     */
    public IBGECityID(Integer IBGEId){
        this.IBGE_Id = IBGEId;
    }

    /**
     * @return A String of the City's IBGE identification
     */
    public String toString(){
        return getIBGE_Id().toString();
    }

    /**
     * @return the city IBGE identification
     */
    public Integer getIBGE_Id() {
        return IBGE_Id;
    }
}
