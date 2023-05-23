package io.noties.adapt.ui.shape

/**
 * Utility function to build [Shape]
 *
 * ```kotlin
 * val shape: Shape = Shape.builder {
 *   Oval()
 * }
 * ```
 *
 * @see ShapeDrawable
 * @see ShapeFactory
 */
fun Shape.Companion.builder(
    block: ShapeFactoryBuilder
): Shape {
    return block(ShapeFactory.NoOp)
}

/**
 * Utility function to build [ShapeDrawable]
 *
 * ```kotlin
 * val drawable: ShapeDrawable<Unit> = Shape.drawable {
 *   Rectangle()
 * }
 * ```
 *
 * @see ShapeDrawable.invoke
 * @see Shape.newDrawable
 * @see ShapeFactory
 */
fun Shape.Companion.drawable(
    block: ShapeFactoryBuilder
): ShapeDrawable<Unit> {
    return builder(block).newDrawable()
}