// Import the Slick interface for H2:

import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, duration}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object Comebol extends App {

  case class Posicion(posicion: Byte,
                      pais: String,
                      puntos: Byte,
                      partidos_jugados: Byte,
                      partidos_ganados: Byte,
                      partidos_empatados: Byte,
                      partidos_perdidos: Byte,
                      diferencia_gol: Byte)

  class ComebolTable(tag: Tag) extends Table[Posicion](tag, "pocision") {

    def posicion = column[Byte]("posicion", O.PrimaryKey)

    def pais = column[String]("pais")

    def puntos = column[Byte]("puntos")

    def partidos_jugados = column[Byte]("partidos_jugados")

    def partidos_ganados = column[Byte]("partidos_ganados")

    def partidos_empatados = column[Byte]("partidos_empatados")

    def partidos_perdidos = column[Byte]("partidos_perdidos")

    def diferencia_gol = column[Byte]("diferencia_gol")

    def * = (posicion,
      pais,
      puntos,
      partidos_jugados,
      partidos_ganados,
      partidos_perdidos,
      partidos_empatados,
      diferencia_gol).mapTo[Posicion]

  }

  def posicionesPrimeras = Seq(
    Posicion(1, "Brasil", 12, 4, 4, 0, 0, 10),
    Posicion(2, "Argentina", 10, 4, 3, 1, 0, 4),
    Posicion(3, "Ecuador", 9, 4, 3, 0, 1, 7)
  )

  lazy val posiciones = TableQuery[ComebolTable]

  val posicionEcuador = posiciones.filter(_.pais === "Ecuador")

  val db = Database.forConfig("comebol")

  def exec[T](program: DBIO[T]) : T = Await.result(db.run(program), 2.second)

  // Creamos la tabla 'posicion' en la base de datos
  exec(posiciones.schema.create)
  println("Database created")

  // Añadimos filas a la tabla
  exec(posiciones ++= posicionesPrimeras)
  println("First positions added")

  // Consulta a la base de datos, todos los registros de la tabla
  println("Print all positions in database")
  exec(posiciones.result) foreach(println)

  // Consulta a la base de datos, para obtener la fila de Ecuador
  println("Print Ecuador position")
  exec(posicionEcuador.result) foreach(println)

}