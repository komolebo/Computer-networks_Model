package model;

/**
 * Created by oleh on 11.12.15.
 */
public abstract class AbstractObject {
    private int ID;

    private static int ID_STATIC = 0;

    public AbstractObject(){
        ID = ID_STATIC++;
    }

    public AbstractObject(int id){
        ID = id;

        // Lower id can come later
        if (ID_STATIC <= id + 1) ID_STATIC = id + 1;
    }

    public int getID(){
        return ID;
    }
}
