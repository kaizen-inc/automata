package inc.kaizen.automate.core.template

data class ModuleTemplate(
    var name: String = "",
    var templatePath: String = "",
    var isDefault: Boolean = false,
) {
    override fun equals(other: Any?): Boolean {
        return when (other) {
            is ModuleTemplate -> other.name == name
                    && other.templatePath == templatePath
            else -> false
        }
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + templatePath.hashCode()
        return result
    }

    override fun toString(): String {
        return "$name ($templatePath)"
    }
}


