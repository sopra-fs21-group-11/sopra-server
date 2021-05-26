package ch.uzh.ifi.hase.soprafs21.service.countdown;

import ch.uzh.ifi.hase.soprafs21.entity.Game;

public class EvaluationVisibleCountdown extends CountdownHelper{

    private boolean countdownEnd;

    public EvaluationVisibleCountdown(int time, Game callingGame){
        super(time, callingGame);
    }

    @Override
    public void onPropertyChange(boolean countdownEnd){
        super.support.firePropertyChange("EvaluationVisibleCdEnded"+super.gameId, this.countdownEnd, true);
        countdownEnd = true;
    }
}
