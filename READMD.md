1. How netty handle CHANNEL:
  Key classes:
    ChannelHandler
    ChannelInboundInvoker, ChannelOutboundInvoker
    ChannelHandlerContext -> { ChannelInboundInvoker, ChannelOutboundInvoker }
    ChannelPipeline -> { ChannelInboundInvoker, ChannelOutboundInvoker }
    ChannelInboundHandler -> { ChannelHandler }
    ChannelOutboundHandler -> { ChannelHandler }

  ChannelInboundHandler is arm for processing incomming bytes.B
  ChannelOutboundHandler is used to deal with outcomming bytes.

  ChannelInboundHandler implementations:
    ChannelInboundHandlerAdapter -> 
    { e:ChannelHandlerAdapter, i:ChannelInboundHandler } 
    This is where ChannelInboundHandler begin to implementation.
    ChannelInboundHandlerAdapter do nothing but calling
    channelHandlerContext."corresponding method"
    
    Must know ChannelInboundHanderAdapters:
      1. ByteToMessageDecoder
        convert incoming TCP bytes to Message.
      2. MessageToMessageDecoder
        decode message to different format of message
      3. ServerBootstrapAcceptor
        very important and netty loads it by default, which is used to invoke ChannelOptions that set by user.
        only deal with channel.
        ChannelOptions will eventually go to **ChannelConfig.

    ByteToMessageDecoder:
      This is a abstract class.
      Encapsulating bytes into Bytebuf and output ByteBuf.
      It is subClass responsible to implement how to convert ByteBuf.
      SubClass should also be response to handle sticky and half packet.
      Example subClass that handle above problem:
        1. FixedLengthFrameDecoder
        2. DelimiterBasedFrameDecoder
        3. lengthFiledBasedFrameDecoder
      It is responsible to call invoke ChannelHandlerContext then trigger following handlers.
        using ChannelHandlerContext.fileChannelRead method.


      key methods:
        channelRead:
          1. use Cumulator to cumulate bytes
          2. subClass need to implement decode method to do the actual decode.

2. How netty handle async and sync:
  Key class:
    Future<V> -> { java.util.concurrent.Future<V> }
    Promise<V>:I -> Future<V>:I
    !!! DefaultPromise<V>:C -> { AbstractFuture<V>:A, Promise<V>:I }
    AbstractFuture<V>:A -> { Future<V>:I }
    ChannelPromise:I -> { ChannelFuture:I, Promise<Void>:I }
    DefaultChannelPromise:C -> { DefaultPromise<Void>, ChannelPromise:I, FlushCheckpoint:I }
    ChannelFuture:I -> { Future<Void>:I }
    CompleteChannelFuture:A -> { CompleteFuture<Void>:C, ChannelFuture:I }
    GenericFutureListener<F extends Future<?>> -> { EventListener }
    DefaultFutureListeners:C
    FutureListener<V>:I -> { GenericFutureListener<Future<V>>:I }
    ChannelFutureListener:I -> { GenericFutureListener<ChannelFuture>:I }
    ChannelGroupFuture:I -> { Future<Void>:I, Iterable<ChannelFuture> }
    DefaultChannelGroupFuture:C -> { DefaultPromise<Void>:C, ChannelGroupFuture:I }
    ScheduledFuture<V>:I -> { Future<V>:I, java.util.concurrent.ScheduledFuture<V>:I }

  DefaultFutureListeners:C & GenericFutureListener<F extneds Future<?>> -> { java.util.EventListener }
  ! DefaultFutureListeners is a container for GenericFutureListener.
  ! DefaultFutureListeners provides add and remove to allow user to add and remove GenericFutureListener from listeners[]
  ! GenericFutureListener provides method called operationComplete to call back when channel is ready.

  Future<V>:I -> { java.util.concurrent.Future<V> }
    Key methods:
      1. addListener
      2. await
      3. sync
      4. getNow

  Promise<V>:I -> { Future<V>:I }
    special Future which is writable.
    Key methods:
      1. setSuccess
      2. setFailure
    key implementations:
      DefaultPromise<V>

  DefaultPromise<V>:C -> { Promise<V>:I }
    Tips:
      how it implement superClass method.
      It has EventExecutor which looks like relating to all notify methods.
      Read methods that are using eventExecutor
    Key methods:
      1. setValue0
        This method uses CAS. If set successes, return true. Otherwise, false.
        If set success, notify all listeners. ( set success === isDoen() == true )
      2. notifyListenersNow()
        do some secure checks then call notifyListeners0 and notifyListener0
        ! Very interesting way to make sure threadsafe. Check out if interesting
        ! It also remove all listener when it done.
      3. notifyListeners0
        Iterate listeners array, for each listener call notifyListener0
      4. notifyListener0
        call back operationComplete
      5. await & await0
        check deadlock then call java object.wait method
        wait until isDone() == true
      6. addListerner0
        Create DefaultFutureListeners to store listener.
      7. get
        It override get method from AbstractFuture.
        This method will wait untill it is done,
        so it is a sync method.
      8. notifyListenerWithStackOverFlowProtection 
        This is a static method which allow other Future use to directly call back Listener.

  AbstractFuture<V>:A -> { Future<V>:I }
    key methods:
      1. public V get() throws InterruptedException, ExecutionException();
        Check cause, if cause != null throw CancellationExcetion
      2. public V get(long, TimeUnit) {}
        Wait for define interval.

  CompleteFuture<V>:A -> { AbstractFuture<V> }
    This Future is always done.
    If a listener is add then operationComplete will invoke immidiately.

  CompleteChannelFuture:A -> { CompleteFuture<Void>:C, ChannelFuture:I }
    It re-implements executor() method.
    ! Get EventExecutor from channel.

  DefaultChannelPromise:C -> { DefaultPromise<Void>, ChannelPromise:I, FlushCheckpoint:I }
    ! Set Void in generic.
    Override executor() method.
    Mostly reused implemented method from DefaultPromise

  ChannelFutureListener:I -> { GenericFutureListener<ChannelFuture>:I }
    create three default ChannelFutureListener:
      CLOSE, CLOSE_ON_CAILURE, FIRE_EXCEPTION_ON_FAILURE

  DefaultChannelGroupFuture:C -> { DefaultPromise<Void>:C, ChannelGroupFuture:I }
    setSuccess onlyIf all futures success.
    Use childListener as adaptor to listen to all registed ChannelFuture.

