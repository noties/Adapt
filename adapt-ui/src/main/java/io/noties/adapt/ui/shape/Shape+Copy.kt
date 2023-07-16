package io.noties.adapt.ui.shape

fun <S : Shape> S.copy(block: S.() -> Unit = {}): S {
    @Suppress("UNCHECKED_CAST")
    val s = clone() as S
    // copy common attributes
    copyTo(s)
    // apply block customization
    block(s)
    return s
}