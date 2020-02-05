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

2. Create connection request
  Boss thread:
    1. listen to OP_ACCEPT event.
    2. create SocketChannel
    3. assign socketChannel to a NioEventLoop from worker group

  Worker thread:
    1. register socketChannel into selector of NioEventLoop
    2. register OP_READ