##3. How netty represent CHANNEL:
  Key Classes:
    Channel:I -> { AttributeMap:I, ChannelOutboundInvoker:I, Comparable:I }
      Key methods:
        1. ChannelId id()
        2. Channel parent();
        3. ChannelConfig config();
        4. ChannelPipleline pipline();
        5. ByteBufAllocateor alloc();
        6. Channel read();
        7. ChannelFuture write(Object msg, ChannePromis promise); Extend from ChannelOutboundInvoker
      Key implementations:
        1. AbstractChannel:A -> { DefaultAttributeMap:C, Channel:I }
    
    ChannelFactory:I
      key methods:
        T newChannel();

    AbstractChannel:A -> { DefaultAttributeMap:C, Channel:I }
    

##4. What is eventloop in netty:

5. What is Attribute?
  Key classes:
    1. Attribute<T>:I
      use to store a value reference.
      Key methods:
        1. AttributeKey<T> key()
        2. T get()
        3. void set(T value);
      Key implementations:
        1. DefaultAttribute -> { Attribute, AtomicReference } in DefaultAttributeMap
        

    2. AttributeKey:C
      use to represent a key in AttributeMap

    3. AttributeMap:I
      input: AttributeKey<T>; output: Attribute<T>
      key implementations:
        1. DefaultAttributeMap -> { AttributeMap:I }
          There is a array to store key-value.
          So, finding need to loop all elements.
          Key methods:
            1. public <T> Attribute<T> attr(AttributeKey<T> key)
              key points:
                1. There is Updater to keep thread-safe.
                2. We should read this method.

6. How ChannelPipeline work:
  

