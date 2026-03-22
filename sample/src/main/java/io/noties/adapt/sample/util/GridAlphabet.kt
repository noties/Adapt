package io.noties.adapt.sample.util

// 5x7 grid
object GridAlphabet {
    data class Pos(val x: Int, val y: Int) {
        operator fun plus(other: Pos): Pos {
            return Pos(x = x + other.x, y = y + other.y)
        }
    }

    val glyphs = mapOf(
        'A' to listOf(
            Pos(2, 0),
            Pos(1, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(0, 6),
            Pos(3, 1),
            Pos(4, 2),
            Pos(4, 3),
            Pos(4, 4),
            Pos(4, 5),
            Pos(4, 6),
            Pos(1, 4), Pos(2, 4), Pos(3, 4),
        ),
        'B' to listOf(
            Pos(0, 0),
            Pos(0, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(0, 6),
            Pos(1, 0),
            Pos(2, 0),
            Pos(3, 0),
            Pos(4, 1),
            Pos(4, 2),
            Pos(3, 3), Pos(2, 3), Pos(1, 3),
            Pos(4, 4),
            Pos(4, 5),
            Pos(3, 6), Pos(2, 6), Pos(1, 6),
        ),
        'C' to listOf(
            Pos(4, 1),
            Pos(3, 0), Pos(2, 0), Pos(1, 0),
            Pos(0, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(1, 6), Pos(2, 6), Pos(3, 6),
            Pos(4, 5),
        ),
        'D' to listOf(
            Pos(0, 0),
            Pos(0, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(0, 6),
            Pos(1, 0), Pos(2, 0), Pos(3, 0),
            Pos(4, 1),
            Pos(4, 2),
            Pos(4, 3),
            Pos(4, 4),
            Pos(4, 5),
            Pos(3, 6), Pos(2, 6), Pos(1, 6),
        ),
        'E' to listOf(
            Pos(0, 0),
            Pos(0, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(0, 6),
            Pos(1, 0), Pos(2, 0), Pos(3, 0), Pos(4, 0),
            Pos(1, 3), Pos(2, 3), Pos(3, 3),
            Pos(1, 6), Pos(2, 6), Pos(3, 6), Pos(4, 6),
        ),
        'F' to listOf(
            Pos(0, 0),
            Pos(0, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(0, 6),
            Pos(1, 0), Pos(2, 0), Pos(3, 0), Pos(4, 0),
            Pos(1, 3), Pos(2, 3), Pos(3, 3),
        ),
        'P' to listOf(
            Pos(0, 0),
            Pos(0, 1),
            Pos(0, 2),
            Pos(0, 3),
            Pos(0, 4),
            Pos(0, 5),
            Pos(0, 6),
            Pos(1, 0), Pos(2, 0), Pos(3, 0),
            Pos(4, 1),
            Pos(4, 2),
            Pos(3, 3), Pos(2, 3), Pos(1, 3),
        ),
        'T' to listOf(
            Pos(2, 0),
            Pos(2, 1),
            Pos(2, 2),
            Pos(2, 3),
            Pos(2, 4),
            Pos(2, 5),
            Pos(2, 6),
            Pos(0, 0), Pos(1, 0), Pos(2, 0), Pos(3, 0), Pos(4, 0),
        )
    )

    val available: List<Char> = glyphs.map { it.key }
}