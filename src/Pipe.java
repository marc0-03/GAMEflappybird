import java.awt.*;

public class Pipe {
    public int X,Y;
    private Rectangle hitBox;

    public Pipe(int x, int y) {
        X=x;
        Y=y;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

}