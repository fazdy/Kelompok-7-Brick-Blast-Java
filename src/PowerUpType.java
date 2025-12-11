import java.awt.Color;

/**
 * Enum representing different types of power-ups in the game.
 * Each power-up has a display name, color, and description.
 */
public enum PowerUpType {
    MULTI_BALL("Multi Ball", new Color(50, 150, 255), "Bola jadi 3!"),
    WIDE_PADDLE("Wide Paddle", new Color(50, 255, 100), "Paddle lebih lebar!"),
    NARROW_PADDLE("Narrow Paddle", new Color(255, 80, 80), "Paddle mengecil!"),
    SPEED_DOWN("Slow Ball", new Color(255, 230, 50), "Bola melambat!"),
    EXTRA_LIFE("Extra Life", new Color(200, 100, 255), "+1 Nyawa!");

    private final String displayName;
    private final Color color;
    private final String description;

    PowerUpType(String displayName, Color color, String description) {
        this.displayName = displayName;
        this.color = color;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Color getColor() {
        return color;
    }

    public String getDescription() {
        return description;
    }
}
