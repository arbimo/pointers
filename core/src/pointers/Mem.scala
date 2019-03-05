package pointers

import java.lang.{Double => JDouble}

trait IMem {
  type M
  type Ptr[A] = pointers.Ptr[A, M]
}

/** Memory that can be read. */
trait RMem extends IMem {

  protected def read32(address: Int): Int

  private def read64(address: Int): Long =
    read32(address).toLong | (read32(address + 1).toLong << 32)

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
    val a = value.toInt
    val b = (value >> 32).toInt
    write32(address, a)
    write32(address + 1, b)
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
}
object AMem {
  type Aux[X] = AMem { type M = X }
}

trait Mem extends RMem with WMem with AMem

object Mem {
  type Aux[X] = Mem { type M = X }
}
