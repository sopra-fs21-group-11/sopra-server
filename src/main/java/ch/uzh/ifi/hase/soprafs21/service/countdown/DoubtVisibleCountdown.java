package ch.uzh.ifi.hase.soprafs21.service.countdown;

import ch.uzh.ifi.hase.soprafs21.entity.Game;

public class DoubtVisibleCountdown extends CountdownHelper{

    private boolean countdownEnd;

    public DoubtVisibleCountdown(int time, Game callingGame){
        super(time, callingGame);
    }

    @Override
    public void onPropertyChange(boolean countdownEnd){
        super.support.firePropertyChange("DoubtVisibleCdEnded"+super.gameId, this.countdownEnd, true);
        countdownEnd = true;
    }
}
