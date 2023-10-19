// Agent inmetAgent in project inmetAlert

/* Initial beliefs and rules */
inmetAlertAS("https://apiprevmet3.inmet.gov.br/avisos/rss").// Provided by Alert-AS - Centro Virtual para Avisos de Eventos Meteorológicos Severos --> https://alertas2.inmet.gov.br/
myCity(3303302).                                            // Niteroi-RJ -- Provided by IBGE - Instituto Brasileiro de Geografia e Estatística --> https://www.ibge.gov.br/explica/codigos-dos-municipios.php

/* Initial goals */
!conf.

/* Plans */
+!conf
: inmetAlertAS(URL) 
& myCity(COD) <- 
    pythia.inmet.clear;
    pythia.inmet.check(URL,COD).

+inmetAlert(AlertID,Event,Serverity,Certainty,Time,ResponseType,Description,Instruction,Link)[source(inmetGovBR)] <-
    .print("New Alert: ",AlertID," description: ",Description," event : ",Event, "serverity: ",Serverity, "certainty: ",Certainty, "when: ",Time, "type: ",ResponseType," what to do: ",Instruction, "more info: ",Link).


/*  inmetAlert(44166,
    acumuladodeChuva,
    moderate,
    likely,
    past,
    prepare,
    "INMET publica aviso iniciando em: 19/07/2023 10:17. Chuva entre 20 a 30 mm/h ou até 50 mm/dia. Baixo risco de alagamentos e pequenos deslizamentos, em cidades com tais áreas de risco.",
    "Evite enfrentar o mau tempo. Observe alteração nas encostas. Evite usar aparelhos eletrônicos ligados à tomada. Obtenha mais informações junto à Defesa Civil (telefone 199) e ao Corpo de Bombeiros (telefone 193).",
    "https://alertas2.inmet.gov.br/44166")
*/