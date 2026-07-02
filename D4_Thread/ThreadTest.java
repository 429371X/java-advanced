package D4_Thread;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

/**
 * Java 多线程常见概念示范 —— 每个知识点一个测试方法，覆盖多线程详解中的核心主题。
 *
 * 包含：
 *   一、创建线程的三种方式（Thread / Runnable / Callable）
 *   二、Thread 常用方法（sleep / join / interrupt / 守护线程）
 *   三、线程状态查看
 *   四、线程安全问题演示
 *   五、synchronized 同步锁
 *   六、Lock 锁（ReentrantLock / ReadWriteLock）
 *   七、线程通信（wait/notify / Condition）
 *   八、线程池
 *   九、JUC 并发工具类（CountDownLatch / CyclicBarrier / Semaphore）
 *   十、Atomic 原子类
 *   十一、volatile 可见性
 *   十二、ThreadLocal
 *   十三、JUC 并发集合
 *   十四、死锁演示与排查
 *   十五、CompletableFuture 异步编排
 */
public class ThreadTest {

    public static void main(String[] args) throws Exception {

        // ============================================================
        // 一、创建线程的三种方式
        // ============================================================
        System.out.println("==================== 一、创建线程的三种方式 ====================");
        testExtendsThread();
        testRunnable();
        testCallable();

        // ============================================================
        // 二、Thread 常用方法
        // ============================================================
        System.out.println("\n==================== 二、Thread 常用方法 ====================");
        testSleepAndJoin();
        testInterrupt();
        testDaemonThread();

        // ============================================================
        // 三、线程状态
        // ============================================================
        System.out.println("\n==================== 三、线程状态 ====================");
        testThreadStates();

        // ============================================================
        // 四、线程安全问题
        // ============================================================
        System.out.println("\n==================== 四、线程安全问题 ====================");
        testThreadUnsafe();

        // ============================================================
        // 五、synchronized
        // ============================================================
        System.out.println("\n==================== 五、synchronized 同步锁 ====================");
        testSynchronized();

        // ============================================================
        // 六、Lock
        // ============================================================
        System.out.println("\n==================== 六、Lock 锁 ====================");
        testReentrantLock();
        testReadWriteLock();

        // ============================================================
        // 七、线程通信
        // ============================================================
        System.out.println("\n==================== 七、线程通信 wait/notify ====================");
        testWaitNotify();

        // ============================================================
        // 八、线程池
        // ============================================================
        System.out.println("\n==================== 八、线程池 ====================");
        testThreadPool();

        // ============================================================
        // 九、JUC 工具类
        // ============================================================
        System.out.println("\n==================== 九、JUC 并发工具类 ====================");
        testCountDownLatch();
        testCyclicBarrier();
        testSemaphore();

        // ============================================================
        // 十、Atomic 原子类
        // ============================================================
        System.out.println("\n==================== 十、Atomic 原子类 ====================");
        testAtomic();

        // ============================================================
        // 十一、volatile
        // ============================================================
        System.out.println("\n==================== 十一、volatile 可见性 ====================");
        testVolatile();

        // ============================================================
        // 十二、ThreadLocal
        // ============================================================
        System.out.println("\n==================== 十二、ThreadLocal ====================");
        testThreadLocal();

        // ============================================================
        // 十三、JUC 并发集合
        // ============================================================
        System.out.println("\n==================== 十三、JUC 并发集合 ====================");
        testConcurrentCollections();

        // ============================================================
        // 十四、死锁演示
        // ============================================================
        System.out.println("\n==================== 十四、死锁演示 ====================");
        testDeadlock();

        // ============================================================
        // 十五、CompletableFuture
        // ============================================================
        System.out.println("\n==================== 十五、CompletableFuture 异步编排 ====================");
        testCompletableFuture();

        System.out.println("\n✅ 全部示例执行完成！");
    }

