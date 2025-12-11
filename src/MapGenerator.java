import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

/**
 * Generates and manages the brick layout for each level.
 * Features colorful rainbow bricks with gradient effects.
 * 
 * Brick values:
 * 0 = empty (destroyed)
 * 1 = normal brick
 * 2 = power-up brick (drops power-up when destroyed)
 * 3 = strong brick (needs 2 hits)
 */
public class MapGenerator {
    private int[][] map;
    private int brickWidth;
    private int brickHeight;
    private int totalBricks;
    private Random random = new Random();

    // Rainbow colors for brick rows
    private static final Color[] BRICK_COLORS = {
            new Color(255, 87, 87), // Red
            new Color(255, 165, 87), // Orange
            new Color(255, 230, 87), // Yellow
            new Color(87, 255, 130), // Green
            new Color(87, 210, 255), // Cyan
            new Color(150, 120, 255), // Purple
            new Color(255, 120, 200) // Pink
    };

    // Darker versions for gradient
    private static final Color[] BRICK_COLORS_DARK = {
            new Color(180, 50, 50),
            new Color(180, 110, 50),
            new Color(180, 160, 50),
            new Color(50, 180, 90),
            new Color(50, 150, 180),
            new Color(100, 80, 180),
            new Color(180, 80, 140)
    };

    // Special brick colors
    private static final Color POWERUP_BRICK_COLOR = new Color(100, 200, 255);
    private static final Color POWERUP_BRICK_DARK = new Color(50, 150, 200);
    private static final Color STRONG_BRICK_COLOR = new Color(150, 150, 170);
    private static final Color STRONG_BRICK_DARK = new Color(100, 100, 120);

    /**
     * Create map for specific level
     * 
     * @param level Current level (1-5)
     */
    public MapGenerator(int level) {
        int rows = 2 + level; // Level 1: 3 rows, Level 5: 7 rows
        int cols = 7;

        // Power-up and strong brick percentages based on level
        int powerUpPercent = 15 + level * 3; // 18% to 30%
        int strongPercent = (level - 1) * 10; // 0% to 40%

        map = new int[rows][cols];
        totalBricks = 0;

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                int rand = random.nextInt(100);

                if (rand < strongPercent && level >= 2) {
                    map[i][j] = 3; // Strong brick
                } else if (rand < strongPercent + powerUpPercent) {
                    map[i][j] = 2; // Power-up brick
                } else {
                    map[i][j] = 1; // Normal brick
                }
                totalBricks++;
            }
        }

        brickWidth = 540 / cols;
        brickHeight = 150 / rows;
    }

    public void draw(Graphics2D g) {
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[0].length; j++) {
                if (map[i][j] > 0) {
                    int x = j * brickWidth + 80;
                    int y = i * brickHeight + 50;

                    Color topColor, bottomColor;

                    if (map[i][j] == 3) {
                        // Strong brick - gray/silver
                        topColor = STRONG_BRICK_COLOR;
                        bottomColor = STRONG_BRICK_DARK;
                    } else if (map[i][j] == 2) {
                        // Power-up brick - light blue with sparkle
                        topColor = POWERUP_BRICK_COLOR;
                        bottomColor = POWERUP_BRICK_DARK;
                    } else {
                        // Normal brick - rainbow based on row
                        int colorIndex = i % BRICK_COLORS.length;
                        topColor = BRICK_COLORS[colorIndex];
                        bottomColor = BRICK_COLORS_DARK[colorIndex];
                    }

                    // Draw brick with gradient
                    GradientPaint gradient = new GradientPaint(
                            x, y, topColor,
                            x, y + brickHeight, bottomColor);
                    g.setPaint(gradient);
                    g.fillRoundRect(x, y, brickWidth - 2, brickHeight - 2, 8, 8);

                    // Draw highlight on top
                    g.setColor(new Color(255, 255, 255, 80));
                    g.fillRoundRect(x + 3, y + 2, brickWidth - 8, brickHeight / 3, 5, 5);

                    // Special indicator for strong bricks
                    if (map[i][j] == 3) {
                        g.setColor(new Color(255, 255, 255, 150));
                        g.setStroke(new BasicStroke(2));
                        g.drawLine(x + 5, y + brickHeight / 2, x + brickWidth - 7, y + brickHeight / 2);
                    }

                    // Star indicator for power-up bricks
                    if (map[i][j] == 2) {
                        g.setColor(new Color(255, 255, 100));
                        g.setFont(new java.awt.Font("Arial", java.awt.Font.BOLD, 12));
                        g.drawString("★", x + brickWidth / 2 - 5, y + brickHeight / 2 + 4);
                    }

                    // Draw border
                    g.setStroke(new BasicStroke(1));
                    g.setColor(new Color(0, 0, 0, 100));
                    g.drawRoundRect(x, y, brickWidth - 2, brickHeight - 2, 8, 8);
                }
            }
        }
    }

    /**
     * Hit a brick and return if it should drop a power-up
     * 
     * @return true if brick was a power-up brick
     */
    public boolean hitBrick(int row, int col) {
        if (map[row][col] == 3) {
            // Strong brick - reduce to normal
            map[row][col] = 1;
            return false;
        } else if (map[row][col] == 2) {
            // Power-up brick - destroy and signal power-up drop
            map[row][col] = 0;
            totalBricks--;
            return true;
        } else if (map[row][col] == 1) {
            // Normal brick - destroy
            map[row][col] = 0;
            totalBricks--;
            return false;
        }
        return false;
    }

    public void setBrickValue(int value, int row, int col) {
        map[row][col] = value;
    }

    public int getBrickValue(int row, int col) {
        return map[row][col];
    }

    public int getRows() {
        return map.length;
    }

    public int getCols() {
        return map[0].length;
    }

    public int getBrickWidth() {
        return brickWidth;
    }

    public int getBrickHeight() {
        return brickHeight;
    }

    public int getTotalBricks() {
        return totalBricks;
    }
}
