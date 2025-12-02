import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Ball {
    private int posX;
    private int posY;
    private int dirX;
    private int dirY;
    private int size = 20;

    public Ball(int startX, int startY, int startDirX, int startDirY) {
        this.posX = startX;
        this.posY = startY;
        this.dirX = startDirX;
        this.dirY = startDirY;
    }

    public void move() {
        posX += dirX;
        posY += dirY;
    }

    public void draw(Graphics g) {
        g.setColor(Color.yellow);
        g.fillOval(posX, posY, size, size);
    }

    public void reverseX() {
        dirX = -dirX;
    }

    public void reverseY() {
        dirY = -dirY;
    }

    public int getX() {
        return posX;
    }

    public int getY() {
        return posY;
    }

    public int getDirX() {
        return dirX;
    }

    public int getDirY() {
        return dirY;
    }

    public int getSize() {
        return size;
    }

    public void setX(int x) {
        this.posX = x;
    }

    public void setY(int y) {
        this.posY = y;
    }

    public void setDirX(int dx) {
        this.dirX = dx;
    }

    public void setDirY(int dy) {
        this.dirY = dy;
    }

    public Rectangle getRect() {
        return new Rectangle(posX, posY, size, size);
    }
}
