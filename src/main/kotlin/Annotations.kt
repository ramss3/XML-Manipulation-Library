import kotlin.reflect.KClass
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor

/**
 * Annotation for defining an XML entity with a specified name.
 *
 * @property name The name of the XML entity.
 */
@Target(AnnotationTarget.PROPERTY, AnnotationTarget.CLASS)
annotation class XmlEntity(
    val name: String
)

/**
 * Annotation for defining an XML attribute with a specified name.
 *
 * @property name The name of the XML attribute.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlAttribute(
    val name: String
)

/**
 * Annotation to exclude a property from XML serialization.
 * Properties marked with this annotation will not be included in XML output.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlExclude

/**
 * Annotation to specify a custom string transformer for XML serialization of a property.
 *
 * @property transformer The class of the string transformer used to serialize and deserialize the property.
 */
@Target(AnnotationTarget.PROPERTY)
annotation class XmlString(val transformer: KClass<out StringTransformer>)

/**
 * Annotation for using a custom adapter for XML serialization of a class.
 *
 * @property adapter The class of the XML adapter used to customize XML
 * serialization and deserialization for the annotated class.
 */
@Target(AnnotationTarget.CLASS)
annotation class XmlAdapter(val adapter: KClass<out XmlCustomAdapter<*>>)

/**
 * Interface for defining a string transformer.
 */
interface StringTransformer {
    /**
     * Transforms the input string according to specific rules or formats.
     *
     * @param input The input string to transform.
     * @return The transformed output string.
     */
    fun transform(input: String): String
}

/**
 * Interface for defining a custom XML adapter.
 *
 * @param T The type of the object being adapted.
 */
interface XmlCustomAdapter<T> {
    /**
     * Adapts the provided object [obj] of type [T] to XML representation within the context of the given [entity].
     *
     * @param entity The entity context for XML adaptation.
     * @param obj The object of type [T] to adapt to XML.
     */
    fun adapt(entity: Entity, obj: T)
}

/**
 * Object for converting objects to XML entities.
 */
object XmlConverter {

    /**
     * Converts an object to an XML entity.
     *
     * @param T The type of the object being converted.
     * @param obj The object to convert.
     * @return The root XML entity representing the object.
     */
    fun <T : Any> toXml(obj: T): Entity {
        val clazz = obj::class
        val rootName = clazz.findAnnotation<XmlEntity>()?.name?.takeIf { it.isNotEmpty() } ?: clazz.simpleName ?: "root"
        val root = Entity(rootName)
        objectToEntity(obj, root)
        return root
    }

    /**
     * Converts an object to an XML entity recursively.
     *
     * @param T The type of the object being converted.
     * @param obj The object to convert.
     * @param entity The current XML entity being constructed.
     */
    private fun <T : Any> objectToEntity(obj: T, entity: Entity) {
        val clazz = obj::class
        val params = clazz.primaryConstructor?.parameters ?: emptyList()
        val properties = clazz.memberProperties.associateBy { it.name }

        params.forEach { param ->
            val prop = properties[param.name] ?: return@forEach
            if (prop.findAnnotation<XmlExclude>() != null) return@forEach

            val value = try {
                prop.getter.call(obj)
            } catch (e: Exception) {
                return@forEach
            } ?: return@forEach

            val newValue = prop.findAnnotation<XmlString>()?.let { annotation ->
                val transformer = annotation.transformer.constructors.first().call()
                transformer.transform(value.toString())
            } ?: value.toString()

            when {
                prop.findAnnotation<XmlAttribute>() != null -> {
                    val attributeName = prop.findAnnotation<XmlAttribute>()?.name?.takeIf { it.isNotEmpty() } ?: prop.name
                    entity.addAttribute(attributeName, newValue)
                }
                prop.findAnnotation<XmlEntity>() != null -> {
                    val entityName = prop.findAnnotation<XmlEntity>()?.name?.takeIf { it.isNotEmpty() } ?: prop.name
                    val childEntity = Entity(entityName, entity)
                    objectToEntity(value, childEntity)
                    if (value is Collection<*>) {
                        value.forEach {
                            if (it != null) {
                                val itemClass = it::class
                                val itemEntityName = itemClass.findAnnotation<XmlEntity>()?.name?.takeIf { it.isNotEmpty() } ?: prop.name
                                val newEntity = Entity(itemEntityName, childEntity)
                                objectToEntity(it, newEntity)
                            }
                        }
                    } else {
                        Text(newValue, childEntity)
                    }
                }
            }
        }

        clazz.findAnnotation<XmlAdapter>()?.let { adapterAnnotation ->
            val adapter = adapterAnnotation.adapter.constructors.first().call()
            (adapter as XmlCustomAdapter<T>).adapt(entity, obj)
        }
    }
}