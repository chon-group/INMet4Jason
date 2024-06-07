package group.chon.pythia.ibgeGovBR;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Optional;

public class IBGEdtb {
    private List<IBGEdtbCity> cityList;

    public IBGEdtb() {
        try (Reader reader = new InputStreamReader(
                getClass().getResourceAsStream("/IBGE-DTB/ibgeDTB.json"), "UTF-8")) {
            Gson gson = new Gson();
            cityList = gson.fromJson(reader, new TypeToken<List<IBGEdtbCity>>() {}.getType());
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Falha ao ler o arquivo ibgeDTB.json", e);
        }
    }

    public Integer getIBGECod(String UF, String City) {
        Optional<IBGEdtbCity> municipio = cityList.stream()
                .filter(m -> m.getUF().equalsIgnoreCase(UF) && m.getCity().equalsIgnoreCase(City))
                .findFirst();
        return municipio.map(IBGEdtbCity::getIBGECod).orElse(null);
    }

}
