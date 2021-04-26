package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;

public class CountdownHelper extends Thread {
    private boolean doStop = false;
    private int time;
    private volatile Game game;
    private final User doubtedUser;

    public CountdownHelper(int time, Game callingGame, User doubtedUser) {
        this.time = time;
        this.game = callingGame;
        this.doubtedUser = doubtedUser;
    }

    public synchronized void doStop(){
        this.doStop=true;

    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }



    private synchronized  void startTurnCd(){

        this.game.startTurnCd();
    }

    @Override
    public void run() {
            long now = System.currentTimeMillis();
            long countdown = now + time * 1000;
            while (now <= countdown && keepRunning()) {
                try {
                    Thread.sleep(1000);
                }
                catch (Exception ex) {
                }
                now = System.currentTimeMillis();
            }

            this.startTurnCd();

    }

    public User getDoubtedUser() {
        return doubtedUser;
    }
}
