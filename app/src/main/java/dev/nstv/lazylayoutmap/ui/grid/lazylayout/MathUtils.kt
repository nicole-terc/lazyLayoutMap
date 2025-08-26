package dev.nstv.lazylayoutmap.ui.grid.lazylayout

fun intersectsViewport(
    left: Float,
    top: Float,
    width: Float,
    height: Float,
    viewportWidth: Int,
    viewportHeight: Int
): Boolean {
    val right = left + width
    val bottom = top + height
    return right > 0f && bottom > 0f && left < viewportWidth && top < viewportHeight
}