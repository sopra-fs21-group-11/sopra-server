package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.rest.fetch.NominatimResponse;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import javax.transaction.Transactional;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Service
@Transactional
public class FetchingService {

    private  WebClient.Builder builder;
    private String overpassBuilder = "";

    public FetchingService(){
        WebClient.Builder builder = WebClient.builder();


    }

    public List<Card> fetchCardsFromCountry(int top, String country){
        List<Card> cardList = new ArrayList<>();
        if(overpassBuilder==""){
            this.overpassBuilder = readOverpassFile();
        }
        String querry = overpassBuilder;
        long osmId = getOSMId(country);
        querry.replace("####", Long.toString(osmId));
        querry.replace("&&&&", "100000");
    return null;
    }


    /**
     * gets the top result from nominatim
     * @param querry
     */
    private long getOSMId(String querry){

        NominatimResponse response = builder.build()
                .get()
                .uri("https://nominatim.openstreetmap.org/search?q="+querry+"&format=json")
                .retrieve()
                .bodyToMono(NominatimResponse.class)
                .block();
        return response.getOsm_id();
    }

    private String readOverpassFile(){
        String querry = "";
        try {
            File overpass = new File("overpassString.txt");
            Scanner scanner = new Scanner(overpass);
            while(scanner.hasNextLine()){
                querry+=scanner.nextLine();
            }
            scanner.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return querry;
    }
}
