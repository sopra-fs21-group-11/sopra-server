package ch.uzh.ifi.hase.soprafs21.service.countdown;

import ch.uzh.ifi.hase.soprafs21.Application;
import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public abstract class CountdownHelper extends Thread{
    private boolean doStop = false;
    private int time;
    private volatile Game game;

    public PropertyChangeSupport support;
    public String gameId;

    private boolean ended;//dont know if we can delete this or not

    public CountdownHelper(int time, Game callingGame) {
        gameId = Long.toString(callingGame.getId());

        this.time = time;
        this.game = callingGame;
        support = new PropertyChangeSupport(this);
    }
    public void addPropertyChangeListener(PropertyChangeListener pcl){
        support.addPropertyChangeListener(pcl);
    }

    public void removePropertyChangeListener(PropertyChangeListener pcl){
        support.removePropertyChangeListener(pcl);
    }
    public void onPropertyChange(boolean ended){
        support.firePropertyChange("CDEnded"+gameId, this.ended, true);
        ended = true;
    }

    public synchronized void doStop(){
        this.doStop=true;

    }

    private synchronized boolean keepRunning() {
        return this.doStop;
    }

    @Override
    public void run() {
        Application.logger.info(this.gameId+":\t"+this.getClass()+" started.");
        long now = System.currentTimeMillis();
        long countdown = now + time * 1000;
        while (now <= countdown) {
            try {
                if(keepRunning()){
                    return;
                }
                Thread.sleep(1000);
            }
            catch (Exception ex) {
                this.doStop();
            }
            now = System.currentTimeMillis();
        }
        onPropertyChange(true);//call observer and change property -> subclass implementation
        //startEvaluationAfterCdFinished();//we start the evaluation regardless if all guesses have came in.


    }
}
