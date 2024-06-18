# XML Manipulation Library

# Description
The XML Manipulation Library provides a simple way to generate and manipulate XML documents. The purpose of creating this library was to take on the role of someone who is developing something for others to use. Therefore, the library offers some flexibility in its use to allow it to be applied in a wide range of situations.

# Installation
To install the XML Manipulation Library, clone the repository and install the necessary dependencies.
git clone https://github.com/ramss3/XML-Manipulation-Library.git
cd XML-Manipulation-Library

* * *

# Features
- Add and Remove entities
- Add, Remove and Change attributes in entities
- Access the parent entity and the children entities of an entity
- Pretty Print in String format and writing to file
- Document scanning with visitor objects
- Add attributes globally to the document (providing the entity's name, and the attribute's name and value)
- Rename entities globally to the document (providing the entity's old and new name)
- Rename attributes globally to the document (providing the entity's name, and the attribute's old and new names)
- Remove entities globally to the document (providing the entity's name)
- Remove attributes globally to the document (providing the entity's name and attribute's name)
- Obtain XML fragments by scanning the document with simple XPath expressions
- Change identifier's names when translating to XML
- Determine how the object's attributes are translated (XML attribute or entity)
- Exclude attributes of objects
- Indicate a class that implements a transformation to be done to the default string
- Associate an adapter that makes free changes to the XML entity after automatic mapping
- DSL

* * *

# Usage
With the developed API we are able to manipulate XML documents in memory. For that we have three data classes (Document, Entity, Text and Attribute) wich are connected as follows:

![image](https://github.com/ramss3/XML-Manipulation-Library/assets/114668627/64b709f2-62a5-493f-8207-1280a9a0e1c6)

To create a Document object its always necessary to provide a name for it and an entity root. If the user doens't want the default version("1.0") or enconding("UTF-8"), it can also be provided.
```Kotlin
val root = Entity("fuc")
val document = Document("MyDocument", root)
```
To create an Entity object its always necessary to provide a name for it, but it can or cannot provide another Entity object to serve as a parent.
```Kotlin
val parent = Entity("fuc")
val child = Entity("avaliacao", parent)
```
To create a Text object its always necessary to provide an Entity object to serve as parent and the text to have in the object. The Text object cannot have children associated to it.
```Kotlin
val root = Entity("curso")
val text = Text("Mestrado em Engenharia Informática", root)
```

To create a class using the annotations its necessary to specify the class as an Entity with the `@XmlEntity` annotation, and the values as Attributes or Entities with the `@XmlAttribute` and `@XmlEntity` annotations, and give each one the name that you want them to take. If not, the values not specified will not be created.
```Kotlin
@XmlEntity("fuc")
data class FUC(
  @XmlAttribute("codigo") val code: String,
  @XmlEntity("nome") val name: String,
  @XmlEntity("ects") val ects: Double,
  @XmlEntity("observacoes") val observations: String,
  @XmlEntity("avaliacao") val evaluation: List<ComponenteAvaliacao>,
)
```
To create an object of that class, we simply use the class constructor.
```Kotlin
val f = FUC(
  "M4310", "Programação Avançada", 6.0, "la la...",
  listOf(
    ComponenteAvaliacao("Quizzes", "20"),
    ComponenteAvaliacao("Projeto", "80")
  )
)
```
```XML
<fuc codigo="M4310">
  <nome>Programação Avançada</nome>
  <ects>6.0</ects>
  <observacoes>la la...<observacoes>
  <avaliacao>
    <componente nome="Quizzes",  peso="20">
    <componente nome="Project",  peso="80">
  </avaliacao>
</fuc>
```
If you want to exclude a value of a class you just specify that value with the `@XmlExclude` annotation and the value will not be created.
```Kotlin
@XmlEntity("observacoes") @XmlExclude val observations: String
```
```XML
<fuc codigo="M4310">
  <nome>Programação Avançada</nome>
  <ects>6.0</ects>
  <avaliacao>
    <componente nome="Quizzes",  peso="20">
    <componente nome="Project",  peso="80">
  </avaliacao>
</fuc>
```
If you want to transform a string on a value first you need to create a class that will handle that transformation and extends the `StringTransformer` interface. After that, you simply specify the value that you want to be transformed with the `@XmlString` annotation and provide the name of the class created to it.
```Kotlin
class AddPercentage : StringTransformer {
  override fun transform(input: String): String {
    return "$input%"
  }
}
```
```Kotlin
@XmlEntity("componente")
data class ComponenteAvaliacao(
  @XmlAttribute("nome") val name: String,
  @XmlAttribute ("peso") @XmlString(AddPercentage::class) val weigth: String,
)
```
```XML
<fuc codigo="M4310">
  <nome>Programação Avançada</nome>
  <ects>6.0</ects>
  <observacoes>la la...<observacoes>
  <avaliacao>
    <componente nome="Quizzes",  peso="20%">
    <componente nome="Project",  peso="80%">
  </avaliacao>
</fuc>
```
If you want to associate a adapter that makes free changes to the Xml Entity, first you need to create a class that will handle that adapter and extends the `XmlCustomAdapter<>` interface. After that, you simply specify the class that you want the adapter to be used with the `@XmlAdapter` annotation and provide the name of the class created to it.
```Kotlin
class FUCAdapter : XmlCustomAdapter<FUCAdapted> {
  override fun adapt(entity: Entity, obj: FUCAdapted) {
    entity.children.sortBy { it.name }
  }
}
```
```Kotlin
@XmlAdapter(FUCAdapter::class)
@XmlEntity("fuc")
data class FUCAdapted(
  @XmlAttribute("codigo") val code: String,
  @XmlEntity("nome") val name: String,
  @XmlEntity("ects") val ects: Double,
  @XmlEntity("observacoes") @XmlExclude val observations: String,
  @XmlEntity("avaliacao") val evaluation: List<ComponenteAvaliacao>,
)
```
```XML
<fuc codigo="M4310">
  <avaliacao>
    <componente nome="Quizzes",  peso="20%">
    <componente nome="Project",  peso="80%">
  </avaliacao>
  <ects>6.0</ects>
  <nome>Programação Avançada</nome>
</fuc>
```

To create Entity and Text objects, to add attributes to the Entity objects, and to establish it's order using DSL we use the following lamba functions.
```Kotlin
val dir = entity("plano"){
  entity("curso"){
    text("Mestrado em Engenharia Informática")
  }
  entity("fuc"){
    attribute("codigo", "M4310")
    entity("nome"){
      text("Programação Avançada")
    }
    entity("ects"){
      text("6.0")
    }
    entity("avaliacao"){
      entity("componente"){
        attribute("nome", "Quizzes")
        attribute("peso", "20%")}
      entity("componente"){
        attribute("nome", "Projeto")
        attribute("peso", "80%")}
    }
  }
}
```
If we want to access a specific Entity or Text object we can use the following operator functions.
```Kotlin
val entityOperator : Entity = dir / "fuc" / "nome"
val textOperator : Text = (dir / "fuc" / "nome")["Programação Avançada"]
```
If we want to get a list of the Text objects associated to a Entity we can use the following inFix.
```Kotlin
dir / "fuc" / "nome" files
```

# Done by

Rafael Silva 98472

Tiago Felício 99213
