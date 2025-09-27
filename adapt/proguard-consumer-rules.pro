# Preserve all subclasses of Item but allow obfuscation of their names
# this is done, because internally each impl class is used to identify
#   each type of instance by automatically by creating view-types. so, in order to function
#   each instance must have dedicated class, which is not always true after proguard optimization,
#   which could combine 2 (very) similar classes. internally each class is cached in a map which
#   uses its hashcode, which after optimization might become the same (if classes are similar),
#   but their ids might collide, so a duplicate item could be thrown for 2 distinct items
#   (especially when it is of a item that is intended for a single usage, and thus might
#   have id == 0 to just indicate its uniqueness), or when a list contains some amount of such
#   similar but distinct items and their ids might collide.
-keep,allowobfuscation class * extends io.noties.adapt.Item { *; }