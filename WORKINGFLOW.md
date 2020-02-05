1. Boot
  Main thread:
    1. create Selector
    2. create ServerSocketChannel
    3. initial ServerSocketChannel
    4. assign ServerSocketChannel to a NioEventGroup

  Bose thread:
    1. register ServerSocketChannel into JDK Selector in NioEventLoop
    2. bind IP address
    3. Register listening Event.

  Code flow:
    1. Selector selector = sun.nio.ch.SelectorProviderImpl.openSelector()
    2. ServerSocketChannel serverSocketChannel = provider.openServerSocketChannel()
    3. selectionKey = javaChannel().register(eventLoop().unwrappedSelector(), 0, this)
    4. javaChannel().bind(localAddress, config.getBackLog())
    5. selectionKey.interestOps(OP_ACCEPT)

  Tip:
    Binding OP_ACCEPT is from fireChannelActive()
    ServerBootstrapAcceptor is a ChannelInboundHandler to handle new connection.

2. Create connection request
  Boss thread:
    1. listen to OP_ACCEPT event.
    2. create SocketChannel
    3. assign socketChannel to a NioEventLoop from worker group

  Worker thread:
    1. register socketChannel into selector of NioEventLoop
    2. register OP_READ into JDK SelectionKey

  Tip:
    Function stack of registering OP_READ:
      ChannelPiple -> fileChannelActive -> AbstractUnsafe -> register0

    Channel is attached in SelectionKey

3. Receive data
  Tip:
    1. AdaptiveRecvByteBufAllcator:C
      This allocator has power to guess in comming message size.
    
    2. DefaultMaxMessagesPerRead:C
  
  Core flow:
    1. has OP_READ event.
    2. invoke NioSocketChannelUnsafe.read
    3. allocate initial byte buffer
    4. read bytes from channel
    5. record the size of incomming bytes that use to allocate the size of next buffer.
    6. invoke pipelin.fireChannelRead sending data to rest of ChannelHandlers

4. User application
  Core flow:
    1. Waiting for pipeline.fireChannelRead invoke.

  Entrance:
    AbstractNioByteChannel

  Use:
    implement ChannelInboundHandlerAdapter.channelRead method.

4. Output data.
  Three ways of writing back:
    1. write: write bytes into a buffer, do not send immidiately
    2. flush: send back bytes inside the buffer
    3. writeAndFlush: write to a buffer, and send immidiately

  TODO:
    1. read code

5. Close connection:
  Core flow:
    1. handl OP_READ event
    2. Check if bytes size < 0
    3. clean queue
    4. fireChannelInactive and fileChannelUnregistered

  TODO:
    1. read code

6. Close main server:
  Core flow:
    1. boss thread stop, then work group stop

  TODO:
    1. read code
