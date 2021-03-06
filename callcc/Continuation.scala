//
// See http://blog.tmorris.net/continuation-monad-in-scala/
//

sealed trait Continuation[R, +A] {
  def apply(f: A => R): R

  import Continuation.continuation

  def map[B](k: A => B) =
    continuation[R, B](z => apply(z compose k))

  def flatMap[B](k: A => Continuation[R, B]) =
    continuation[R, B](z => apply(k(_)(z)))
}

object Continuation {
  def continuation[R, A](g: (A => R) => R) = new Continuation[R, A] {
    def apply(f: A => R) = g(f)
  }

  def unit[R] = new {
    def apply[A](a: A) = continuation[R, A](f => f(a))
  }

  def callcc[R, A, B](f: (A => Continuation[R, B]) => Continuation[R, A]) =
    continuation[R, A](k => f(a => continuation(x => k(a)))(k))
}
