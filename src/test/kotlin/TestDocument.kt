
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows


class TestDocument {
    val plano = Entity("plano")
    val curso = Entity("curso", plano)
    val fuc = Entity("fuc", plano)
    val nome = Entity("nome", fuc)
    val ects = Entity("ects", fuc)
    val avaliacao = Entity("avaliacao", fuc)
    val componente = Entity("componente", avaliacao)
    val componente1 = Entity("componente", avaliacao)
    val fuc1 = Entity("fuc", plano)
    val nome1 = Entity("nome", fuc1)
    val ects1 = Entity("ects", fuc1)
    val avaliacao1 = Entity("avaliacao", fuc1)
    val componente2 = Entity("componente", avaliacao1)
    val componente3 = Entity("componente", avaliacao1)
    val componente4 = Entity("componente", avaliacao1)

    @Test
    fun testAddAttributeGlobal() {
        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)

        val document = Document("MyDocument", diretorio1)

        val document2 = Document("FailedDocument", diretorio2)

        document.addAttribute("avaliacao" ,"name", "Mestrado em Engenharia Informática")

        assertEquals(mutableMapOf("name" to "Mestrado em Engenharia Informática"), diretorio2.attributes)

        val exception = assertThrows<NotFoundException> {
            document2.addAttribute("NonExistingEntity", "name", "nota")
        }
        assertEquals("NonExistingEntity entity not found.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            document.addAttribute("avaliacao", "na#ds", "nota")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            document.addAttribute("avaliacao", "", "nota")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception2.message)

        val exception3 = assertThrows<InvalidNameException> {
            document.addAttribute("avaliacao", "name", "#ds")
        }
        assertEquals("Please introduce a valid value.", exception3.message)

        val exception4 = assertThrows<InvalidNameException> {
            document.addAttribute("avaliacao", "name", "")
        }
        assertEquals("Please introduce a valid value.", exception4.message)



    }

    @Test
    fun testRenameEntity() {
        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)

        val document = Document("MeuDocumento", diretorio1)
        document.renameEntity("avaliacao", "testes")

        assertEquals("testes", diretorio2.name)

        val exception = assertThrows<NotFoundException> {
            document.renameEntity("NonExistingEntity","testes")
        }
        assertEquals("NonExistingEntity entity not found.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            document.renameEntity("fuc", "!djf")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            document.renameEntity("fuc", "")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception2.message)
    }

    @Test
    fun testRenameAttributeGlobal() {

        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)

        val document = Document("MyDocument", diretorio1)

        val document2 = Document("FailsDocument", diretorio2)

        document.addAttribute("avaliacao" ,"name", "Mestrado em Engenharia Informática")
        document.addAttribute("avaliacao", "ects", "6.0")
        document.renameAttribute("avaliacao", "name", "nome")
        document.renameAttribute("avaliacao", "ects", "nota")

        assertEquals(
            mutableMapOf("nome" to "Mestrado em Engenharia Informática", "nota" to "6.0"),
            diretorio2.attributes
        )

        val exception = assertThrows<NotFoundException> {
            document2.renameAttribute("NonExistingEntity", "name", "nota")
        }
        assertEquals("NonExistingEntity entity not found.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            document.renameAttribute("fuc", "!djf", "nota")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            document.renameAttribute("fuc", "", "nota")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception2.message)

        val exception3 = assertThrows<InvalidNameException> {
            document.renameAttribute("avaliacao", "name", "#ds")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception3.message)

        val exception4 = assertThrows<InvalidNameException> {
            document.renameAttribute("avaliacao", "name", "")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception4.message)
    }

    @Test
    fun testRemoveEntity() {
        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)

        val document = Document("MeuDocumento", diretorio1)
        document.removeEntity("avaliacao")

        assertTrue(document.root.findEntities("avaliacao").isEmpty())

        val exception1 = assertThrows<NotFoundException> {
            document.removeEntity("NonExistingEntity")
        }
        assertEquals("NonExistingEntity entity not found.", exception1.message)
    }

    @Test
    fun testRemoveAttributeGlobal() {

        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)

        val document = Document("MyDocument", diretorio1)

        document.addAttribute("avaliacao" ,"name", "Mestrado em Engenharia Informática")
        document.addAttribute("avaliacao", "ects", "6.0")
        document.removeAttribute("avaliacao", "name")

        assertEquals(null, diretorio2.attributes["name"])
        assertEquals(mutableMapOf("ects" to "6.0"), diretorio2.attributes)

        val exception = assertThrows<NotFoundException> {
            document.removeAttribute("NonExistingEntity", "name")
        }
        assertEquals("NonExistingEntity entity not found.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            document.removeAttribute("avaliacao", "!djffdf")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            document.removeAttribute("avaliacao", "")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception2.message)
    }

    @Test
    fun testXPath() {

        val diretorio0 = Entity("plano")
        val diretorio1 = Entity("fuc", diretorio0)
        val diretorio2 = Entity("avaliacao", diretorio1)
        val diretorio3 = Entity("componente", diretorio2)
        val diretorio4 = Entity("componente", diretorio2)
        val document = Document("MyDocument", diretorio0)
        document.addAttribute("componente" ,"name", "Mestrado em Engenharia Informática")
        document.addAttribute("componente", "ects", "6.0")


        assertEquals(
            "<componente name=\"Mestrado em Engenharia Informática\" ects=\"6.0\"/>" + "\n" +
                    "<componente name=\"Mestrado em Engenharia Informática\" ects=\"6.0\"/>" + "\n",
            document.XPath("fuc/avaliacao/componente")
        )

    }

    @Test
    fun testToStringDocument() {
        Text("Mestrado em Engenharia Informática", curso)
        fuc.addAttribute("codigo", "M4310")
        Text("Programação Avançada", nome)
        Text("6.0", ects)
        componente.addAttribute("nome", "Quizzes")
        componente.addAttribute("peso", "20%")
        componente1.addAttribute("nome", "Projeto")
        componente1.addAttribute("peso", "80%")
        fuc1.addAttribute("codigo", "03782")
        Text("Dissertação", nome1)
        Text("42.0", ects1)
        componente2.addAttribute("nome", "Dissertação")
        componente2.addAttribute("peso", "60%")
        componente3.addAttribute("nome", "Apresentação")
        componente3.addAttribute("peso", "20%")
        componente4.addAttribute("nome", "Discussão")
        componente4.addAttribute("peso", "20%")
        val document = Document("MyDocument", plano)

        val text = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "\t<fuc codigo=\"M4310\">\n" +
                "\t\t<nome>Programação Avançada</nome>\n" +
                "\t\t<ects>6.0</ects>\n" +
                "\t\t<avaliacao>\n" +
                "\t\t\t<componente nome=\"Quizzes\" peso=\"20%\"/>\n" +
                "\t\t\t<componente nome=\"Projeto\" peso=\"80%\"/>\n" +
                "\t\t</avaliacao>\n" +
                "\t</fuc>\n" +
                "\t<fuc codigo=\"03782\">\n" +
                "\t\t<nome>Dissertação</nome>\n" +
                "\t\t<ects>42.0</ects>\n" +
                "\t\t<avaliacao>\n" +
                "\t\t\t<componente nome=\"Dissertação\" peso=\"60%\"/>\n" +
                "\t\t\t<componente nome=\"Apresentação\" peso=\"20%\"/>\n" +
                "\t\t\t<componente nome=\"Discussão\" peso=\"20%\"/>\n" +
                "\t\t</avaliacao>\n" +
                "\t</fuc>\n" +
                "</plano>"

        assertEquals(text, document.toText())
    }
}