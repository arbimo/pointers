import scala.language.higherKinds

package object pointers {

  /** A pointer to a value of type A, inside the memory M. */
  type Ptr[A, M] <: Int

  /** Offset (number of words) of a field of type B inside a struct of type A. */
  type FieldOffset[A, B] <: Int

  /** Number of words occupied by type A. */
  type SizeOf[A] <: Int

  // size of primitive types
  implicit val sizeOfInt: SizeOf[Int]     = 1.asInstanceOf[SizeOf[Int]]
  implicit val sizeOfFloat: SizeOf[Float] = 1.asInstanceOf[SizeOf[Float]]

  implicit val sizeOfLong: SizeOf[Long]     = 2.asInstanceOf[SizeOf[Long]]
  implicit val sizeOfDouble: SizeOf[Double] = 2.asInstanceOf[SizeOf[Double]]

  implicit class PtrOps[A, M](private val ptr: Ptr[A, M]) extends AnyVal {
    def apply[B](fo: FieldOffset[A, B]): Ptr[B, M] = (ptr + fo).asInstanceOf[Ptr[B, M]]
  }

}
