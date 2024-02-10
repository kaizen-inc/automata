package inc.kaizen.automata.module.ide.model

data class Variable(
    var name: String = "",
    var value: String = "",
    var isEditable: Boolean = true
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is Variable -> other.name == name
                    && other.value == value
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + value.hashCode()
        return result.hashCode()
    }

    override fun toString(): String {
        return "$name ($value)"
    }
}