7. How Netty handle Thread and ThreadLocal?
  Netty implement its own Thread class by using java.lang.Thread.
  
  Key classes of Netty Thread:
    1. FastThreadLocalThread:C -> { java.lang.Thread }
      It has a InternalThreadLocalMap field, which is key to FastThreadLocal.
      Key methods:
        1. threadLocalMap
      
    2. FastThreadLocalRunnable:C -> { java.lang.Runnable }
      Key methods:
        1 run
          using FastThreadLocal.removeAll() in finally -> very interesting.
  
  Key classes of Netty ThreadLocal:
    1. FastThreadLocal<V>:C
      !! Each FastThreadLocal has its own index that is get from InternalThreadLocalMap.
      !! And the index is guarantee to be unique. So instead of using HashMap like java jdk
      !! Netty use index + Object[] which reduce the cost of hash.
      Key methods:
        1. set
          This method is same as java threadLocal.set
          get corresponding InternalThreadLocalMap instance, then set into it.
          call method: setKnownNotUnset method to do set.
        2. setKnownNotUnset
          call InternalThreadLocalMap.setIndexedvariable to set value -> !! This is how to set value in thread local.
          call InternalThreadLocalMap.addToVariablesToRemove to recore this FastThreadLocal, and allow to be able to remove it.
        3. initialValue
          always return null at this class.
          !!! SubClass should implement this method.
        

  Netty use InternalThreadLocalMap to link Thread and ThreadLocal.
  InternalThreadLocalMap is thread only instance. Each FastThreadLocaThead has its own instance.
  Must know!!!
    Key classes:
      1. InternalThreadLocalMap:C -> { UnpaddedInternalThreadLocalMap }
        Key methods:
          1. get
            This method is static.
            Set InternalThreadLocalMap instance into this thread first if not set before.
          2. nextVariableIndex
            
          3. setIndexedVariable
            Actual ThreadLocal set method.
          4. indexedVariable
            FastThreadLocal use it to get set method.
  Key classes:
    1. UnpaddedInternalThreadLocalMap:C
      The internal data structure that stores the thread=local variables for Netty.
    2. ThreadLocalRandom:C
      better than Random in term of overhead and contention.

8. What is EventExecutor?
  !!! Very important component in Netty.
  !! You need to understand the difference EventExecutorGroup, EventExecutor, Executor, EventLoop, and EventLoopGroup
  Netty use EventExecutorGroup to create a mutli-thread EventExecutor, which has a Array of EventExecutor inside.
  Key classes:
    !! PromiseTask<V>:C -> { DefaultPromise<V>:C, java.util.concurrent.RunnableFuture:I }
    ! EventExecutorGroup:I -> { java.util.concurrent.ScheduledExecutorService:I, Iterable<EventExecutor>:I }
    AbstractEventExecutorGroup:A -> { EventExecutorGroup:I }
    ! EventExecutor:I -> { EventExecutorGroup:I }
    AbstractEventExecutor:A -> { java.util.concurrent.AbstractExecutorService, EventExecutor } : for every subclass forcus on execute() method re-implementation.
    OrderedEventExecutor:I -> { EventExecutor:I } & process submitted tasks in an ordered. Class does not implement any method.
    !! AbstactScheduledEventExecutror:A -> { AbstractEventExecutor:A } & base class for EventExecutor that whant to support scheduling
    ScheduledFuture<V>:I -> { Future<V>, java.util.concurrent.ScheduledFuture } : no code but extend
    !! ScheduledFutureTask<V>:C -> { PromiseTask<V>, ScheduledFuture<V>, PriorityQueueNode }
    !! SingleThreadEventExecutor:C -> { AbstractScheduledEventExecutor:A }
    !! DefaultEventExecutor:C -> { SingleThreadEventExecutor:A }
    !! ThreadPerTaskExecutor:C -> { java.util.concurrent.Executor:I } & Create a new thread and start.
    !! MultithreadEventExecutorGroup:A -> { AbstractEventExecutorGroup:A }

  !!! PromiseTask<V>:C -> { DefaultPromise<V>:C, java.util.concurrent.RunnableFuture:I }
    register EventExecutor
    This class implements java RunnableFuture<V>. It is key to EvenExecutor
    !!! key question:
      1. Who responsible to call this Task. (EventExecutor?)
    Key methods:
      1. runTask
        call acutal java Runnable or Callable method.
      2. run
        call runTask method

  !!! ScheduledFutureTask<V>:C -> { PromiseTask<V>, ScheduledFuture<V>, PriorityQueueNode }
    Check the diffence between PromiseTask<V>
    Check if you are interesting how repeat task work.
    Difference:
      1. only accept AbstractScheduledEventExecutor
      2. there is deadline control -> check how it work.
      3. Check run method.
      4. use period to control if repeat the Task

    Key methods:
      1. run
        If currentTime < delayTime, queue task
        If periodNanos != 0 repeat task. add back to scheduledTaskQueue.

      2. compareTo
        Compare deadline first
        Then compare id.

  EventExecutorGroup:I -> { java.util.concurrent.ScheduledExecutorService:I, Iterable<EventExecutor>:I }
    Key methods:
      1. next()
        How to submit to shedule to next EventExecutor

  EventExecutor:I -> { EventExecutorGroup:I }
    Key methods:
      1. inEventLoop
      2. newPromise
      3. NewSucceededFuture
      4. newFailedFuture
      5. next & always point to instance itself.

  AbstractEventExecutor:A -> { java.util.concurrent.AbstractExecutorService, EventExecutor }
    It extends java AbstractExecutorService' submit implementation.
    submit will call newTaskFor to wrap input Runnable.
    It also implements its own newTaskFor method.
    Key question:
      1. Who implement execute? (subClass?)
    Key methods:
      1. newTaskFor:O
        return a new PromiseTask
    Example subClass:
      1. ImmediateEventExecutor | check if interesting

  ImmediateEventExecutor:C -> { AbstractEventExecutor:A }
    **This class shows how a single instance can be safely use in multi-thread env
    It use ThreadLocal to achieve above design.
    Do not create a new thread.
    Key method:
      1. execute
        Very interesting design on multi-thread sharing.
        If nothing running, Runnable will execute immidiately, otherwise put into Queue.

  !!! AbstactScheduledEventExecutror:A -> { AbstractEventExecutor:A } 
    Very important if you want to know how Scheduled works
    provide schedule interfaces.
    Key method:
      1. schedule
        call supClass execute implementation to schedule task.
      2. peekScheduledTask

    Key implementations:
      SingleThreadEventExecutor:A

  !!! SingleThreadEventExecutor:A -> { AbstractScheduledExecutor:A } 
    Abstract class for retrieve task from queue.
    Key methods:
      1. takeTask
        load task from superClass method pollScheduledTask

      2. doStartThread
        use ThreadPerTaskExecutor create a Java thread and start a thread.
        It is runing forever untill shutdown.
        actually run method is implemented by subClass.

      3. execute
        add Runnable into Queue.
        startThread if need

  !! DefaultEventExecutor:C -> { SingleThreadEventExecutor:A }
    Basic implementation of SingleEventExecutor
    Key methods:
      1. run
        It is a infinite loop. 
        Use takeTask method from superClass, then call task.run method.


  !! MultithreadEventExecutorGroup:A -> { AbstractEventExecutorGroup:A }
    It has an array of EvenExecutor that is created from newChild method.
    newChild method is implemented in subClass (eg. DefaultEventExecutorGroup)
    It passes whole array into a Chooser. Let Chooser who is next.
    Key methods:
      1. next
        Call Chooser.next

