package io.noties.adapt.sample.explore

object ExplorePath2 {
    // now, each must be also done with relative -> dimension.exact and dimension.relative
    interface Builder {
        // TODO: change to float?
        // TODO: view-port and scale
        // TODO: if we would be scaled, could shape still position it?

        fun m(x: Int, y: Int): Builder
        fun M(x: Int, y: Int): Builder

        fun l(x: Int, y: Int): Builder
        fun L(x: Int, y: Int): Builder

        fun h(x: Int): Builder
        fun H(x: Int): Builder

        fun v(y: Int): Builder
        fun V(y: Int): Builder

        // TODO: maybe all can use pair then?
        fun q(control: Pair<Int, Int>, end: Pair<Int, Int>): Builder
        fun Q(control: Pair<Int, Int>, end: Pair<Int, Int>): Builder

        fun t(x: Int, y: Int): Builder
        fun T(x: Int, y: Int): Builder

        fun c(control1: Pair<Int, Int>, control2: Pair<Int, Int>, end: Pair<Int, Int>): Builder
        fun C(control1: Pair<Int, Int>, control2: Pair<Int, Int>, end: Pair<Int, Int>): Builder

        fun s(control2: Pair<Int, Int>, end: Pair<Int, Int>): Builder
        fun S(control2: Pair<Int, Int>, end: Pair<Int, Int>): Builder

        fun a(radius: Pair<Int, Int>, rotation: Int, arc: Boolean, sweep: Boolean, end: Pair<Int, Int>): Builder
        fun A(radius: Pair<Int, Int>, rotation: Int, arc: Boolean, sweep: Boolean, end: Pair<Int, Int>): Builder

        fun z(): Builder
        fun Z() = z()
    }
}