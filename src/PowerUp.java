import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Rectangle;

/**
 * Represents a power-up that falls from destroyed bricks.
 * Players collect power-ups by catching them with the paddle.
 */
public class PowerUp {
    private int x;
    private int y;
    private int width = 30;
    private int height = 20;
    private int fallSpeed = 2;
    private PowerUpType type;
    private boolean active = true;

    public PowerUp(int x, int y, PowerUpType type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    /**
     * Move the power-up down
     */
    public void fall() {
        y += fallSpeed;
    }

    /**
     * Draw the power-up with gradient and icon
     */
    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        Color baseColor = type.getColor();
        Color darkColor = baseColor.darker();

        // Gradient background
        GradientPaint gradient = new GradientPaint(x, y, baseColor, x, y + height, darkColor);
        g2d.setPaint(gradient);
        g2d.fillRoundRect(x, y, width, height, 8, 8);

        // Border
        g2d.setColor(Color.WHITE);
        g2d.drawRoundRect(x, y, width, height, 8, 8);

        // Icon/Symbol based on type
        g2d.setColor(Color.WHITE);
        g2d.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
        String symbol = getSymbol();
        int textX = x + (width - g2d.getFontMetrics().stringWidth(symbol)) / 2;
        int textY = y + height / 2 + 4;
        g2d.drawString(symbol, textX, textY);
    }

    private String getSymbol() {
        switch (type) {
            case MULTI_BALL:
                return "x3";
            case WIDE_PADDLE:
                return "+";
            case NARROW_PADDLE:
                return "-";
            case SPEED_DOWN:
                return "S";
            case EXTRA_LIFE:
                return "♥";
            default:
                return "?";
        }
    }

    public Rectangle getRect() {
        return new Rectangle(x, y, width, height);
    }

    public PowerUpType getType() {
        return type;
    }

    public int getY() {
        return y;
    }

    public boolean isActive() {
        return active;
    }

    public void deactivate() {
        active = false;
    }
}
