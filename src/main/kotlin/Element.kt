/**
 * Represents an element in the XML structure.
 */
sealed interface Element {
    val name: String
    val parent: Entity?

    /**
     * Calculates the depth of the element in the tree.
     */
    val depth: Int
        get() = if (parent == null)
            1
        else
            1 + parent!!.depth

    /**
     * Computes the path of the element in the tree.
     */
    val path: String
        get() = if (parent == null)
            "/" + this.name
        else
            parent!!.path + "/" + this.name

    /**
     * Accepts a visitor function to process this element.
     *
     * @param visitor A function that processes an element and returns a Boolean.
     */
    fun accept(visitor: (Element) -> Boolean) {
        visitor(this)
    }

    /**
     * Generates a formatted string representation of the entity and its children in an XML-like format.
     *
     * @param indentation The current indentation level (default is an empty string).
     * @return The pretty-printed XML string.
     */
    fun prettyPrint(indentation: String = ""): String
}

/**
 * Represents an XML entity which can have attributes and child elements.
 *
 * @property name The name of the entity.
 * @property parent The parent entity of this entity.
 */
data class Entity(
    override var name: String,
    override val parent: Entity? = null
) : Element {
    val children: MutableList<Element> = mutableListOf()
    var attributes: MutableList<Attribute> = mutableListOf()

    init {
        validateName(name)
        parent?.children?.add(this)
    }

    /**
     * Accepts a visitor function to traverse elements in a hierarchical structure.
     * The function recursively visits each child element if the visitor returns true for this element.
     *
     * @param visitor Function that takes an `Element` and returns a `Boolean` indicating whether to visit its children.
     */
    override fun accept(visitor: (Element) -> Boolean) {
        if (visitor(this))
            children.forEach {
                it.accept(visitor)
            }
    }

    /**
     * Generates a formatted string representation of this entity and its children in an XML-like format.
     *
     * @param indentation The current indentation level (default is an empty string).
     * @return the pretty-printed string.
     */
    override fun prettyPrint(indentation: String): String {
        var text = "$indentation<$name"
        this.attributes.forEach {
            text += it.prettyPrint()
        }
        if (this.children.isEmpty()) {
            text += "/>"
        } else {
            text += ">"
            val hasText = children.any { it is Text }
            this.children.forEach {
                if (!hasText)
                    text += "\n"
                text += it.prettyPrint("$indentation\t")
            }
            text += if (hasText) "</$name>" else "\n$indentation</$name>"
        }
        return text
    }
}

/**
 * This class represents a text element that implements the Element interface.
 *
 * @property name The name of the text element.
 * @property parent The parent entity of this text element.
 */
data class Text(
    override val name: String,
    override val parent: Entity? = null
) : Element {
    init {
        validateValue(name)
        parent?.children?.add(this)
    }

    /**
     * Generates a formatted string representation of this text.
     *
     * @param indentation The current indentation level (default is an empty string).
     * @return The pretty-printed string.
     */
    override fun prettyPrint(indentation: String): String {
        return this.name
    }
}

/**
 * Removes this entity from its parent and recursively removes its children.
 */
fun Entity.remove() {
    this.parent?.children?.remove(this)
    children.forEach {
        if (it is Entity)
            it.remove()
    }
}

/**
 * Finds an attribute within this entity's attributes list, given a specific name.
 *
 * @param name The name of the attribute.
 */
fun Entity.findAttribute(name: String): Attribute? {
    var attribute: Attribute? = null
    attributes.forEach {
        if (it.name == name)
            attribute = it
    }
    return attribute
}

/**
 * Adds an attribute to this entity.
 *
 * @param name The name of the attribute.
 * @param value The value of the attribute.
 */
fun Entity.addAttribute(name: String, value: String) {
    if(this.findAttribute(name) != null)  {
        throw AttributeAlreadyExistsException("$name attribute already exists.")
    }
    Attribute(name, value, this)
}

/**
 * Removes an attribute from this entity.
 *
 * @param name The name of the attribute to remove.
 * @throws InvalidNameException If the provided attribute name is invalid.
 * @throws NotFoundException If the attribute with the given name does not exist in the entity.
 */
fun Entity.removeAttribute(name: String) {
    validateName(name)
    val attribute = this.findAttribute(name)
    if(attribute == null) {
        throw NotFoundException("$name attribute not found.")
    }
    this.attributes.remove(attribute)
}

/**
 * Changes the value of an existing attribute.
 *
 * @param name The name of the attribute.
 * @param newValue The new value of the attribute.
 * @throws InvalidNameException If the provided attribute name or new value is invalid.
 * @throws NotFoundException If the attribute with the given name does not exist in the entity.
 */
fun Entity.changeAttribute(name: String, newValue: String) {
    validateName(name)
    val attribute = this.findAttribute(name)
    if(attribute == null) {
        throw NotFoundException("$name attribute not found.")
    }
    attribute.changeValue(newValue)
}

/**
 * Renames an existing attribute.
 *
 * @param oldName The current name of the attribute.
 * @param newName The new name of the attribute.
 * @throws InvalidNameException If either the old or new attribute name is invalid.
 * @throws NotFoundException If the attribute with the old name provided does not exist in the entity,
 * @throws EntitiesWithTheSameNameException If the new name is the same as the old name.
 *
 */
fun Entity.renameAttribute(oldName: String, newName: String) {
    validateName(oldName)

    val attribute = this.findAttribute(oldName)
    if(attribute == null) {
        throw NotFoundException("$oldName attribute not found.")
    }

    if(oldName == newName) {
        throw EntitiesWithTheSameNameException("New name must be different from the old one.")
    }

    attribute.rename(newName)
}

/**
 * Retrieves a list of child elements associated with this entity.
 *
 * @return A list of child elements.
 */
fun Entity.getChildren(): List<Element> {
    return this.children
}

/**
 * Retrieves the parent entity of this entity, if it exists.
 *
 * @return The parent entity, or null if there is no parent.
 */
fun Entity.getParent(): Entity? {
    return this.parent
}

/**
 * Recursively collects all elements in the hierarchy rooted at this entity.
 * The elements are added to a list in depth-first order.
 *
 * @return A list containing all elements in the hierarchy.
 */
fun Entity.deepListFiles(): List<Element> {
    val list = mutableListOf<Element>()

    this.accept {
        list.add(it)
        true
    }

    return list
}

/**
* Recursively searches for entities with the specified name in the hierarchy rooted at this entity.
* Entities matching the name are added to a list that will be returned.
*
* @param name The name of the entities to search for.
* @return A list containing all entities with the specified name found in the hierarchy.
*/
fun Entity.findEntities(name: String): List<Entity> {
    val list = mutableListOf<Entity>()
    if (this.name == name)
        list.add(this)
    children.forEach {
        if (it is Entity)
            list.addAll(it.findEntities(name))
    }

    return list
}

/**
 * Renames the entity to the specified new name after validation.
 *
 * @param newName The new name to assign to the entity.
 * @throws InvalidNameException If the provided new name is invalid.
 */
fun Entity.rename(newName: String) {
    validateName(newName)
    this.name = newName
}