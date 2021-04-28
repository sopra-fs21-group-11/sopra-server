package ch.uzh.ifi.hase.soprafs21.service.countdown;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;

public class WaitForGuessCountdown extends CountdownHelper{

    private boolean countdownEnd;

    public WaitForGuessCountdown(int time, Game callingGame){
        super(time, callingGame);
    }

    @Override
    public synchronized void doStop(){
        super.doStop();
        super.support.firePropertyChange("GuessCdStopped", this.countdownEnd, true);
        countdownEnd = true;
    }

    @Override
    public void onPropertyChange(boolean countdownEnd){
        super.support.firePropertyChange("GuessCdEnded", this.countdownEnd, true);
        countdownEnd = true;
    }
}
