import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Merepresentasikan bola dalam game Brick Blast.
 * Menampilkan efek gradient yang bersinar untuk tampilan visual yang menarik.
 */
public class Ball {
    private int posX;
    private int posY;
    private double dirX;
    private double dirY;
    private int size = 20;

    // Kecepatan dasar sebagai referensi
    private static final double BASE_SPEED_X = 2.5;
    private static final double BASE_SPEED_Y = 3.5;

    // Warna untuk efek gradient
    private static final Color BALL_COLOR_INNER = new Color(255, 255, 100);
    private static final Color BALL_COLOR_OUTER = new Color(255, 180, 0);
    private static final Color GLOW_COLOR = new Color(255, 200, 50, 100);

    public Ball(int startX, int startY, double startDirX, double startDirY) {
        this.posX = startX;
        this.posY = startY;
        this.dirX = startDirX;
        this.dirY = startDirY;
    }

    public void move() {
        posX += (int) dirX;
        posY += (int) dirY;
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gambar efek glow (lingkaran besar semi-transparan)
        g2d.setColor(GLOW_COLOR);
        g2d.fillOval(posX - 4, posY - 4, size + 8, size + 8);

        // Gambar bola dengan gradient
        GradientPaint gradient = new GradientPaint(
                posX, posY, BALL_COLOR_INNER,
                posX + size, posY + size, BALL_COLOR_OUTER);
        g2d.setPaint(gradient);
        g2d.fillOval(posX, posY, size, size);

        // Gambar highlight (lingkaran putih kecil untuk efek kilau)
        g2d.setColor(new Color(255, 255, 255, 180));
        g2d.fillOval(posX + 4, posY + 3, 6, 6);
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

    public double getDirX() {
        return dirX;
    }

    public double getDirY() {
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

    public void setDirX(double dx) {
        this.dirX = dx;
    }

    public void setDirY(double dy) {
        this.dirY = dy;
    }

    public Rectangle getRect() {
        return new Rectangle(posX, posY, size, size);
    }

    /**
     * Membuat salinan bola ini dengan arah yang sedikit berbeda (untuk multi-bola)
     */
    public Ball clone(double angleOffset) {
        double newDirX = dirX + angleOffset;
        double newDirY = dirY;
        return new Ball(posX, posY, newDirX, newDirY);
    }

    /**
     * Memperlambat kecepatan bola
     */
    public void slowDown() {
        dirX *= 0.7;
        dirY *= 0.7;
        // Pastikan kecepatan minimum
        if (Math.abs(dirX) < 1.5)
            dirX = dirX > 0 ? 1.5 : -1.5;
        if (Math.abs(dirY) < 2.0)
            dirY = dirY > 0 ? 2.0 : -2.0;
    }

    /**
     * Mendapatkan kecepatan dasar yang disesuaikan dengan level
     */
    public static double getBaseSpeedX(int level) {
        return BASE_SPEED_X + (level - 1) * 0.3;
    }

    public static double getBaseSpeedY(int level) {
        return BASE_SPEED_Y + (level - 1) * 0.4;
    }
}
