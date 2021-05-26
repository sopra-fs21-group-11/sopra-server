package ch.uzh.ifi.hase.soprafs21.rest.socket_dto;

public class GameTurnDTO {

    private long gameId;
    private int placementIndex;
    private String axis;

    public GameTurnDTO() {
        //empty constructor
    }

    public long getGameId() {
        return gameId;
    }

    public void setGameId(long gameId) {
        this.gameId = gameId;
    }

    public int getPlacementIndex() {
        return placementIndex;
    }

    public void setPlacementIndex(int placementIndex) {
        this.placementIndex = placementIndex;
    }

    public String getAxis() {
        return axis;
    }

    public void setAxis(String axis) {
        this.axis = axis;
    }
}
