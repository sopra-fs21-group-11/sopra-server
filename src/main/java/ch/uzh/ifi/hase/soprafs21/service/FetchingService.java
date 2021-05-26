package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.Application;
import ch.uzh.ifi.hase.soprafs21.entity.RepositoryObjects.Card;
import ch.uzh.ifi.hase.soprafs21.rest.fetch.NominatimResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import javax.transaction.Transactional;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static java.nio.file.StandardOpenOption.CREATE_NEW;

@Service
@Transactional
public class FetchingService {
    private  WebClient.Builder builder;
    private String overpassBuilder = "";
    private List<Card> allCards;

    public FetchingService(){
        builder = WebClient.builder();
    }

    public String fetchingAvailable(){
        String response = "";
        try{
            response = builder
                    .build()
                    .get()
                    .uri("http://overpass-api.de/api/status")
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
        }catch (Exception ex){

        }
        return response;
    }

    public List<Card> fetchCardsFromCountry(String country, long population, List<Card> allCards){
        this.allCards = allCards;
        if(overpassBuilder==""){
            this.overpassBuilder = readOverpassFile();
        }
        String querry = overpassBuilder;
        long osmId = getOSMId(country);
        String osmIdBuilder = Long.toString(osmId);
        String i = "";
        while (("36"+i+osmIdBuilder).length()!=10){ //pad middle with "0"
            i+="0";
        }
        String definitiveId = "36"+i+osmIdBuilder;

        querry = querry.replace("####", definitiveId);
        querry = querry.replace("&&&&", Long.toString(population));


        Flux<DataBuffer> xmlResponse = null;
        try {
            xmlResponse = builder
                    .defaultHeader(HttpHeaders.CONTENT_TYPE, String.valueOf(MediaType.APPLICATION_XML))
                    .defaultHeader(HttpHeaders.ACCEPT, String.valueOf(MediaType.APPLICATION_XML))
                    .build()
                    .post()
                    .uri("http://overpass-api.de/api/interpreter")
                    .body(BodyInserters.fromValue(querry))
                    .retrieve()
                    .bodyToFlux(DataBuffer.class);

        }catch (Exception ex){
            if(ex.getMessage().contains("429")){
                throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS, "overpass has too many requests. Please wait some minutes.");
            }
            Application.logger.warn("Error in fetching: \n"+ex.getMessage());
        }
        //handle decimalFormat for xml coordinate parsing:
        DecimalFormat df = new DecimalFormat("0.00");
        df.setMaximumFractionDigits(2);
        String test = "";

        InputStream inStream = getInputStreamFromFluxDataBuffer(xmlResponse);
        //parse xml:
        Document doc = null;
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = factory.newDocumentBuilder();
            InputSource is = new InputSource( inStream);

            doc = docBuilder.parse(is);
        }catch (Exception ex){
        }
        doc.getDocumentElement().normalize();
        Element rootElement = doc.getDocumentElement();
        NodeList nodeList = rootElement.getElementsByTagName("node");

        List<Card> cardList = new ArrayList<>();

        for(int j = 0;j< nodeList.getLength();j++){
            Node cardElement = nodeList.item(j);
            Card newCard = new Card();
            try {
                String latStr = cardElement.getAttributes().getNamedItem("lat").toString();
                latStr = latStr.replace("lat=", "");
                latStr = latStr.replace("\"", "");
                String lonStr = cardElement.getAttributes().getNamedItem("lon").toString();
                lonStr = lonStr.replace("lon=", "");
                lonStr = lonStr.replace("\"", "");
                BigDecimal lat = new BigDecimal(latStr);
                BigDecimal lon = new BigDecimal(lonStr);
                lat.setScale(2, RoundingMode.CEILING);
                lon.setScale(2, RoundingMode.CEILING);

                newCard.seteCoordinate(lon.floatValue());
                newCard.setnCoordinate(lat.floatValue());
            }catch (Exception ex){ //if coordinate couldnt be set, we dont have to handle the rest...
                continue;
            }
            NodeList childList = cardElement.getChildNodes();
            for(int k = 0;k< childList.getLength();k++){
                Node tagElement = childList.item(k);
                if(tagElement.hasAttributes()) {
                    Node item = tagElement.getAttributes().getNamedItem("k");
                    if(item.getNodeValue().equals("name:en")){
                        try {
                            newCard.setName(tagElement.getAttributes().getNamedItem("v").getNodeValue());
                        }catch (Exception ex){continue;}
                    }
                    if(item.getNodeValue().equals("name")){
                        try {
                            newCard.setName(tagElement.getAttributes().getNamedItem("v").getNodeValue());
                        }catch (Exception ex){
                            continue;
                        }
                    }
                    if(item.getNodeValue().equals("population")){
                        try {
                            newCard.setPopulation(Long.parseLong(tagElement.getAttributes().getNamedItem("v").getNodeValue()));
                        }catch (Exception ex){
                        }
                    }
                }
            }
            //validate every card attribute and if not valid, we skip the card.
            if(newCard.getPopulation()!=0 && newCard.getnCoordinate()!=0 && newCard.geteCoordinate()!=0 && newCard.getName()!=null && newCard.getName().length()!=0) {
                boolean flag = false;
                for(Card existingCard : this.allCards){
                    try {
                        if (newCard.getName().equals(existingCard.getName())) {
                            flag = true;
                        }
                    } catch (Exception ex){
                        flag = true; //at exception, we skip
                    }
                }
                if(flag){
                    continue;
                }else {
                    cardList.add(newCard);
                }
            }
        }
        return cardList;
    }

    /**
     * gets the top result from nominatim
     * @param querry
     */
    private long getOSMId(String querry){

        NominatimResponse[] responses = builder.build()
                .get()
                .uri("https://nominatim.openstreetmap.org/search?q="+querry+"&format=json")
                .retrieve()
                .bodyToMono(NominatimResponse[].class)
                .block();
        return responses[0].getOsm_id();
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

    private InputStream getInputStreamFromFluxDataBuffer(Flux<DataBuffer> data){
        try {
            PipedOutputStream osPipe = new PipedOutputStream();

            PipedInputStream isPipe = new PipedInputStream(osPipe);

            DataBufferUtils.write(data, osPipe)
                    //.subscribeOn(Schedulers.elastic())
                    .doOnComplete(() -> {
                        try {
                            osPipe.close();
                        }
                        catch (IOException ignored) {
                        }
                    })
                    .subscribe(DataBufferUtils.releaseConsumer());
            return isPipe;
        } catch (Exception ex){
            return null;
        }
    }
}
