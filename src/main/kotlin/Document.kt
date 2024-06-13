/**
 * Represents an XML document with a root entity.
 *
 * @property name The name of the document.
 * @property root The root entity of the document.
 * @property version The XML version (default is "1.0").
 * @property encoding The encoding of the document (default is "UTF-8").
 */

/**
 * Represents a document with metadata and a root entity.
 *
 * @property name The name of the document.
 * @property root The root entity of the document hierarchy.
 * @property version The version of the document. Defaults to "1.0".
 * @property encoding The encoding of the document content. Defaults to "UTF-8".
 * @throws InvalidNameException If the provided document name is invalid.
 * @throws InvalidVersionException If the provided document version is invalid.
 * @throws InvalidEncodingException If the provided document encoding is invalid.
 */
data class Document(
    val name: String,
    val root: Entity,
    val version: String = "1.0",
    val encoding: String = "UTF-8"
) {
    init {
        validateName(name)
        validateVersion(version)
        validateEncoding(encoding)
    }
}

/**
 * Converts the document to a text representation with XML declaration.
 *
 * @return A string representing the XML document.
 */
fun Document.toText(): String {
    return "<?xml version=\"$version\" encoding=\"$encoding\"?>\n" + root.prettyPrint()
}

/**
 * Adds an attribute to all entities with the specified name in the document's root hierarchy.
 *
 * @param entityName The name of the entities to which the attribute will be added.
 * @param attributeName The name of the attribute to add.
 * @param attributeValue The value of the attribute to add.
 * @throws NotFoundException If no entities with the specified name are found in the document's root hierarchy.
 */
fun Document.addAttribute(entityName: String, attributeName: String, attributeValue: String) {
    val entities = root.findEntities(entityName)
    if(entities.isEmpty())
        throw NotFoundException("$entityName entity not found.")
    entities.forEach {
        it.addAttribute(attributeName, attributeValue)
    }
}

/**
 * Renames all entities with the specified old name to the new name.
 *
 * @param oldName The current name of the entities.
 * @param newName The new name for the entities.
 * @throws NotFoundException If no entities with the specified old name are found in the document's root hierarchy.
 */
fun Document.renameEntity(oldName: String, newName: String) {
        val entities = root.findEntities(oldName)
        if(entities.isEmpty())
            throw NotFoundException("$oldName entity not found.")
        entities.forEach {
            it.rename(newName)
        }
}

/**
 * Renames an attribute in all entities with the specified name.
 *
 * @param entityName The name of the entities whose attribute will be renamed.
 * @param oldAttributeName The current name of the attribute to rename.
 * @param newAttributeName The new name to assign to the attribute.
 * @throws NotFoundException If no entities with the specified name are found.
 */
fun Document.renameAttribute(entityName: String, oldAttributeName: String, newAttributeName: String) {
    val entities = root.findEntities(entityName)
    if(entities.isEmpty())
        throw NotFoundException("$entityName entity not found.")
    entities.forEach {
        it.renameAttribute(oldAttributeName, newAttributeName)
    }
}

/**
 * Removes all entities with the specified name from the document.
 *
 * @param entityName The name of the entities to remove.
 * @throws NotFoundException If no entities with the specified name are found in the document's root hierarchy.
 */
fun Document.removeEntity(entityName: String) {
    val entities = root.findEntities(entityName)
    if(entities.isEmpty())
        throw NotFoundException("$entityName entity not found.")
    entities.forEach {
        it.remove()
    }
}

/**
 * Removes an attribute in all entities with a specified name in the document's root hierarchy.
 *
 * @param entityName The name of the entities from which the attribute will be removed.
 * @param attributeName The name of the attribute to remove.
 * @throws NotFoundException If no entities with the specified name are found in the document's root hierarchy.
 */
fun Document.removeAttribute(entityName: String, attributeName: String) {
    val entities = root.findEntities(entityName)
    if(entities.isEmpty())
        throw NotFoundException("$entityName entity not found.")
    entities.forEach {
        it.removeAttribute(attributeName)
    }
}

/**
 * Executes an XPath-like query on the document's root hierarchy and returns a
 * formatted string representation of matching entities.
 *
 * @param path The XPath-like path to query entities in the document.
 * @return A formatted string representation of entities matching the XPath query.
 */
fun Document.XPath(path: String): String {
    val paths = path.split('/').toMutableList()
    val entities = root.findEntities(paths[0])
    paths.removeFirst()

    fun iterate(paths: List<String>, entities: List<Entity>): String {
        var result = ""
        val newEntities = mutableListOf<Entity>()
        entities.forEach {
            newEntities.addAll(it.findEntities(paths[0]))
        }
        paths.removeFirst()
        if (paths.isNotEmpty()) {
            newEntities.forEach {
                result += iterate(paths, listOf(it))
            }
        } else {
            newEntities.forEach {
                result += it.prettyPrint() + "\n"
            }
        }
        return result
    }
    return iterate(paths, entities)
}