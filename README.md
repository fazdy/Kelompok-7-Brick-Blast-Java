# 🎮 Brick Blast Java

Game brick breaker klasik dibuat dengan Java Swing, dilengkapi dengan power-up, sistem multi-bola, dan tingkat kesulitan bertahap.

![Java](https://img.shields.io/badge/Java-21-orange)
![Platform](https://img.shields.io/badge/Platform-Windows%20%7C%20macOS%20%7C%20Linux-blue)

## 👥 Anggota Kelompok

| Nama | NRP |
|------|-----|
| M. Rizal Febrianto | 5025231258 |
| Kemal Aji Rajasa | 5025231263 |
| Faizal Aldy Armiriawan | 5025231266 |

## ✨ Fitur

- 🌈 **Brick warna-warni** dengan efek gradient
- ⚡ **5 Power-up** untuk dikumpulkan selama bermain
- 🔵 **Sistem multi-bola** - bisa sampai tak terbatas!
- 💪 **Brick kuat** yang membutuhkan 2x hit
- 📈 **5 Level** dengan tingkat kesulitan bertahap
- ❤️ **Sistem nyawa** dengan 3 nyawa awal
- ⏸️ **Fungsi pause**

## 🎯 Power-up

| Power-up | Simbol | Efek |
|----------|--------|------|
| Multi Ball | x3 | Bola terpecah menjadi 3 |
| Wide Paddle | + | Paddle 50% lebih lebar |
| Narrow Paddle | - | Paddle 30% lebih kecil |
| Slow Ball | S | Kecepatan bola berkurang |
| Extra Life | ♥ | +1 nyawa |

## 🕹️ Kontrol

| Tombol | Aksi |
|--------|------|
| ← → | Gerakkan paddle kiri/kanan |
| P | Pause / Lanjutkan |
| Enter | Mulai / Restart game |

## 🚀 Cara Menjalankan

### Prasyarat
- Java 8 atau lebih tinggi (disarankan Java 21)

### Opsi 1: Menggunakan IDE
Buka project di IDE favorit Anda (IntelliJ IDEA, Eclipse, VS Code) dan jalankan `Main.java`.

### Opsi 2: Command Line
```bash
cd src
javac *.java
java Main
```

### Opsi 3: Dengan path JDK spesifik
```bash
cd src
/path/to/javac *.java
/path/to/java Main
```

## 📁 Struktur Project

```
brick-blast-java/
├── src/
│   ├── Main.java          # Entry point
│   ├── GamePanel.java     # Logika game & rendering
│   ├── Ball.java          # Class bola dengan pergerakan
│   ├── Paddle.java        # Paddle yang dikontrol pemain
│   ├── MapGenerator.java  # Generator layout brick
│   ├── PowerUp.java       # Power-up yang jatuh
│   ├── PowerUpType.java   # Enum tipe power-up
│   └── GameState.java     # Enum state game
└── README.md
```

## 🎓 Konsep OOP yang Digunakan

- **Encapsulation** - Field private dengan method getter/setter
- **Enum** - `GameState` dan `PowerUpType` untuk type safety
- **Composition** - GamePanel berisi Ball, Paddle, MapGenerator
- **ArrayList** - Koleksi dinamis untuk sistem multi-bola
- **Polymorphism** - Efek power-up berbeda berdasarkan tipe

## 📊 Progresi Level

| Level | Baris Brick | Brick Kuat | Kecepatan Bola |
|-------|-------------|------------|----------------|
| 1 | 3 | 0% | Normal |
| 2 | 4 | 10% | +10% |
| 3 | 5 | 20% | +20% |
| 4 | 6 | 30% | +30% |
| 5 | 7 | 40% | +40% |

## 📝 Lisensi

Project ini dibuat untuk tujuan edukasi.

---

Dibuat dengan ❤️ menggunakan Java Swing
