package ch.uzh.ifi.hase.soprafs21.service.countdown;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;

public class DoubtCountdown extends CountdownHelper{

    private final User doubtedUser;
    private boolean countdownEnd;

    public DoubtCountdown(int time, Game callingGame, User doubtedUser){
        super(time, callingGame);
        this.doubtedUser = doubtedUser;
    }

    public User getDoubtedUser() {
        return doubtedUser;
    }

    @Override
    public synchronized void doStop(){
        super.doStop();
        super.support.firePropertyChange("DoubtCdStopped"+super.gameId, this.countdownEnd, true);
        countdownEnd = true;
    }

    @Override
    public void onPropertyChange(boolean countdownEnd){
        super.support.firePropertyChange("DoubtCdEnded"+super.gameId, this.countdownEnd, true);
        countdownEnd = true;
    }

}
