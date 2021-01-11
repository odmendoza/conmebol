// Import the Slick interface for H2:

import slick.jdbc.H2Profile.api._

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object Conmebol extends App {

  case class Posicion(posicion: Byte,
                      pais: String,
                      puntos: Byte,
                      partidos_jugados: Byte,
                      partidos_ganados: Byte,
                      partidos_empatados: Byte,
                      partidos_perdidos: Byte,
                      diferencia_gol: Byte)

  class PosicionTable(tag: Tag) extends Table[Posicion](tag, "posiciones") {

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

  // Data from https://www.conmebol.com/es/eliminatorias-sudamericanas-catar-2022
  val brasil = Posicion(1, "Brasil", 12, 4, 4, 0, 0, 10)
  val argentina = Posicion(2, "Argentina", 10, 4, 3, 1, 0, 4)
  val ecuador = Posicion(3, "Ecuador", 9, 4, 3, 0, 1, 7)
  val paraguay = Posicion(4, "Paraguay", 6, 4, 1, 3, 0, 1)
  val uruguay = Posicion(5, "Uruguay", 6, 4, 2, 0, 2, 0)
  val chile = Posicion(6, "Chile", 4, 4, 1, 1, 2, 0)
  val colombia = Posicion(7, "Colombia", 4, 4, 1, 1, 2, -5)
  val venezuela = Posicion(8, "Venezuela", 3, 4, 1, 0, 3, -4)
  val peru = Posicion(9, "Perú", 1, 4, 0, 1, 3, -6)
  val bolivia = Posicion(10, "Perú", 1, 4, 0, 1, 3, -7)

  def posicionesConmebol = Seq(
    brasil,
    argentina,
    ecuador,
    paraguay,
    uruguay,
    chile,
    colombia,
    venezuela,
    peru,
    bolivia
  )

  lazy val posiciones = TableQuery[PosicionTable]

  val posicionEcuador = posiciones.filter(_.pais === "Ecuador")

  val tresPrimeros =
    for (pais <- posiciones if pais.posicion <= 3.toByte)
    yield pais

  val db = Database.forConfig("conmebol")

  def execute[T](action: DBIO[T]): T = {
    Await.result(db.run(action), 2.second)
  }

  // Creamos la tabla 'posicion' en la base de datos
  val createSchema = posiciones.schema.create
  // val future: Future[T] = db.run(createSchema)
  execute(createSchema)
  println("Database created")

  // Añadimos filas a la tabla
  execute(posiciones ++= posicionesConmebol)
  println("First positions added")

  // Consulta a la base de datos, todos los registros de la tabla
  println("Print all positions in database")
  execute(posiciones.result) foreach (println)

  // Consulta a la base de datos, para obtener la fila de Ecuador
  println("Print Ecuador position")
  execute(posicionEcuador.result) foreach (println)

  // Consulta a la base de datos, para obtener las tres primeras posiciones
  println("Print first three positions")
  execute(tresPrimeros.result) foreach (println)

  /*
  val actions: DBIO[Seq[Posicion]] = (
    posiciones.schema.create andThen
      (posiciones ++= posicionesConmebol) andThen
      posicionEcuador.result
    )
  execute(actions)
  */
}