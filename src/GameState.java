/**
 * Enum representing the different states of the Brick Blast game.
 * Used for managing game flow and UI rendering.
 */
public enum GameState {
    /** Initial state - shows start screen with instructions */
    START,

    /** Active gameplay state */
    PLAYING,

    /** Game is paused */
    PAUSED,

    /** Player has lost all lives */
    GAME_OVER,

    /** Player has completed current level */
    LEVEL_COMPLETE,

    /** Player has won the game (completed all levels) */
    WON
}