9. What is EventLoop?
  I think it is a speical EventExecutor that is for HandleChannel.
  This component has strong relationship with Channel, ChannelPromise, and ChannelFuture
  Question:
    What will happen after register a channel.
    !! How eventLoop connect task and channel? -> Check AbstractChannel register implementation
  Key class:
    EventLoopGroup:I -> { EventExecutorGroup:I } & provide method to register a Channel
    EventLoop:I -> { OrderedEventExecutor:I, EventLoopGroup:I } 
    AbstractEventLoop:A -> { AbstractEventExecutor:A, EventLoop:I } & override next and parent method return EventLoop and EventLoopGroup
    SingleThreadEventLoop:A -> { SingleThreadEventExecutor:A, EventLoop:I }
    DefaultEventLoopGroup:C -> { MultithreadEventLoopGroup:A }
    NioEventLoop:C -> { SingleThreadEventLoop:A }

  SingleThreadEventLoop:A -> { SingleThreadEventExecutor:A, EventLoop:I }
    Key methods:
      1. register
        register this EventLoop into Channel.

  DefaultEventLoopGroup:C -> { MultithreadEventLoopGroup:A }
    Key methods:
      1. newChild
        return a new DefaultEventLoop

10. How netty handle nio?
  You need to know how netty override Selector first.
  Key Class:
    !!! NioEventLoop:C -> { SingleThreadEventLoop:A }
    NioEventLoopGroup:C -> { MultithreadEventLoopGroup:A }

  !!! NioEventLoop:C -> { SingleThreadEventLoop:A }
    ! Check all override method.
    Key methods:
      1. newTaskQueue:O
        use JCTools to perform better queue. Check out if you are interesting.
      2. wakeup:O
        call jdk selector.wakeup
      3. processSelectedKey
        
        

11. What is Channel

12. How netty handle Selector?
  Netty use reflection change selectedKeys and publicSelectedKeys in java Selection implementation.
  It uses SelectedSelectionKeySet instead of HashSet to achieve better performance
  Key classes:
    SelectedSelectionKeySet:C -> { AbstractSet<SelectionKey> } ? How it handle key remove $ Use reset in stead of iterate remove to achieve better performance.
    SelectedSelectionKeySetSelector:C -> { Selector:A }


