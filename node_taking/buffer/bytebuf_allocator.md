ByteBufAllocator:I
  Define interface to allocate different ByteBuf.
  There is a DEFAULT ByteBufferAllocator define in this class.
  And the implementation of it is in ByteBufUtil.

ByteBufUtil:C
  static block that get system property.

EmptyByteBuf:C
  use as empty case. I could be singleton

ByteBufAllocatorMetric:I
  key unimplement method:
    1. usedHeapMemory
    2. usedDirectMemory

AbstractByteBufAllocator:A
  Key const:
    1. DEFAULT_INITIAL_CAPACITY = 256
    2. DEFAULT_MAX_CAPACITY = Integer.MAX_VALUE
    3. DEFAULT_MAC_COMPONENTS = 16
    4. CALCULATE_THREADHOLD = 4mb
  Key constructor:
    1. AbstractByteBufAllocator(boolean preferDirect)
      user preferDirect to disign create method direcBuffer or heapBuffer
  Static Method:
    1.toLeakAwareBuffer
      Wrap ByteBuffer with Simple or Advance LeakAwareBytebuf
  Key Method:
    1. calculateNewCapacity:
      calculate new capacity base on maxCapacity and threshold
    2. compositeHeapBuffer and compositeDirectBuffer.
      There are all created from CompositeBuf with different inputs.
  Key static Method:
    1. toLeakAwareBuffer
      ResourceLeakDetector invoke from this method.
  Key unImplemented Method:
    1. newHeapBuffer
    2. newDirectBuffer

UnpooledByteBufAllocator:C
  create ByteBuf + wrapped into LeakAwareByteBuffer
  key implementation:
    1. newHeapBuffer
      create HeapByteBuf from InstrumentedUnpooled
    2. newDirectBuffer
      create DirectByteBuf from InstrumentedUnpooled
  key next class:
    1. InstrumentedUnpooledUnsafeHeapByteBuf
    2. InstrumentedUnpooledHeapByteBuf
    3. InstrumentedUnpooledUnsafeNoCleanerDirectByteBuf
    4. InstrumentedUnpooledUnsafeDirectByteBuf
    5. InstrumentedUnpooledDirectByteBuf
    6. UnpooledByteBufAllocatorMetric
    *** allocateArray and freeArray are very important
    *** directByteBuf -> allocateDirect; heapByteBuf -> allocateArray
