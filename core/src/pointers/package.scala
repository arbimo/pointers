import scala.language.higherKinds

package object pointers {

  /** A pointer to a value of type A, inside the memory M.
    * A valid pointer is represented by a positive. */
  type Ptr[A, M] <: Int

  /** A contiguous array of values of type A inside memory M.
    * Internally this is implemented by storing:
    *  - the pointer to the first element in the lower 32 bits
    *  - the pointer to the last element in the upper 32 bits.
    **/
  type Vec[A, M] <: Long

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

    /** Move the pointer forward by the size of pointed type.
      * This is equivalent to moving to the next element in an array
      * or (ptr + 1) in C's pointer arithmetic. */
    def unsafeNext(implicit sizeof: SizeOf[A]): Ptr[A, M] = unsafeAdd(1)
    def unsafePrev(implicit sizeof: SizeOf[A]): Ptr[A, M] = unsafeAdd(-1)

    def unsafeAdd(i: Int)(implicit sizeof: SizeOf[A]): Ptr[A, M] = (ptr + i * sizeof).asInstanceOf[Ptr[A,M]]
  }

  implicit class VecOps[A, M](private val vec: Vec[A, M]) extends AnyVal {
    def apply(i: Int)(implicit sizeof: SizeOf[A]): Ptr[A, M] = {
      val p = first.unsafeAdd(i)
      assert(p <= last, "Index out of bound")
      p
    }
    def isEmpty: Boolean = first < last
    def first: Ptr[A, M] = lower(vec).asInstanceOf[Ptr[A, M]]
    def last: Ptr[A, M] = upper(vec).asInstanceOf[Ptr[A, M]]
    def tail(implicit sizeof: SizeOf[A]): Vec[A, M] =
      merge(first + sizeof, last).asInstanceOf[Vec[A,M]]
    def length(implicit sizeof: SizeOf[A]): Int = (last - first) / sizeof + 1
  }



  private[pointers] def merge(lower: Int, upper: Int): Long = lower.toLong | (upper.toLong << 32)
  private[pointers] def lower(l: Long): Int = l.toInt
  private[pointers] def upper(l: Long): Int = (l >> 32).toInt

}
