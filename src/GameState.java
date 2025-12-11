/**
 * Enum yang merepresentasikan state-state berbeda dalam game.
 * Digunakan untuk mengelola alur game dan rendering UI.
 */
public enum GameState {
    /** State awal - menampilkan layar start dengan instruksi */
    START,

    /** State gameplay aktif */
    PLAYING,

    /** Game sedang di-pause */
    PAUSED,

    /** Pemain kehilangan semua nyawa */
    GAME_OVER,

    /** Pemain menyelesaikan level saat ini */
    LEVEL_COMPLETE,

    /** Pemain memenangkan game (menyelesaikan semua level) */
    WON
}
