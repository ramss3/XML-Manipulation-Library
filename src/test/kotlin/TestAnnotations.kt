import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TestAnnotations {

    @XmlEntity("componente")
    data class ComponenteAvaliacao(
        @XmlAttribute("nome")
        val name: String,
        @XmlAttribute ("peso")
        @XmlString(AddPercentage::class)
        val weigth: String,
    )

    @XmlEntity("fuc")
    data class FUC(
        @XmlAttribute("codigo") val code: String,
        @XmlEntity("nome") val name: String,
        @XmlEntity("ects") val ects: Double,
        @XmlEntity("observacoes") @XmlExclude val observations: String,
        @XmlEntity("avaliacao") val evaluation: List<ComponenteAvaliacao>,
    )

    @XmlAdapter(FUCAdapter::class)
    @XmlEntity("fuc")
    data class FUCAdapted(
        @XmlAttribute("codigo") val code: String,
        @XmlEntity("nome") val name: String,
        @XmlEntity("ects") val ects: Double,
        @XmlEntity("observacoes") @XmlExclude val observations: String,
        @XmlEntity("avaliacao") val evaluation: List<ComponenteAvaliacao>,
    )

    class AddPercentage : StringTransformer {
        override fun transform(input: String): String {
            return "$input%"
        }
    }

    class FUCAdapter : XmlCustomAdapter<FUCAdapted> {
        override fun adapt(entity: Entity, obj: FUCAdapted) {
            entity.children.sortBy { it.name }
        }
    }

    @Test
    fun testChangeName() {
        val c = ComponenteAvaliacao("Quizzes", "20")
        val xmlEntity = XmlConverter.toXml(c)
        assertEquals("Quizzes", xmlEntity.attributes["nome"])
    }

    @Test
    fun testTranslation() {
        val f = FUC(
            "M4310", "Programação Avançada", 6.0, "la la...",
            listOf(
                ComponenteAvaliacao("Quizzes", "20"),
                ComponenteAvaliacao("Projeto", "80")
            )
        )

        val xmlEntity = XmlConverter.toXml(f)
        val prettyPrint = xmlEntity.prettyPrint()
        assertEquals("<fuc codigo=\"M4310\">\n" +
                "\t<nome>Programação Avançada</nome>\n" +
                "\t<ects>6.0</ects>\n" +
                "\t<avaliacao>\n" +
                "\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                "\t</avaliacao>\n" +
                "</fuc>", prettyPrint)
    }

    @Test
    fun testExclusion() {
        val f = FUC(
            "M4310", "Programação Avançada", 6.0, "la la...",
            listOf(
                ComponenteAvaliacao("Quizzes", "20"),
                ComponenteAvaliacao("Projeto", "80")
            )
        )

        val xmlEntity = XmlConverter.toXml(f)
        assertEquals("M4310", xmlEntity.attributes["codigo"])
        assertTrue(!xmlEntity.children.any { it.name == "observations" })
    }

    @Test
    fun testStringTransformer() {
        val c = ComponenteAvaliacao("Quizzes", "20")
        val xmlEntity = XmlConverter.toXml(c)
        assertEquals("20%", xmlEntity.attributes["peso"])
    }

    @Test
    fun testAdapter(){
        val f = FUCAdapted(
            "M4310", "Programação Avançada", 6.0, "la la...",
            listOf(
                ComponenteAvaliacao("Quizzes", "20"),
                ComponenteAvaliacao("Projeto", "80")
            )
        )

        val xmlEntity = XmlConverter.toXml(f)
        val childNames = xmlEntity.children.map { it.name }
        val expectedOrder = listOf("avaliacao", "ects", "nome")
        assertEquals(expectedOrder, childNames)
    }
}

