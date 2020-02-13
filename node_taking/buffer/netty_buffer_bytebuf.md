ReferenceCounted:I
  Count the reference count of this object.
  Core methods:
    1. int refCnt();
      return the reference coung of this object
    2. ReferenceCounted retain()
      Increase the reference by 1
    3. ReferenceCounted touch()
      record the current ass location of this object for debuggin purposes.
    4. boolean release();
      decrease the reference count by 1

ByteBuf:I
  Core declared method:
    1. internalNioBuffer
      ? why interalNioBuffer need
        set and get method do not change index for underline JDK ByteBuffer,
        but read and write dose. So every read and write operation should 
        use same JDK buffer
    2. setBytes vs getBytes
      setBytes: read bytes from this ByteBuf to outputting source
      getBytes: read bytes from outputting source to this ByteBuf

AbstractByteBuf:A
  ResourceLeakDetector -> check memory leak
  Core methods:
    1. ensureAccessible
      check from reference see if this ByteBuf is GC
    2. ByteBuf capacity(int newCapacity)
      allow new bytebuffer with new capacity
    3. _getByte
       _getShort
       _getShortLE
       _getUnsignedMedium
       _getBytes
       _get***
      *** basic method of get byte from buf, which is being used by other get method.
      *** subClass will design underline data structure.
      *** get method do not move read index.

    3.1 getBytes
      *** the set of methods is important
      *** It can to read to GatheringByteChannel, FileChannel, and OutputStream

    4. _set***
      *** set value in specific index, do not move index
    4.1 setBytes
      *** entry of outputing this buffer to another ByteBuf, Channel, and InputStream

    5. _read***
      *** read value from readIndex, readIndex will increse after read.
      *** it used get*** method to get value after that increate readerIndex

