package group.chon.pythia.inmetGovBR;

import java.util.ArrayList;

/**
 * An Object that stores a Weather Alert issued by INMET::AlertAS Service.
 *
 * @author Nilson Lazarin
 */
public class InmetAlert {
    private Boolean perceived;
    private Integer id;
    private String title;
    private String link;
    private String category;
    private String event;
    private String responseType;
    private String urgency;
    private String severity;
    private String certainty;
    private String senderName;
    private String description;
    private String instruction;
    private String web;
    private String colorRisk;
    private Long timeStampDateOnSet;
    private Long timeStampDateExpires;
    private ArrayList<InmetAlertIBGECityID> ibgeMunicipios = new ArrayList<>();

    /**
     * An alert of INMET::AlertAS service
     *
     * @param id the alert number
     * @param title the alert title
     * @param link the alert URL
     */
    public InmetAlert(Integer id, String title, String link) {
        this.perceived = false;
        this.id = id;
        this.title = title;
        this.link = link;
    }

    /**
     * Get the perception status of the alert
     *
     * @return TRUE if the alert was already perceived
     */
    public Boolean getPerceived() {
        return perceived;
    }

    /**
     * Set the perception status of the alert
     *
     * @param perceived a boolean value
     */
    public void setPerceived(Boolean perceived) {
        this.perceived = perceived;
    }

    /**
     * Get the alert number
     *
     * @return the alert number
     */
    public Integer getId() {
        return id;
    }

    /**
     * Get the alert title
     *
     * @return the alert title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the alert URL
     *
     * @return the alert URL
     */
    public String getLink() {
        return link;
    }


    /**
     * Get the Category from an alert
     *
     * @return the Category from the alert
     */
    public String getCategory() {
        return category;
    }

    /**
     * Stores the Category parameter
     *
     * @param category the value of [category] parameter from RSS alert file
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Get the HeadLine from an alert
     *
     * @return the HeadLine from the alert
     */
    public String getEvent() {
        return event;
    }

    /**
     * Stores the HeadLine parameter
     *
     * @param event the value of [headline] parameter from RSS alert file
     */
    public void setEvent(String event) {
        this.event = event;
    }

    /**
     * Get the ResponseType from an alert
     *
     * @return the ResponseType from the alert
     */
    public String getResponseType() {
        return responseType;
    }

    /**
     * Stores the ResponseType parameter
     *
     * @param responseType the value of [responseType] parameter from RSS alert file
     */
    public void setResponseType(String responseType) {
        this.responseType = responseType;
    }

    /**
     * Get the Urgency from an alert
     *
     * @return the Urgency from the alert
     */
    public String getUrgency() {
        return urgency;
    }

    /**
     * Stores the Urgency parameter
     *
     * @param urgency the value of [urgency] parameter from RSS alert file
     */
    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    /**
     * Get the Severity from an alert
     *
     * @return the Severity from the alert
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Stores the Severity parameter
     *
     * @param severity the value of [severity] parameter from RSS alert file
     */
    public void setSeverity(String severity) {
        this.severity = severity;
    }

    /**
     * Get the Certainty from an alert
     *
     * @return the Certainty from the alert
     */
    public String getCertainty() {
        return certainty;
    }

    /**
     * Stores the Certainty parameter
     *
     * @param certainty the value of [certainty] parameter from RSS alert file
     */
    public void setCertainty(String certainty) {
        this.certainty = certainty;
    }

    /**
     * Get the SenderName from an alert
     *
     * @return the SenderName from the alert
     */
    public String getSenderName() {
        return senderName;
    }

    /**
     * Stores the SenderName parameter
     *
     * @param senderName the value of [senderName] parameter from RSS alert file
     */
    public void setSenderName(String senderName) {
        this.senderName = this.senderName;
    }

    /**
     * Get the Description from an alert
     *
     * @return the Description from the alert
     */
    public String getDescription() {
        return description;
    }

    /**
     * Stores the Description parameter
     *
     * @param description the value of [description] parameter from RSS alert file
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get the Instruction from an alert
     *
     * @return the Instruction from the alert
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * Stores the Instruction parameter
     *
     * @param instruction the value of [instruction] parameter from RSS alert file
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * Get the alert URL
     *
     * @return the alert URL
     */
    public String getWeb() {
        return web;
    }

    /**
     * Stores the Alert URL
     *
     * @param web the value of [web] parameter from RSS alert file
     *
     */
    public void setWeb(String web) {
        this.web = web;
    }

    /**
     * Get the ColorRisk from an alert
     *
     * @return the ColorRisk from the alert
     */
    public String getColorRisk() {
        return colorRisk;
    }

    /**
     * Stores the ColorRisk parameter
     *
     * @param colorRisk the value of [ColorRisk] parameter from RSS alert file
     */
    public void setColorRisk(String colorRisk) {
        this.colorRisk = colorRisk;
    }

    /**
     * Get the TimeStampDateOnSet from an alert
     *
     * @return the TimeStampDateOnSet from the alert
     */
    public Long getTimeStampDateOnSet() {
        return timeStampDateOnSet;
    }

    /**
     * Stores the TimeStampDateOnSet parameter
     *
     * @param timeStampDateOnSet the value of [TimeStampDateOnSet] parameter from RSS alert file
     */
    public void setTimeStampDateOnSet(Long timeStampDateOnSet) {
        this.timeStampDateOnSet = timeStampDateOnSet;
    }

    /**
     * Get the TimeStampDateExpires from an alert
     *
     * @return the TimeStampDateExpires from the alert
     */
    public Long getTimeStampDateExpires() {
        return timeStampDateExpires;
    }

    /**
     * Stores the TimeStampDateExpires parameter
     *
     * @param timeStampDateExpires the value of [TimeStampDateExpires] parameter from RSS alert file
     */
    public void setTimeStampDateExpires(Long timeStampDateExpires) {
        this.timeStampDateExpires = timeStampDateExpires;
    }

    /**
     * Get the City list from an alert
     *
     * @return the list of [Municipios] from the alert
     */
    public ArrayList<InmetAlertIBGECityID> getIbgeMunicipios() {
        return ibgeMunicipios;
    }

    /**
     * Stores the City list parameter
     *
     * @param ibgeMunicipios the list of [Municipios] from RSS alert file
     *
     */
    public void setIbgeMunicipios(ArrayList<InmetAlertIBGECityID> ibgeMunicipios) {
        this.ibgeMunicipios = ibgeMunicipios;
    }
}