    // ==================== 1. 继承 Thread ====================
    static void testExtendsThread() throws InterruptedException {
        System.out.println("--- 1.1 继承 Thread ---");

        class MyThread extends Thread {
            @Override
            public void run() {
                System.out.println("  " + getName() + " 正在执行");
            }
        }

        MyThread t = new MyThread();
        t.start();   // ✅ start() 会启动新线程
        t.join();    // 等待 t 执行完，保证输出顺序不乱
    }

    // ==================== 2. 实现 Runnable ====================
    static void testRunnable() throws InterruptedException {
        System.out.println("--- 1.2 实现 Runnable ---");

        // 传统写法
        Runnable task = () -> System.out.println("  Lambda 线程: " + Thread.currentThread().getName());
        Thread t = new Thread(task, "MyRunnable");
        t.start();
        t.join();

        // ⚠️ 对比：直接调用 run() 不会启动新线程
        System.out.print("  直接调用 run(): ");
        task.run();  // 输出 main，不是新线程
    }

    // ==================== 3. Callable + FutureTask ====================
    static void testCallable() throws Exception {
        System.out.println("--- 1.3 Callable + FutureTask ---");

        Callable<Integer> callable = () -> {
            int sum = 0;
            for (int i = 1; i <= 100; i++) sum += i;
            return sum;
        };

        FutureTask<Integer> task = new FutureTask<>(callable);
        Thread t = new Thread(task);
        t.start();

        Integer result = task.get();  // 阻塞获取结果
        System.out.println("  1+2+...+100 = " + result + " (预计 5050)");
    }

    // ==================== 4. sleep + join ====================
    static void testSleepAndJoin() throws InterruptedException {
        System.out.println("--- 2.1 sleep + join ---");

        long start = System.currentTimeMillis();

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(500);  // 休眠 500ms，不释放锁
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t.start();
        t.join();  // main 线程等待 t 执行完毕

        long elapsed = System.currentTimeMillis() - start;
        System.out.println("  等待耗时: " + elapsed + " ms (预计 >= 500)");
    }

