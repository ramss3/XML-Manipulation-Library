/**
 * Validates the provided name to ensure it is not blank and does not contain special characters.
 *
 * @param name The name to validate.
 * @throws InvalidNameException if the name is blank or contains special characters.
 */
fun validateName(name: String) {
    if(name.isBlank() || specialCharactersRegex.containsMatchIn(name)) {
        throw InvalidNameException("Please introduce a valid name for the attribute.")
    }
}

/**
 * Validates the provided value to ensure it is not blank and does not contain special characters.
 *
 * @param value The value to validate.
 * @throws InvalidNameException if the value is blank or contains special characters.
 */
fun validateValue(value: String) {
    if(value.isBlank() || specialCharactersRegex.containsMatchIn(value)) {
        throw InvalidNameException("Please introduce a valid value.")
    }
}

/**
 * Validates the provided version to ensure it follows the pattern of "major.minor".
 *
 * @param version The version to validate.
 * @throws IllegalArgumentException if the version format is invalid.
 */
fun validateVersion(version: String) {
    require(version.matches(Regex("^\\d+\\.\\d+$"))) { "Invalid version format" }
}

/**
 * Validates the provided encoding to ensure it is one of the accepted encoding values.
 *
 * @param encoding The encoding to validate.
 * @throws IllegalArgumentException if the encoding is not one of the accepted values.
 */
fun validateEncoding(encoding: String) {
    val validEncodings = setOf("UTF-8", "UTF-16", "ISO-8859-1", "US-ASCII")
    require(encoding in validEncodings) { "Invalid encoding" }
}