import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Paddle {
    private int x;
    private int y = 550;
    private int width = 100;
    private int height = 8;
    private int moveSpeed = 20;
    private int limitLeft = 10;
    private int limitRight = 600; // 700 width - 100 paddle width roughly

    public Paddle(int startX) {
        this.x = startX;
    }

    public void moveRight() {
        if (x >= limitRight) {
            x = limitRight;
        } else {
            x += moveSpeed;
        }
    }

    public void moveLeft() {
        if (x <= limitLeft) {
            x = limitLeft;
        } else {
            x -= moveSpeed;
        }
    }

    public void draw(Graphics g) {
        g.setColor(Color.green);
        g.fillRect(x, y, width, height);
    }

    public int getX() {
        return x;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }
}
