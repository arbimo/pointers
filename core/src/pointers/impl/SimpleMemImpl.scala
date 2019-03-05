package pointers.impl

import pointers._

final class SimpleMemImpl extends Mem {

  private var impl = new Array[Int](32)
  private var top  = 0

  override protected def read32(address: Int): Int = impl(address)

  override protected def write32(address: Int, value: Int): Unit = impl(address) = value

  override protected def allocMem(size: Int): Int = {
    if(top + size < impl.length) {
      impl = java.util.Arrays.copyOf(impl, math.max(impl.length * 2, top + size))
    }
    val ptr = top
    top += size
    ptr
  }
}
