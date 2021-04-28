package ch.uzh.ifi.hase.soprafs21.service.countdown;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;

public class PlayersTurnCountdown extends CountdownHelper{

    private boolean countdownEnd;
    private User usersTurn;

    public PlayersTurnCountdown(int time, Game callingGame, User usersTurn){
        super(time, callingGame);
        this.usersTurn = usersTurn;
    }

    @Override
    public synchronized void doStop(){
        super.doStop();
        super.support.firePropertyChange("PlayerTurnCdStopped"+super.gameId, this.countdownEnd, true);
        countdownEnd = true;
    }

    @Override
    public void onPropertyChange(boolean countdownEnd){
        super.support.firePropertyChange("PlayerTurnCdEnded"+super.gameId, this.countdownEnd, true);
        countdownEnd = true;
    }
}
