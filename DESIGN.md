Netty Object reuse?
  Key class:
    1. WeakOrderQueue:C -> { WeakReference<Thread>:JDK }
    2. Head:C < ( WeakOrderQueue:C )
    3. Link:C < ( WeakOrderQueue:C ) -> { AtomicInteger:JDK }

  Link:C < ( WeakOrderQueue:C ) -> { AtomicInteger:JDK }
    has next to point to next Link
    Each Link has its own DefaultHandle array.
    The size of the DefaultHandle array is controlled by LINK_CAPACITY
    ? but I do not know why it needs to extend from AtomicInteger.
  Head:C < ( WeakOrderQueue:C )
    
