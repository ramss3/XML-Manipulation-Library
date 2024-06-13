/**
 * Creates a new directory with the given [name] and applies the [build] function to it.
 *
 * @param name The name of the directory.
 * @param build The function to apply to the new directory.
 * @return The newly created [Entity] representing the directory.
 */
fun directory(name: String, build: Entity.() -> Unit) =
    Entity(name).apply {
        build(this)
    }

/**
 * Creates a new subdirectory within this [Entity] with the given [name] and applies the [build] function to it.
 *
 * @param name The name of the subdirectory.
 * @param build The function to apply to the new subdirectory.
 * @return The newly created [Entity] representing the subdirectory.
 */
fun Entity.directory(name: String, build: Entity.() -> Unit) =
    Entity(name, this).apply {
        build(this)
    }

/**
 * Creates a new text file with the given [name] within this [Entity].
 *
 * @param name The name of the text file.
 * @return The newly created [Text] file.
 */
fun Entity.text(name: String) = Text(name, this)

/**
 * Retrieves a child [Text] entity by its [name].
 *
 * @param name The name of the child [Text] entity.
 * @return The [Text] entity with the given [name].
 * @throws ClassCastException if the child entity is not of type [Text].
 */
operator fun Entity.get(name: String) =
    children.find { it.name == name } as Text

/**
 * Retrieves a child [Entity] by its [name].
 *
 * @param name The name of the child [Entity].
 * @return The [Entity] with the given [name].
 * @throws ClassCastException if the child entity is not of type [Entity].
 */
operator fun Entity.div(name: String): Entity =
    children.find { it.name == name } as Entity

/**
 * Retrieves the [Text] entities within this [Entity]
 *
 * @param action The action to apply to each [Text] entity.
 */
infix fun Entity.files(action: (Text) -> Unit) {
    children.filterIsInstance<Text>()
        .forEach { action(it) }
}
