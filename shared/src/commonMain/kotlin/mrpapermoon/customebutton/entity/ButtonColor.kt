package mrpapermoon.customebutton.entity

sealed class ButtonColor(val name: String) {
    data object Pink : ButtonColor("Pink")
    data object Lavender : ButtonColor("Lavender")
    data object Blue : ButtonColor("Blue")
    data object Mint : ButtonColor("Mint")
    data object Peach : ButtonColor("Peach")

    data class Custom(val argb: Int) : ButtonColor("#" + argb.toUInt().toString(16).padStart(8, '0'))

    companion object {
        val entries: List<ButtonColor> = listOf(Pink, Lavender, Blue, Mint, Peach)

        /** Reads both legacy preset names and custom #AARRGGBB values. */
        fun valueOf(value: String): ButtonColor {
            entries.firstOrNull { it.name == value }?.let { return it }
            require(value.length == 9 && value.startsWith('#')) { "Invalid button color: $value" }
            val argb = requireNotNull(value.substring(1).toUIntOrNull(16)) { "Invalid button color: $value" }
            return Custom(argb.toInt())
        }
    }
}
