import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class TestDSL {

    val plano = Entity("plano")
    val curso = Entity("curso", plano)
    val cursoText = Text("Mestrado em Engenharia Informática", curso)
    val fuc = Entity("fuc", plano)
    val nome = Entity("nome", fuc)
    val nomeText = Text("Programação Avançada", nome)
    val ects = Entity("ects", fuc)
    val ectsText = Text("6.0", ects)
    val avaliacao = Entity("avaliacao", fuc)
    val componente = Entity("componente", avaliacao)
    val componente1 = Entity("componente", avaliacao)
    val fuc1 = Entity("fuc", plano)
    val nome1 = Entity("nome", fuc1)
    val nome1Text = Text("Dissertação", nome1)
    val ects1 = Entity("ects", fuc1)
    val ects1Text = Text("42.0", ects1)
    val avaliacao1 = Entity("avaliacao", fuc1)
    val componente2 = Entity("componente", avaliacao1)
    val componente3 = Entity("componente", avaliacao1)
    val componente4 = Entity("componente", avaliacao1)

    val dir = entity("plano"){
        entity("curso"){
            text("Mestrado em Engenharia Informática")
        }
        entity("fuc"){
            entity("nome"){
                text("Programação Avançada")
            }
            entity("ects"){
                text("6.0")
            }
            entity("avaliacao"){
                entity("componente"){}
                entity("componente"){}
            }
        }
        entity("fuc"){
            entity("nome"){
                text("Dissertação")
            }
            entity("ects"){
                text("42.0")
            }
            entity("avaliacao"){
                entity("componente"){}
                entity("componente"){}
                entity("componente"){}
            }
        }
    }

    @Test
    fun testLambdaFunc() {
        assertEquals("<plano>\n" +
                "\t<curso>Mestrado em Engenharia Informática</curso>\n" +
                "\t<fuc>\n" +
                "\t\t<nome>Programação Avançada</nome>\n" +
                "\t\t<ects>6.0</ects>\n" +
                "\t\t<avaliacao>\n" +
                "\t\t\t<componente/>\n" +
                "\t\t\t<componente/>\n" +
                "\t\t</avaliacao>\n" +
                "\t</fuc>\n" +
                "\t<fuc>\n" +
                "\t\t<nome>Dissertação</nome>\n" +
                "\t\t<ects>42.0</ects>\n" +
                "\t\t<avaliacao>\n" +
                "\t\t\t<componente/>\n" +
                "\t\t\t<componente/>\n" +
                "\t\t\t<componente/>\n" +
                "\t\t</avaliacao>\n" +
                "\t</fuc>\n" +
                "</plano>", dir.prettyPrint())
    }

    @Test
    fun testOperators() {
        val nomeOperator : Entity = dir / "fuc" / "nome"
        val textOperator : Text = (dir / "fuc" / "nome")["Programação Avançada"]
        assertEquals(nome, nomeOperator)
        assertEquals(nomeText, textOperator)
    }

    @Test
    fun testInfix() {
        dir / "fuc" / "nome" files{ assertEquals(nomeText, it) }
    }
}