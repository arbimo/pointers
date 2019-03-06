package pointers

import java.lang.{Double => JDouble}

trait IMem {
  type M
  type Ptr[A] = pointers.Ptr[A, M]
  type Vec[A] = pointers.Vec[A, M]
}

/** Memory that can be read. */
trait RMem extends IMem {

  protected def read32(address: Int): Int

  private def read64(address: Int): Long = merge(read32(address), read32(address +1))

  final def readInt(p: Ptr[Int]): Int          = read32(p)
  final def readLong(p: Ptr[Long]): Long       = read64(p)
  final def readDouble(p: Ptr[Double]): Double = JDouble.longBitsToDouble(read64(p))
}
object RMem {
  type Aux[X] = RMem { type M = X }
}

/** Memory that can be written. */
trait WMem extends IMem {

  protected def write32(address: Int, value: Int): Unit

  private def write64(address: Int, value: Long): Unit = {
    write32(address, lower(value))
    write32(address + 1, upper(value))
  }

  final def writeInt(p: Ptr[Int], value: Int): Unit    = write32(p, value)
  final def writeLong(p: Ptr[Long], value: Long): Unit = write64(p, value)
  final def writeDouble(p: Ptr[Double], value: Double): Unit =
    write64(p, JDouble.doubleToLongBits(value))
}
object WMem {
  type Aux[X] = WMem { type M = X }
}

/** Memory that can be allocated. */
trait AMem extends IMem {

  protected def allocMem(size: Int): Int

  final def alloc[A](implicit sizeof: SizeOf[A]): Ptr[A] = allocMem(sizeof).asInstanceOf[Ptr[A]]
  final def alloc(struct: Struct): Ptr[struct.Self]      = alloc(struct.sizeof)

  final def allocVec[A](n: Int)(implicit sizeOf: SizeOf[A]): Vec[A] = {
    val first = allocMem(n * sizeOf)
    val last = first + (n-1) * sizeOf
    merge(first, last).asInstanceOf[Vec[A]]
  }
  final def allocVec(struct: Struct)(n: Int): Vec[struct.Self] = allocVec(n)(struct.sizeof)
}
object AMem {
  type Aux[X] = AMem { type M = X }
}

trait Mem extends RMem with WMem with AMem

object Mem {
  type Aux[X] = Mem { type M = X }
}

trait ReversibleMem extends IMem {
  type BacktrackPoint <: Int

  def save(): BacktrackPoint
  def restoreLast(): Unit
  def restore(bt: BacktrackPoint): Unit
}
