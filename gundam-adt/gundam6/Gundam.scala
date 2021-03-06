class Boom(s: String) extends Exception(s)

sealed abstract class Direction
case object North extends Direction
case object East extends Direction
case object South extends Direction
case object West extends Direction

object Direction {
  def label(d: Direction) =
    d match {
      case North => "north"
      case East => "east"
      case South => "south"
      case West => "west"
    }
}

sealed trait Command
case class Face(dir: Direction) extends Command
case object Start extends Command
case object Stop extends Command
case class Chain(cmd1: Command, cmd2: Command) extends Command

sealed abstract class Status
case object Idle extends Status
case object Moving extends Status

case class State(
  path: List[Direction],
  dir: Direction,
  moving: Status
)

object Gundam {

  def try_it(f: => Unit): Unit = {
    try {
      f
    } catch {
      case e @ (_: Boom | _: MatchError) => println(e)
    }
  }

  def apply(cmd: Command, state: State): State = {
    val State(path, dir, moving) = state
    cmd match {
      case Face(dir) =>
        state.moving match {
          case Idle => State(path, dir, Idle)
          case Moving => throw new Boom(s"Trying to face ${dir} when moving!")
        }
      case Start =>
        state.moving match {
          case Idle => State(path :+ dir, dir, Moving)
          case Moving => throw new Boom("Trying to start while moving!")
        }
      case Stop =>
        state.moving match {
          case Idle => throw new Boom("Trying to stop while not moving!")
          case Moving => State(path, dir, Idle)
        }
      case Chain(cmd1, cmd2) => {
        apply(cmd2, apply(cmd1, state))
      }
    }
  }

  implicit class Compose(cmd1: Command) {
    def ~>(cmd2: Command): Command =
      Chain(cmd1, cmd2)
  }

  val start = Start
  val stop = Stop
  def face(dir: Direction) = Face(dir)

  val north = North
  val east = East
  val south = South
  val west = West

  val startStop = start ~> stop

  def move(d: Direction) =
    face(d) ~> start ~> stop

  val cmds1 =
    Chain(
      Chain(
        Chain(
          Face(East),
          Chain(
            Start,
            Stop
          )
        ),
        Face(West)
      ),
      Chain(
        Start,
        Stop
      )
    )

  val cmds2 = move(east) ~> move(west)

  val illegal1 = start ~> start
  val illegal2 = stop ~> stop
  val illegal3 = start ~> face(north)

  val defaultState =
    State(
      path = Nil,
      dir = North,
      moving = Idle
    )

  def go(): Unit = {
    val state0 = defaultState
    println(state0)
    val state1 = apply(Face(North), state0)
    println(state1)
    val state2 = apply(Face(West), state1)
    println(state2)
    val state3 = apply(Face(South), state2)
    println(state3)
    val state4 = apply(Face(East), state3)
    println(state4)
    val state5 = apply(Start, state4)
    println(state5)
    val state6 = apply(Stop, state5)
    println(state6)
  }

  def main(args: Array[String]): Unit = {
    println(Direction.label(West))
    // cmds1 and cmds2 seems equivalent but do so via
    // different nesting of Chains.
    assert(cmds1 != cmds2)
    println(cmds1)
    println(cmds2)
    val finalState1 = apply(cmds1, defaultState)
    println(finalState1)
    val expectedState =
      State(
        List(East, West),
        West,
        Idle
      )
    assert(finalState1 == expectedState)
    val finalState2 = apply(cmds2, defaultState)
    println(finalState2)
    assert(finalState2.path == List(East, West))
    // Final state of cmds1 and cmd2 are the same.
    assert(finalState1 == finalState2)
    go()
    try_it(apply(illegal1, defaultState))
    try_it(apply(illegal2, defaultState))
    try_it(apply(illegal3, defaultState))
  }
}
