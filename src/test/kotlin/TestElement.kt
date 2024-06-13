import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

class TestElement {
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
    fun testDepth() {
        assertEquals(1, plano.depth)
        assertEquals(2, curso.depth)
        assertEquals(2, fuc.depth)
        assertEquals(3, avaliacao.depth)
        assertEquals(4, componente.depth)
    }

    @Test
    fun testToText() {
        assertEquals("/plano", plano.path)
        assertEquals("/plano/curso", curso.path)
        assertEquals("/plano/fuc", fuc.path)
        assertEquals("/plano/fuc/avaliacao", avaliacao.path)
        assertEquals("/plano/fuc/avaliacao/componente", componente.path)
    }

    @Test
    fun testAddAttribute() {
        curso.addAttribute("name", "Mestrado em Engenharia Informática")
        assertEquals(mutableMapOf("name" to "Mestrado em Engenharia Informática"), curso.attributes)

        val exception = assertThrows<InvalidNameException> {
            curso.addAttribute("na#ds", "Mestrado em Engenharia Informática")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            curso.addAttribute("", "Mestrado em Engenharia Informática")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            curso.addAttribute("name", "!&dsjhd")
        }
        assertEquals("Please introduce a valid value.", exception2.message)

        val exception3 = assertThrows<InvalidNameException> {
            curso.addAttribute("name", "")
        }
        assertEquals("Please introduce a valid value.", exception3.message)
    }

    @Test
    fun testRemoveAttribute() {
        curso.addAttribute("name", "Mestrado em Engenharia Informática")
        curso.addAttribute("ects", "6.0")
        curso.removeAttribute("name")

        assertEquals(mutableMapOf("ects" to "6.0"), curso.attributes)

        val exception = assertThrows<NotFoundException> {
            curso.removeAttribute("avaliação")
        }
        assertEquals("avaliação entity not found.", exception.message)

    }

    @Test
    fun testChangeAttribute() {
        curso.addAttribute("name", "Mestrado em Engenharia Informática")
        curso.changeAttribute("name", "Mestrado")
        assertEquals("Mestrado", curso.attributes["name"])

        val exception = assertThrows<NotFoundException> {
            curso.changeAttribute("nome", "Mestrado")
        }
        assertEquals("nome entity not found.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            curso.changeAttribute("name","na#ds")
        }
        assertEquals("Please introduce a valid value.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            curso.changeAttribute("name","")
        }
        assertEquals("Please introduce a valid value.", exception2.message)
    }

    @Test
    fun testRenameAttribute() {
        curso.addAttribute("name", "Mestrado em Engenharia Informática")
        curso.renameAttribute("name", "nome")
        assertEquals(mutableMapOf("nome" to "Mestrado em Engenharia Informática"), curso.attributes)


        val exception = assertThrows<NotFoundException> {
            curso.renameAttribute("nme", "Mestrado")
        }
        assertEquals("nme entity not found.", exception.message)


        val exception1 = assertThrows<InvalidNameException> {
            curso.renameAttribute("name","na#ds")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)

        val exception2 = assertThrows<InvalidNameException> {
            curso.renameAttribute("name","")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception2.message)

        val exception3 = assertThrows<NotFoundException> {
            curso.renameAttribute("nome","nome")
        }
        assertEquals("New name must be different from the old one.", exception3.message)
    }

    @Test
    fun testGetParent() {
        assertEquals(plano, curso.getParent())
    }

    @Test
    fun testGetChildren() {
        assertEquals(listOf(componente, componente1), avaliacao.getChildren())
    }

    @Test
    fun testPrettyPrint() {
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

        val text = "<plano>\n" +
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

        assertEquals(text, plano.prettyPrint())
    }

    @Test
    fun testRemove() {
        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)

        diretorio2.remove()

        assertEquals(0, diretorio1.children.size)
        assertTrue(diretorio1.findEntities("avaliacao").isEmpty())

    }

    @Test
    fun testDeepListFiles() {
        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)
        val diretorio3 = Entity("avaliacao", diretorio2)

        assertEquals( mutableListOf(diretorio1, diretorio2, diretorio3),diretorio1.deepListFiles())
        assertEquals( mutableListOf(diretorio2, diretorio3),diretorio2.deepListFiles())
        assertEquals( mutableListOf(diretorio3),diretorio3.deepListFiles())

    }

    @Test
    fun testFindDirectories() {
        val diretorio1 = Entity("fuc")
        val diretorio2 = Entity("avaliacao", diretorio1)
        val diretorio3 = Entity("avaliacao", diretorio1)

        assertEquals( mutableListOf(diretorio2, diretorio3),diretorio1.findEntities("avaliacao"))

    }

    @Test
    fun testRename() {
        val diretorio1 = Entity("fuc")
        diretorio1.rename("programa")

        assertEquals("programa", diretorio1.name)

        val exception = assertThrows<InvalidNameException> {
            diretorio1.rename("na#ds")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception.message)

        val exception1 = assertThrows<InvalidNameException> {
            diretorio1.rename("")
        }
        assertEquals("Please introduce a valid name for the attribute.", exception1.message)
    }
}