/**
 * Represents an attribute of an entity.
 *
 * @property name The name of the attribute.
 * @property value The value of the attribute.
 * @property parent The parent entity of this attribute.
 */
data class Attribute(
    var name: String,
    var value: String,
    val parent: Entity
){
    init {
        validateName(name)
        validateValue(value)
        parent.attributes.add(this)
    }
}

/**
 * Changes the name of the attribute.
 *
 * @param newName The new name of the attribute.
 */
fun Attribute.rename(newName: String){
    validateName(newName)
    this.name = newName
}

/**
 * Changes the value of the attribute.
 *
 * @param newValue The new value of the attribute.
 */
fun Attribute.changeValue(newValue: String){
    validateValue(newValue)
    this.value = newValue
}

/**
 * Generates a formatted string representation of this attribute.
 *
 * @return The pretty-printed string.
 */
fun Attribute.prettyPrint(): String{
    return " $name=\"$value\""
}