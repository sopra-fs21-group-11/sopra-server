package ch.uzh.ifi.hase.soprafs21.entity;

import java.util.*;

public class Evaluation {

    private List<Map.Entry<User, Integer>> guesses;
    private Queue<Map.Entry<User, String>> players;
    private int tokensOnNearestGuess;
    private int tokensOnCorrectGuess;
    private boolean tokenShared = false;

    public Evaluation(Queue<Map.Entry<User, String>> players, int correct, int nearest) {
        guesses = new ArrayList<>();
        this.players = players; //Store all players in a local queue because we need to check them
        this.tokensOnCorrectGuess = correct;
        this.tokensOnNearestGuess = nearest;
    }

    /**
     *
     * @param user The guessing user (needed for token handling)
     * @param guess Integer that represents the guessed nr of wrong cards
     * @return true if all players have placed a guess.
     */
    public boolean addGuess(User user, Integer guess){
        Map.Entry<User, Integer> newGuess = new AbstractMap.SimpleEntry<>(user, guess);
        guesses.add(newGuess);
        return evaluationContainsAllGuesses();
    }

    public void shareTokens(int wrongCards){
        if(tokenShared){return;}//we check if we already shared the tokens.
        boolean correct = false;
        for(var guess : guesses){
            if(wrongCards == guess.getValue()){
                correct = true;
                guess.getKey().setCurrentToken(guess.getKey().getCurrentToken()+tokensOnCorrectGuess);//add tokens to player
            }
        }
        if(correct){tokenShared=true;
        return;}
        int distance = 99;
        List<Map.Entry<User, Integer>> nearestGuessList = new ArrayList<>();
        if(!correct){//no correct guess present -> continue with nearest:
            for(var guess : guesses){
                int currentDistance = Math.abs(wrongCards -guess.getValue());
                if(distance > currentDistance){
                    //currentDistance is smaller -> add to list and clear.
                    nearestGuessList.clear();
                    nearestGuessList.add(guess);
                    distance = currentDistance;
                } else if(distance == currentDistance){
                    nearestGuessList.add(guess);
                }
            }
            for(var guessToShareToken : nearestGuessList){
                guessToShareToken.getKey().setCurrentToken(guessToShareToken.getKey().getCurrentToken()+tokensOnNearestGuess);//add tokens to player
            }
        }
        tokenShared=true;
    }

    private boolean evaluationContainsAllGuesses(){
        for(Map.Entry<User, String> player : players){
            boolean match = false;
            for(Map.Entry<User, Integer> guess : guesses){
                if(player.getKey().getId() == guess.getKey().getId()){
                    match = true;
                    break;
                }
            }
            if(!match){ //a player has not yet sent his guess.
                return false;
            }
        }
        return true;//loop has finished -> every player in the game guessed.
    }
}
