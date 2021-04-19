package ch.uzh.ifi.hase.soprafs21.entity;

import ch.uzh.ifi.hase.soprafs21.entity.Cards.Card;
import ch.uzh.ifi.hase.soprafs21.entity.Cards.SwissLocationCard;
import ch.uzh.ifi.hase.soprafs21.entity.ValueCategories.ValueCategory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class Deck extends Stack {
    //This is not the definitive implementation. I just add the cards of the csv to test functionalities.
    private Stack<Card> cards;
    private List<ValueCategory> possibleComparisonStrategies;


    public Deck(){
        cards = new Stack<>();
        List<Card> locationCards = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("src/bunzendataset.csv"))) {
            String line;

            int cardIdCounter = 0;
            while ((line = br.readLine()) != null) {
                cardIdCounter++;
                String[] values = line.split(";");
                //skip title row
                if(values[0].equals("Locationname")){
                    continue;
                }

                SwissLocationCard newCard = new SwissLocationCard();
                newCard.setCardId(cardIdCounter);
                newCard.setLocationName(values[0]);
                //split coordinates
                String toFloat = values[1].split(" ")[0]+"."+values[1].split(" ")[1];
                newCard.setNsCoordinates(Float.parseFloat(toFloat));
                toFloat = values[2].split(" ")[0]+"."+values[2].split(" ")[1];
                newCard.setEwCoordinates(Float.parseFloat(toFloat));
                if(values[3].length()==0){//Population
                    continue;
                }else{
                    newCard.setPopulation(Integer.parseInt(values[3]));
                }
                if(values[4].length()==0){//Area
                    continue;
                }
                else{
                    newCard.setArea(Float.parseFloat(values[4]));
                }
                if(values[5].length()==0){//Height
                    continue;
                }else{
                    newCard.setHeight(Integer.parseInt(values[5]));
                }
                //6: canton
                //7: flag for sightseeing
                //8: flag for River
                //9: flag for Mountain
                locationCards.add(newCard);
            }
        } catch (Exception ex){


        }
        for(int i = 0;i<=30;i++){//hardcoded: A deck contains 30 cards. TODO: change this according to gamesettings.
            Random rand = new Random();
            cards.add(locationCards.get(rand.nextInt(locationCards.size()-1)));//pick a random card out of the dataset
        }

    }

    /**
     * Picks the top card and returns it
     * @return The card that is picked.
     */
    public Card pop(){
        return this.cards.pop();
    }






}
