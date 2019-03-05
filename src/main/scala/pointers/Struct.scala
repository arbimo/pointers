package pointers

abstract class Struct { self =>
  val sizeof: SizeOf[Self]
  type Self = this.type

  implicit val sizeOfInstance: SizeOf[Self] = sizeof.asInstanceOf[SizeOf[Self]]
}

/** Provides syntactic convenience to define Structs such as:
  *
  * object Point extends StructBuilder {
      val x = newField[Double]
      val y = newField[Double]
      override val sizeof: Int = computeSize()
    }
  */
abstract class StructBuilder extends Struct {

  private var top: Int = 0
  protected def newField[A: SizeOf]: FieldOffset[Self, A] = {
    val offset = top
    top += implicitly[SizeOf[A]]
    offset.asInstanceOf[FieldOffset[Self, A]]
  }

  /** Must be called inside the static initialization AFTER all newField calls*/
  protected def computeSize(): SizeOf[Self] = top.asInstanceOf[SizeOf[Self]]

}
