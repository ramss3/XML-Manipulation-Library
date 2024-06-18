/**
 * Regex pattern that matches strings that consist entirely of lowercase letters from 'a' to 'z'
 */
val specialCharactersRegex = Regex("^[a-z]+$")

/**
 * A regular expression to match special characters that are not letters, digits, spaces, dots, or commas.
 */
val specialCharactersRegexForDocument = Regex("[^\\p{L}0-9 .,]")

/**
 * Exception thrown when a resource or entity is not found.
 * @param message Error message describing the issue.
 */
class NotFoundException(message: String) : Exception(message)

/**
 * Exception thrown when attempting to rename or change entities with the same name.
 *
 * @param message Error message describing the issue.
 */
class EntitiesWithTheSameNameException(message: String) : Exception(message)

/**
 * Exception thrown when an invalid name is encountered.
 * @param message Error message describing the issue.
 */
class InvalidNameException(message: String) : Exception(message)

/**
 * Exception thrown when an attribute already exists.
 * @param message Error message describing the issue.
 */
class AttributeAlreadyExistsException(message: String) : Exception(message)