    // ==================== 5. 线程中断 ====================
    static void testInterrupt() throws InterruptedException {
        System.out.println("--- 2.2 线程中断 ---");

        Thread t = new Thread(() -> {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // sleep 期间收到中断 → 抛异常，中断标志被清除
                    System.out.println("  线程收到中断信号，准备退出...");
                    Thread.currentThread().interrupt();  // 重新设置标志
                }
            }
        });

        t.start();
        Thread.sleep(100);   // 等线程进入 sleep
        t.interrupt();        // 发中断信号
        t.join();
        System.out.println("  线程已优雅退出");
    }

    // ==================== 6. 守护线程 ====================
    static void testDaemonThread() throws InterruptedException {
        System.out.println("--- 2.3 守护线程 ---");

        Thread daemon = new Thread(() -> {
            int count = 0;
            while (true) {
                System.out.println("    守护线程工作中... " + (++count));
                try { Thread.sleep(200); } catch (InterruptedException e) { break; }
            }
        });
        daemon.setDaemon(true);  // 设为守护线程
        daemon.start();

        Thread.sleep(600);  // main 等 600ms
        System.out.println("  main 线程即将结束 → 守护线程将自动退出");
    }

    // ==================== 7. 线程状态 ====================
    static void testThreadStates() throws InterruptedException {
        System.out.println("--- 3.1 线程状态查看 ---");

        Thread t = new Thread(() -> {
            try { Thread.sleep(5000); } catch (InterruptedException e) { }
        });

        System.out.println("  创建后 (NEW):        " + t.getState());
        t.start();
        System.out.println("  start 后 (RUNNABLE): " + t.getState());
        Thread.sleep(100);
        System.out.println("  sleep 中 (TIMED_WAITING): " + t.getState());
        t.interrupt();  // 打断 sleep，让它尽快结束
        t.join();
        System.out.println("  执行完 (TERMINATED): " + t.getState());
    }

    // ==================== 8. 线程安全问题 ====================
    static void testThreadUnsafe() throws InterruptedException {
        System.out.println("--- 4.1 线程不安全: count++ ---");

        // 不安全的计数器
        class UnsafeCounter {
            int count = 0;

            void increment() {
                count++;  // 非原子操作：读 → 加 → 写
            }
        }

        UnsafeCounter counter = new UnsafeCounter();
        int threads = 10;
        int perThread = 10000;
        Thread[] ts = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) counter.increment();
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();

        int expected = threads * perThread;
        System.out.println("  期望值: " + expected);
        System.out.println("  实际值: " + counter.count + " (不一致 = 线程不安全!)");
    }

    // ==================== 9. synchronized ====================
    static void testSynchronized() throws InterruptedException {
        System.out.println("--- 5.1 synchronized 解决线程安全 ---");

        // 安全计数器
        class SafeCounter {
            int count = 0;

            synchronized void increment() { count++; }  // 同步方法

            void incrementBlock() {
                synchronized (this) { count++; }         // 同步代码块（等价）
            }
        }

        SafeCounter counter = new SafeCounter();
        int threads = 10;
        int perThread = 10000;
        Thread[] ts = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) counter.increment();
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();

        int expected = threads * perThread;
        System.out.println("  期望值: " + expected);
        System.out.println("  实际值: " + counter.count + " (一致 = 线程安全!)");
    }

    // ==================== 10. ReentrantLock ====================
    static void testReentrantLock() throws InterruptedException {
        System.out.println("--- 6.1 ReentrantLock ---");

        class LockCounter {
            private final ReentrantLock lock = new ReentrantLock();
            int count = 0;

            void increment() {
                lock.lock();
                try {
                    count++;
                } finally {
                    lock.unlock();  // ⚠️ 必须 finally 中释放
                }
            }

            // tryLock 演示：拿不到锁就做别的事
            void tryIncrement() {
                if (lock.tryLock()) {
                    try {
                        count++;
                    } finally {
                        lock.unlock();
                    }
                }
                // 拿不到也不阻塞
            }
        }

        LockCounter counter = new LockCounter();
        int threads = 10;
        Thread[] ts = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            ts[i] = new Thread(() -> {
                for (int j = 0; j < 10000; j++) counter.increment();
            });
            ts[i].start();
        }
        for (Thread t : ts) t.join();

        System.out.println("  10 线程 × 10000 次 = " + counter.count + " (预计 100000)");
    }

    // ==================== 11. ReadWriteLock ====================
    static void testReadWriteLock() throws InterruptedException {
        System.out.println("--- 6.2 ReadWriteLock 读写锁 ---");

        class DataCache {
            private final ReadWriteLock rwLock = new ReentrantReadWriteLock();
            private int data = 0;

            int read() {
                rwLock.readLock().lock();
                try {
                    return data;
                } finally {
                    rwLock.readLock().unlock();
                }
            }

            void write(int value) {
                rwLock.writeLock().lock();
                try {
                    data = value;
                } finally {
                    rwLock.writeLock().unlock();
                }
            }
        }

        DataCache cache = new DataCache();

        // 多个读线程可以同时进入
        CountDownLatch latch = new CountDownLatch(5);
        for (int i = 0; i < 5; i++) {
            new Thread(() -> {
                System.out.println("  读取到: " + cache.read());
                latch.countDown();
            }).start();
        }

        cache.write(42);  // 写独占
        latch.await();
        System.out.println("  写入后读取: " + cache.read() + " (预计 42)");
    }

    // ==================== 12. wait / notify ====================
    static void testWaitNotify() throws InterruptedException {
        System.out.println("--- 7.1 wait/notify 生产者消费者 ---");

        class Buffer {
            private final Queue<Integer> queue = new LinkedList<>();
            private final int capacity = 3;

            synchronized void produce(int value) throws InterruptedException {
                while (queue.size() == capacity) {  // ⚠️ while 不是 if
                    wait();
                }
                queue.offer(value);
                System.out.println("  生产: " + value + " (队列: " + queue.size() + ")");
                notifyAll();
            }

            synchronized int consume() throws InterruptedException {
                while (queue.isEmpty()) {
                    wait();
                }
                int value = queue.poll();
                System.out.println("  消费: " + value + " (队列: " + queue.size() + ")");
                notifyAll();
                return value;
            }
        }

        Buffer buffer = new Buffer();

        // 生产者
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    buffer.produce(i);
                    Thread.sleep(50);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 消费者
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 6; i++) {
                    buffer.consume();
                    Thread.sleep(100);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        producer.start();
        consumer.start();
        producer.join();
        consumer.join();
    }

    // ==================== 13. 线程池 ====================
    static void testThreadPool() throws Exception {
        System.out.println("--- 8.1 线程池 ---");

        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                2,                              // 核心线程数
                4,                              // 最大线程数
                5, TimeUnit.SECONDS,            // 空闲存活时间
                new LinkedBlockingQueue<>(2),   // 有界队列（容量 2）
                new ThreadPoolExecutor.CallerRunsPolicy()  // 拒绝策略
        );

        // 提交 Runnable
        Future<?> f1 = executor.submit(() -> {
            System.out.println("  任务1: " + Thread.currentThread().getName());
        });

        // 提交 Callable（带返回值）
        Future<Integer> f2 = executor.submit(() -> {
            System.out.println("  任务2: " + Thread.currentThread().getName());
            Thread.sleep(200);
            return 42;
        });

        Integer result = f2.get(3, TimeUnit.SECONDS);  // 超时等待
        System.out.println("  任务2 返回值: " + result);

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);
    }

    // ==================== 14. CountDownLatch ====================
    static void testCountDownLatch() throws InterruptedException {
        System.out.println("--- 9.1 CountDownLatch ---");

        int taskCount = 3;
        CountDownLatch latch = new CountDownLatch(taskCount);

        for (int i = 0; i < taskCount; i++) {
            final int id = i + 1;
            new Thread(() -> {
                System.out.println("  子任务 " + id + " 开始");
                try { Thread.sleep(id * 100); } catch (InterruptedException e) { }
                System.out.println("  子任务 " + id + " 完成");
                latch.countDown();  // 计数 -1
            }).start();
        }

        latch.await();  // main 等待计数归零
        System.out.println("  所有子任务完成，main 继续");
    }

    // ==================== 15. CyclicBarrier ====================
    static void testCyclicBarrier() {
        System.out.println("--- 9.2 CyclicBarrier ---");

        int count = 3;
        CyclicBarrier barrier = new CyclicBarrier(count, () -> {
            System.out.println("  >>> 所有人都到了，出发！");
        });

        for (int i = 0; i < count; i++) {
            final int id = i + 1;
            new Thread(() -> {
                try {
                    System.out.println("  选手 " + id + " 到达起跑线");
                    Thread.sleep(id * 150);
                    barrier.await();  // 等其他人
                    System.out.println("  选手 " + id + " 冲线！");
                } catch (Exception e) { }
            }).start();
        }

        try { Thread.sleep(1500); } catch (InterruptedException e) { }  // 等全部执行完
    }

    // ==================== 16. Semaphore ====================
    static void testSemaphore() throws InterruptedException {
        System.out.println("--- 9.3 Semaphore ---");

        Semaphore semaphore = new Semaphore(2);  // 2 个车位

        for (int i = 0; i < 4; i++) {
            final int car = i + 1;
            new Thread(() -> {
                try {
                    semaphore.acquire();
                    System.out.println("  车 " + car + " 停车 (许可剩余: " + semaphore.availablePermits() + ")");
                    Thread.sleep(300);
                    System.out.println("  车 " + car + " 离开");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    semaphore.release();
                }
            }).start();
        }

        Thread.sleep(2000);  // 等全部执行完
    }

    // ==================== 17. Atomic 原子类 ====================
    static void testAtomic() throws InterruptedException {
        System.out.println("--- 10.1 AtomicInteger ---");

        // 对比：普通 int vs AtomicInteger
        class NormalCounter {
            int count = 0;
            void inc() { count++; }
        }

        NormalCounter nc = new NormalCounter();
        AtomicInteger ai = new AtomicInteger(0);
        int threads = 10;
        int perThread = 10000;

        Thread[] ts1 = new Thread[threads];
        Thread[] ts2 = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            ts1[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) nc.inc();
            });
            ts2[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) ai.incrementAndGet();
            });
            ts1[i].start();
            ts2[i].start();
        }
        for (int i = 0; i < threads; i++) {
            ts1[i].join();
            ts2[i].join();
        }

        System.out.println("  普通 int:    " + nc.count + " (期望 100000) — 线程不安全");
        System.out.println("  AtomicInteger: " + ai.get() + " (期望 100000) — 线程安全，无锁");

        // LongAdder — 高并发累加性能更好
        LongAdder adder = new LongAdder();
        Thread[] ts3 = new Thread[threads];
        for (int i = 0; i < threads; i++) {
            ts3[i] = new Thread(() -> {
                for (int j = 0; j < perThread; j++) adder.increment();
            });
            ts3[i].start();
        }
        for (Thread t : ts3) t.join();
        System.out.println("  LongAdder:   " + adder.sum() + " (期望 100000) — 高并发场景更优");
    }

    // ==================== 18. volatile ====================
    static void testVolatile() throws InterruptedException {
        System.out.println("--- 11.1 volatile 可见性 ---");

        class FlagHolder {
            volatile boolean running = true;  // 有 volatile → 线程能看到变化
            // boolean running = true;        // 无 volatile → 线程可能看不到变化（JIT 优化）
        }

        FlagHolder holder = new FlagHolder();

        Thread worker = new Thread(() -> {
            int count = 0;
            while (holder.running) {
                count++;
            }
            System.out.println("  worker 循环了 " + count + " 次后检测到 running=false，退出");
        });

        worker.start();
        Thread.sleep(10);  // 让 worker 跑一会儿
        holder.running = false;  // volatile 保证 worker 立即看到
        worker.join(2000);
        System.out.println("  worker 已退出（可见性生效）");
    }

    // ==================== 19. ThreadLocal ====================
    static void testThreadLocal() throws InterruptedException {
        System.out.println("--- 12.1 ThreadLocal ---");

        // 每个线程独立持有自己的副本
        ThreadLocal<Integer> threadLocal = ThreadLocal.withInitial(() -> 0);
        ThreadLocal<String> contextHolder = new ThreadLocal<>();

        Thread t1 = new Thread(() -> {
            threadLocal.set(100);
            contextHolder.set("用户A");
            System.out.println("  线程1: count=" + threadLocal.get() + ", user=" + contextHolder.get());
            // ⚠️ 必须 remove()
            threadLocal.remove();
            contextHolder.remove();
        }, "T1");

        Thread t2 = new Thread(() -> {
            threadLocal.set(200);
            contextHolder.set("用户B");
            System.out.println("  线程2: count=" + threadLocal.get() + ", user=" + contextHolder.get());
            threadLocal.remove();
            contextHolder.remove();
        }, "T2");

        // main 线程
        threadLocal.set(300);
        contextHolder.set("用户Main");
        System.out.println("  main:  count=" + threadLocal.get() + ", user=" + contextHolder.get());

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        threadLocal.remove();
        contextHolder.remove();
    }

    // ==================== 20. JUC 并发集合 ====================
    static void testConcurrentCollections() throws InterruptedException {
        System.out.println("--- 13.1 ConcurrentHashMap ---");

        ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

        // 原子复合操作
        map.put("count", 0);
        map.computeIfPresent("count", (k, v) -> v + 1);  // 原子更新
        map.putIfAbsent("newKey", 42);                     // 不存在才 put
        System.out.println("  count=" + map.get("count") + " (预计 1)");
        System.out.println("  newKey=" + map.get("newKey") + " (预计 42)");

        // ==================== 13.2 CopyOnWriteArrayList ====================
        System.out.println("--- 13.2 CopyOnWriteArrayList ---");

        CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();
        list.add("A");
        list.add("B");

        // 读无锁，写时复制整个数组
        StringBuilder sb = new StringBuilder();
        list.forEach(s -> sb.append(s).append(" "));
        System.out.println("  内容: " + sb);

        // ==================== 13.3 BlockingQueue ====================
        System.out.println("--- 13.3 BlockingQueue ---");

        BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(2);

        // 生产者
        new Thread(() -> {
            try {
                queue.put(10);
                queue.put(20);
                System.out.println("  生产: 10, 20 (队列已满)");
                queue.put(30);  // 阻塞，等消费者取走
                System.out.println("  生产: 30");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }).start();

        // 消费者
        Thread.sleep(300);
        System.out.println("  消费: " + queue.take());
        System.out.println("  消费: " + queue.take());
        System.out.println("  消费: " + queue.take());
    }

    // ==================== 21. 死锁演示 ====================
    static void testDeadlock() throws InterruptedException {
        System.out.println("--- 14.1 死锁演示（tryLock 避免） ---");

        Object lockA = new Object();
        Object lockB = new Object();

        // ❌ 死锁版本（注释掉的）
        // Thread t1 = new Thread(() -> {
        //     synchronized (lockA) {
        //         try { Thread.sleep(50); } catch (InterruptedException e) { }
        //         synchronized (lockB) {
        //             System.out.println("t1 获得两把锁");
        //         }
        //     }
        // });
        // Thread t2 = new Thread(() -> {
        //     synchronized (lockB) {
        //         try { Thread.sleep(50); } catch (InterruptedException e) { }
        //         synchronized (lockA) {
        //             System.out.println("t2 获得两把锁");
        //         }
        //     }
        // });

        // ✅ 用 tryLock 避免死锁
        ReentrantLock lock1 = new ReentrantLock();
        ReentrantLock lock2 = new ReentrantLock();

        Thread t1 = new Thread(() -> {
            try {
                if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        Thread.sleep(50);
                        if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("  线程1 获得两把锁 ✅");
                            } finally {
                                lock2.unlock();
                            }
                        } else {
                            System.out.println("  线程1 拿不到 lock2，释放 lock1");
                        }
                    } finally {
                        lock1.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                if (lock2.tryLock(1, TimeUnit.SECONDS)) {
                    try {
                        Thread.sleep(50);
                        if (lock1.tryLock(1, TimeUnit.SECONDS)) {
                            try {
                                System.out.println("  线程2 获得两把锁 ✅");
                            } finally {
                                lock1.unlock();
                            }
                        } else {
                            System.out.println("  线程2 拿不到 lock1，释放 lock2");
                        }
                    } finally {
                        lock2.unlock();
                    }
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();
        t1.join(3000);
        t2.join(3000);
        System.out.println("  未发生死锁（用 tryLock 超时机制避免）");
    }

    // ==================== 22. CompletableFuture ====================
    static void testCompletableFuture() throws Exception {
        System.out.println("--- 15.1 CompletableFuture 异步编排 ---");

        // 链式调用
        String result = CompletableFuture
                .supplyAsync(() -> {
                    System.out.println("  步骤1: 查询数据");
                    return "数据A";
                })
                .thenApply(data -> {
                    System.out.println("  步骤2: 处理 → " + data + " → 已处理");
                    return data + " → 已处理";
                })
                .thenApply(processed -> {
                    System.out.println("  步骤3: 封装 → " + processed + " → 完成");
                    return processed + " → 完成";
                })
                .get();  // 阻塞等待最终结果

        System.out.println("  最终结果: " + result);

        // 组合多个异步任务
        CompletableFuture<String> f1 = CompletableFuture.supplyAsync(() -> "Hello");
        CompletableFuture<String> f2 = CompletableFuture.supplyAsync(() -> "World");

        String combined = f1.thenCombine(f2, (s1, s2) -> s1 + " " + s2).get();
        System.out.println("  组合结果: " + combined);
    }
}
