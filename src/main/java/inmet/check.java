package inmet;

import group.chon.inmet.IBGEMunicipio;
import group.chon.inmet.InmetAlert;
import group.chon.inmet.InmetRSS;
import jason.asSemantics.DefaultInternalAction;
import jason.asSemantics.TransitionSystem;
import jason.asSemantics.Unifier;
import jason.asSyntax.Literal;
import jason.asSyntax.Term;

import java.util.ArrayList;

public class check extends DefaultInternalAction {
    private InmetAlert inmetAlert = null;
    private InmetRSS inmetRSS = null;

    @Override
    public Object execute(TransitionSystem ts, Unifier un, Term[] args) throws Exception {
        inmetRSS = new InmetRSS(args[0].toString().replace("\"",""));
        String strBelief;
        while (inmetRSS.getHasNewItem()) {
            inmetAlert = inmetRSS.getLastUnperceivedAlert();
            if (placeMatch(Integer.parseInt(args[1].toString()))) {
                strBelief = "inmetAlert("+ inmetAlert.getId() +
                        "," + convertString(inmetAlert.getEvent())+
                        "," + convertString(inmetAlert.getSeverity()) +
                        "," + convertString(inmetAlert.getCertainty()) +
                        ","+temporalidade(inmetAlert.getTimeStampDateOnSet(), inmetAlert.getTimeStampDateExpires())+
                        "," + convertString(inmetAlert.getResponseType()) +
                        ",\"" + inmetAlert.getDescription()+"\""+
                        ",\"" + inmetAlert.getInstruction()+"\"" +
                        ",\"" + inmetAlert.getWeb()+"\""+
                        ")";
                ts.getAg().getBB().add(Literal.parseLiteral(strBelief));
            }
        }
        return true;
    }

    private Boolean placeMatch(Integer intPlace){
        ArrayList<IBGEMunicipio> ibgeMunicipios = inmetAlert.getIbgeMunicipios();
        for(int j=0; j<ibgeMunicipios.size(); j++){
            if(ibgeMunicipios.get(j).getIbgeCode().equals(intPlace)){
                return true;
            }
        }
        return false;
    }

    private String temporalidade(Long timeStampDateOnSet, Long timeStampDateExpires){
        if(inmetRSS.isFuture(timeStampDateOnSet,timeStampDateExpires)){
            return "future";
        } else if (inmetRSS.isRightNow(timeStampDateOnSet,timeStampDateExpires)) {
            return "rightNow";
        }else{
            return "past";
        }

    }

    private String convertString(String inputStr){
        return inputStr.substring(0, 1).toLowerCase() + removeEspaco(inputStr.substring(1));
    }

    private String removeEspaco(String inputStr){
        return inputStr.replaceAll("\s", "");
    }
}