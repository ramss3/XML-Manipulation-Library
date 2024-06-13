/**
 * Regex pattern to match special characters that are not letters, digits, spaces, dots, or commas.
 * Used for general text validation.
 */
val specialCharactersRegex = Regex("[^\\p{L}0-9 .,]")

/**
 * Regex pattern to match special characters that are not letters, digits, spaces, dots, commas, or percent signs.
 * Used specifically for validating values of attributes.
 */
val specialCharactersRegexForValueAttribute = Regex("[^\\p{L}0-9 .,%]")

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