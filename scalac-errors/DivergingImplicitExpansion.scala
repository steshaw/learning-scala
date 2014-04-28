//
// What's a "diverging implicit expansion" scalac message mean?
//
// From http://stackoverflow.com/questions/7950014/whats-a-diverging-implicit-expansion-scalac-message-mean
// 
// $ scalac DivergingImplicitExpansion.scala 
// DivergingImplicitExpansion.scala:5: error: diverging implicit expansion for type scala.math.Ordering[Ordered[Int]]
// starting with method comparatorToOrdering in trait LowPriorityOrderingImplicits
//   println(o1 < o2)
//           ^
// DivergingImplicitExpansion.scala:5: error: type mismatch;
//  found   : Ordered[Int]
//  required: Int
//   println(o1 < o2)
//                ^
// two errors found
//
object DivergingImplicitExpansion {
  val o1: Ordered[Int] = 1
  val o2: Ordered[Int] = 2

  println(o1 < o2)
}
