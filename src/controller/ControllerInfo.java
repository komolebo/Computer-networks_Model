package controller;

import view.FrameMain;

/**
 * Created by oleh on 13.12.15.
 */
public class ControllerInfo {
    private static int selectedID = -1;

    public static void selectID(int _id){
        selectedID = _id;

        FrameMain.panelInfo.selectIDInfo(_id);
    }

    public static void deselectID(){
        selectedID = -1;

        FrameMain.panelInfo.selectIDInfo(-1);
    }

    public static void refreshPanelProgress(){
        FrameMain.panelInfo.refreshTact();
    }
}
