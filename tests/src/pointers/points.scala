package pointers

object DPoint extends StructBuilder {
  val x               = newField[Double]
  val y               = newField[Double]
  override val sizeof = computeSize()
}

object Point extends StructBuilder {

  val x = newField[Int]
  val y = newField[Int]
  val z = newField[Double]

  override val sizeof = computeSize()
}
