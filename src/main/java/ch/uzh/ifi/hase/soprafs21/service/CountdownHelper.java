package ch.uzh.ifi.hase.soprafs21.service;

import ch.uzh.ifi.hase.soprafs21.entity.Game;
import ch.uzh.ifi.hase.soprafs21.entity.User;

public class CountdownHelper extends Thread {
    private boolean doStop = false;
    private int time;
    private volatile Game game;
    private final User doubtedUser;
    private final boolean evaluation;

    //three different constructors for different cds:
    public CountdownHelper(int time, Game callingGame, boolean _evaluation) { //constructor for evaluationcd
        this.time = time;
        this.game = callingGame;
        this.doubtedUser = null;
        this.evaluation = _evaluation;
    }

    public CountdownHelper(int time, Game callingGame, User doubtedUser) { //constructor for doubtcd
        this.time = time;
        this.game = callingGame;
        this.doubtedUser = doubtedUser;
        this.evaluation = false;
    }

    public CountdownHelper(int time, Game callingGame) { //constructor for turncd/visiblecd/evaluationvisiblecd
        this.time = time;
        this.game = callingGame;
        this.doubtedUser = null;
        this.evaluation = false;
    }


    public synchronized void doStop(){
        this.doStop=true;

    }

    private synchronized boolean keepRunning() {
        return this.doStop == false;
    }

    private synchronized void startEvaluationCheck(){
        if(!this.game.startActualEvaluation()){//evaluation hasnt been started
            this.game.startTurnCd();
        }
    }

    private synchronized void startEvaluationAfterCdFinished(){
        this.game.performEvaluationAfterGuessPresentOrCdEnded();
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
            if(!evaluation) { //if we have an evaluation, we wont start turncd but we start visiblecd:
                this.startEvaluationCheck();
            } else{
                startEvaluationAfterCdFinished();//we start the evaluation regardless if all guesses have came in.
            }

    }

    public User getDoubtedUser() {
        return doubtedUser;
    }
}
