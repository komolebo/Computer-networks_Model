package model.tools;

/**
 * Created by oleh on 14.12.15.
 */
public class Pair<L,R> {
    private L l;
    private R r;
    public Pair(L l, R r){
        this.l = l;
        this.r = r;
    }
    public L getL(){ return l; }
    public R getR(){ return r; }
    public void setL(L l){ this.l = l; }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Pair && ((Pair) obj).getL() == l && ((Pair) obj).getR() == r)
            return true;
        return super.equals(obj);
    }

    public void setR(R r){ this.r = r; }
}
