import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;

/**
 * Merepresentasikan paddle yang dikontrol oleh pemain.
 * Menampilkan warna gradient dan gerakan yang halus.
 */
public class Paddle {
    private int x;
    private int y = 550;
    private int width;
    private int defaultWidth = 100;
    private int height = 12;
    private int moveSpeed = 30;
    private int limitLeft = 10;
    private int screenWidth = 692;

    // Warna gradient untuk paddle
    private Color paddleColorTop = new Color(0, 255, 200);
    private Color paddleColorBottom = new Color(0, 150, 180);

    public Paddle(int startX) {
        this.x = startX;
        this.width = defaultWidth;
    }

    public void moveRight() {
        int limitRight = screenWidth - width - 10;
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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Gambar paddle dengan gradient
        GradientPaint gradient = new GradientPaint(
                x, y, paddleColorTop,
                x, y + height, paddleColorBottom);
        g2d.setPaint(gradient);
        g2d.fillRoundRect(x, y, width, height, 10, 10);

        // Gambar highlight di atas
        g2d.setColor(new Color(255, 255, 255, 100));
        g2d.fillRoundRect(x + 5, y + 2, width - 10, 4, 5, 5);

        // Gambar border
        g2d.setColor(new Color(0, 100, 130));
        g2d.drawRoundRect(x, y, width, height, 10, 10);
    }

    public int getX() {
        return x;
    }

    public int getWidth() {
        return width;
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Reset paddle ke posisi tengah dan ukuran default
     */
    public void reset() {
        this.x = 310;
        this.width = defaultWidth;
        this.paddleColorTop = new Color(0, 255, 200);
        this.paddleColorBottom = new Color(0, 150, 180);
    }

    /**
     * Membuat paddle lebih lebar (power-up)
     */
    public void widen() {
        width = (int) (defaultWidth * 1.5);
        // Pastikan paddle tetap dalam batas layar
        if (x + width > screenWidth - 10) {
            x = screenWidth - width - 10;
        }
        // Ubah warna untuk menandakan power-up aktif
        paddleColorTop = new Color(50, 255, 100);
        paddleColorBottom = new Color(30, 180, 60);
    }

    /**
     * Membuat paddle lebih kecil (debuff)
     */
    public void narrow() {
        width = (int) (defaultWidth * 0.7);
        // Ubah warna untuk menandakan debuff aktif
        paddleColorTop = new Color(255, 100, 100);
        paddleColorBottom = new Color(180, 60, 60);
    }

    /**
     * Reset ukuran paddle ke default
     */
    public void resetSize() {
        width = defaultWidth;
        paddleColorTop = new Color(0, 255, 200);
        paddleColorBottom = new Color(0, 150, 180);
    }
}
