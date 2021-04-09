class Boom(s: String) extends Exception(s)

object Direction {
  val North = 1
  val East = 2
  val South = 3
  val West = 4

  def label(d: Int) =
    d match {
      case Direction.North => "north"
      case Direction.East => "east"
      case Direction.South => "south"
      case Direction.West => "west"
    }
}

object Gundam {

  def face(a: Any): Unit = {
    a match {
      case a : Int => {
        if (a < 0) throw new Boom("negative!")
      }
      case _ => ()
    }
  }
  def start: Unit = {}
  def stop: Unit = {}
  def triple_backflip = throw new Boom("triple backflip")

  val north = Direction.North
  val east = Direction.East
  val south = Direction.South
  val west = Direction.West

  def try_it(f: => Unit): Unit = {
    try {
      f
    } catch {
      case e: Boom => println(e)
      case e: MatchError => println("didn't cover all cases :(")
    }
  }

  def go(): Unit = {
    face(north)
    face(west)
    face(south)
    face(east)
    start
    stop
    try_it(triple_backflip)
    try_it(face(-35))
  }

  def main(args: Array[String]): Unit = {
    import Direction._
    try_it(label(-35))
    go()
  }
}
