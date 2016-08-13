package model;

/**
 * Created by oleh on 13.12.15.
 */

public class Modes {
    public enum RoutingState {AWAIT, RUNNING, PAUSED, STOPPED }
    public enum Pack {THERE_AND_BACK_SAVED, THERE_AND_BACK_ROUT, SERV_ROUT, SERV_SAVED, DG, LC, SUPERVISOR, SUPERVISOR_FOUND}
    public enum User {VISIBLE, INVISIBLE}

    private static RoutingState routingState = RoutingState.AWAIT;
    private static User userMode = User.VISIBLE;

    public static RoutingState getRoutingState() {
        return routingState;
    }

    public static void setRoutingState(RoutingState _MODE) {
        routingState = _MODE;
    }

    public static void setUserMode(User _userMode) {
        userMode = _userMode;
    }

    public static User getUserMode() {
        return userMode;
    }
}
