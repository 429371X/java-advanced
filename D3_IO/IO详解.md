# Java IO 详解

---

## 一、什么是 IO？

IO（Input/Output，输入/输出）是程序与外部世界交换数据的通道。Java 中通过**流（Stream）**的抽象来统一处理所有 IO 操作——数据像水流一样从源头流向目的地。

---

### 1.1 数据流向

```
数据源（文件/网络/内存）  ──读取──▶  程序  ──写入──▶  目标（文件/网络/内存）
        ◀── 输入流（InputStream）──              ── 输出流（OutputStream）──▶
```

### 1.2 流的分类

| 分类方式 | 类型 | 说明 |
|----------|------|------|
| **按数据单位** | 字节流 | 以 `byte`（8 位）为单位，处理二进制数据（图片、视频、音频） |
| | 字符流 | 以 `char`（16 位）为单位，处理文本数据（自动处理编码） |
| **按数据流向** | 输入流 | 从数据源读取数据到程序 |
| | 输出流 | 从程序写出数据到目标 |
| **按功能** | 节点流 | 直接连接数据源（如 `FileInputStream`） |
| | 处理流 | 包装节点流，增强功能（如 `BufferedInputStream`） |

---

## 二、字节流（InputStream / OutputStream）

字节流是所有 IO 流的**根基**，一切流最终都按字节传输。

---

### 2.1 InputStream（输入字节流）

```java
// InputStream 核心方法
public int read();                              // 读取一个字节（0~255），返回 -1 表示结束
public int read(byte[] b);                      // 读取多个字节到数组，返回实际读取数
public int read(byte[] b, int off, int len);    // 读取 len 个字节到数组的 off 位置
public void close();                            // 关闭流，释放资源
```

#### FileInputStream — 读取文件

```java
// 方式1：逐字节读取（最慢）
try (FileInputStream fis = new FileInputStream("test.txt")) {
    int b;
    while ((b = fis.read()) != -1) {
        System.out.print((char) b);   // 一次只读一个字节
    }
}

// 方式2：按字节数组读取（推荐，一次读 1024 字节）
try (FileInputStream fis = new FileInputStream("test.txt")) {
    byte[] buffer = new byte[1024];
    int len;
    while ((len = fis.read(buffer)) != -1) {
        // 处理 buffer[0] ~ buffer[len-1] 的数据
        System.out.print(new String(buffer, 0, len));
    }
}

// 方式3：readAllBytes（JDK 9+，一次性读到内存，小文件才用）
byte[] allBytes = new FileInputStream("test.txt").readAllBytes();
```

> 推荐使用 `try-with-resources`（JDK 7+），流会在代码块结束时**自动关闭**，无需手动 `close()`。

#### ByteArrayInputStream — 从内存字节数组读取

```java
byte[] data = {72, 101, 108, 108, 111};  // "Hello"
try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
    int b;
    while ((b = bais.read()) != -1) {
        System.out.print((char) b);  // Hello
    }
}
// ByteArrayInputStream 不需要 close，但习惯性放入 try-with-resources 也没问题
```

---

### 2.2 OutputStream（输出字节流）

```java
// OutputStream 核心方法
public void write(int b);                       // 写一个字节
public void write(byte[] b);                    // 写整个字节数组
public void write(byte[] b, int off, int len);  // 写字节数组的指定部分
public void flush();                            // 刷新缓冲区（强制写出）
public void close();                            // 关闭流
```

#### FileOutputStream — 写入文件

```java
// 覆盖写入（默认）
try (FileOutputStream fos = new FileOutputStream("output.txt")) {
    fos.write("Hello World".getBytes());
}

// 追加写入（第二个参数 true）
try (FileOutputStream fos = new FileOutputStream("output.txt", true)) {
    fos.write("\n追加一行".getBytes());
}
```

#### ByteArrayOutputStream — 写入内存字节数组

```java
try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
    baos.write("Hello ".getBytes());
    baos.write("World".getBytes());
    byte[] result = baos.toByteArray();  // 获取全部写入的数据
    System.out.println(new String(result));  // Hello World
}
```

---

## 三、字符流（Reader / Writer）

字节流按字节读写，不关心字符编码。字符流按**字符**读写，自动处理编码转换，专门用来处理文本。

---

### 3.1 Reader（输入字符流）

```java
// Reader 核心方法
public int read();                              // 读取一个字符，返回 -1 表示结束
public int read(char[] cbuf);                   // 读取多个字符到数组
public int read(char[] cbuf, int off, int len); // 读取指定长度
public void close();
```

#### FileReader — 读取文本文件

```java
// 方式1：逐字符读取
try (FileReader fr = new FileReader("test.txt")) {
    int ch;
    while ((ch = fr.read()) != -1) {
        System.out.print((char) ch);
    }
}

// 方式2：按字符数组读取（推荐）
try (FileReader fr = new FileReader("test.txt")) {
    char[] buffer = new char[1024];
    int len;
    while ((len = fr.read(buffer)) != -1) {
        System.out.print(new String(buffer, 0, len));
    }
}
```

> `FileReader` 使用系统默认编码（通常 GBK / UTF-8 取决于系统）。如需指定编码，使用 `InputStreamReader`。

#### InputStreamReader — 指定编码读取

```java
// 指定 UTF-8 编码读取文件
try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream("test.txt"), StandardCharsets.UTF_8))) {
    String line;
    while ((line = br.readLine()) != null) {
        System.out.println(line);
    }
}
```

> **重要：** `FileReader` 底层也是 `InputStreamReader`，但使用默认编码。**跨平台项目建议显式指定编码**，避免中文乱码。

---

### 3.2 Writer（输出字符流）

```java
// Writer 核心方法
public void write(int c);                       // 写一个字符
public void write(char[] cbuf);                 // 写字符数组
public void write(String str);                  // 写字符串
public void write(String str, int off, int len);// 写字符串的指定部分
public void flush();                            // 刷新缓冲区
public void close();
```

#### FileWriter — 写入文本文件

```java
// 覆盖写入
try (FileWriter fw = new FileWriter("output.txt")) {
    fw.write("Hello World\n");
    fw.write("第二行");
}

// 追加写入
try (FileWriter fw = new FileWriter("output.txt", true)) {
    fw.write("\n追加内容");
}
```

> `FileWriter` 同样使用系统默认编码。如果需要指定 UTF-8，用 `OutputStreamWriter`：
> ```java
> try (Writer w = new OutputStreamWriter(new FileOutputStream("out.txt"), StandardCharsets.UTF_8)) {
>     w.write("中文内容");
> }
> ```

---

### 3.3 字节流 vs 字符流的选用

| 场景 | 使用 | 原因 |
|------|------|------|
| 图片、视频、音频、PDF | **字节流** | 二进制数据没有字符概念 |
| 纯文本文件（.txt、.java、.json） | **字符流** | 自动处理编码，读写更方便 |
| 文本文件需要指定编码 | `InputStreamReader` / `OutputStreamWriter` | `FileReader`/`FileWriter` 只能用默认编码 |

---

## 四、缓冲流（Buffered Streams）

缓冲流是**处理流**中最常用的一种——内部维护一个缓冲区，减少对底层系统的频繁 IO 调用，大大提升性能。

---

### 4.1 为什么需要缓冲？

```java
// ❌ 无缓冲：每次 read() 都触发一次磁盘访问（极慢）
try (FileInputStream fis = new FileInputStream("large.bin")) {
    int b;
    while ((b = fis.read()) != -1) { /* ... */ }
}

// ✅ 有缓冲：一次从磁盘读 8192 字节到内存，程序从缓冲区取（快很多）
try (BufferedInputStream bis = new BufferedInputStream(
        new FileInputStream("large.bin"))) {
    int b;
    while ((b = bis.read()) != -1) { /* ... */ }
}
```

### 4.2 四种缓冲流

```java
// 字节缓冲输入流
BufferedInputStream bis = new BufferedInputStream(new FileInputStream("in.bin"));
// 默认缓冲区 8192 字节（8 KB），也可以手动指定：
BufferedInputStream bis2 = new BufferedInputStream(new FileInputStream("in.bin"), 16384);

// 字节缓冲输出流
BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream("out.bin"));

// 字符缓冲输入流
BufferedReader br = new BufferedReader(new FileReader("in.txt"));
// 特有方法：按行读取
String line;
while ((line = br.readLine()) != null) {
    System.out.println(line);
}

// 字符缓冲输出流
BufferedWriter bw = new BufferedWriter(new FileWriter("out.txt"));
// 特有方法：写换行符（平台无关）
bw.write("第一行");
bw.newLine();  // 等同于 \n，但跨平台（Windows 写 \r\n，Linux 写 \n）
bw.write("第二行");
```

> `BufferedOutputStream` / `BufferedWriter` 写入后需要 **`flush()`** 或 **`close()`** 才能确保数据真正写到磁盘。`close()` 会自动调用 `flush()`。

### 4.3 缓冲流装饰模式示意

```
程序 ──read()──▶  BufferedInputStream（缓冲区）  ──read()──▶  FileInputStream（实际磁盘读写）──▶ 磁盘文件
                       ▲ 处理流                                    ▲ 节点流
```

---

## 五、File 类

`java.io.File` 代表文件或目录的**路径**（不是文件内容本身），用于文件系统的元数据操作。

---

### 5.1 常用构造与路径方法

```java
File f1 = new File("D:/data/test.txt");              // 绝对路径
File f2 = new File("./src/Main.java");               // 相对路径（相对于项目根目录）
File f3 = new File("D:/data", "test.txt");           // 父目录 + 子路径

// 路径信息
f1.getName();            // "test.txt"
f1.getPath();            // "D:/data/test.txt"（构造时传什么就是什么）
f1.getAbsolutePath();    // "D:/data/test.txt"（绝对路径）
f1.getParent();          // "D:/data"
f1.length();             // 文件大小（字节数），目录返回 0
f1.lastModified();       // 最后修改时间（毫秒时间戳）
```

### 5.2 判断与操作

```java
File f = new File("D:/data/test.txt");

// 判断
f.exists();              // 是否存在
f.isFile();              // 是文件？
f.isDirectory();         // 是目录？
f.canRead();             // 可读？
f.canWrite();            // 可写？
f.isHidden();            // 隐藏文件？

// 创建
f.createNewFile();       // 创建文件（目录必须存在）
f.mkdir();               // 创建单级目录
f.mkdirs();              // 创建多级目录（父目录不存在也自动创建）

// 删除
f.delete();              // 删除文件或空目录（不进回收站，不可恢复）
f.deleteOnExit();        // JVM 退出时删除（用于临时文件）

// 列出目录内容
String[] names = f.list();                    // 返回文件名数组
File[] files = f.listFiles();                 // 返回 File 对象数组
File[] txtFiles = f.listFiles((dir, name) -> name.endsWith(".txt"));  // 过滤
```

### 5.3 遍历目录示例

```java
public static void listAll(File dir, int level) {
    if (!dir.exists() || !dir.isDirectory()) return;
    File[] files = dir.listFiles();
    if (files == null) return;
    for (File f : files) {
        // 打印缩进
        System.out.println("  ".repeat(level) + (f.isDirectory() ? "[D] " : "[F] ") + f.getName());
        if (f.isDirectory()) {
            listAll(f, level + 1);  // 递归遍历子目录
        }
    }
}
```

---

## 六、对象序列化（Object Streams）

序列化是把 Java 对象转成字节序列（存储或传输）；反序列化是反过来，从字节序列恢复对象。

---

### 6.1 实现序列化

```java
// 1. 类必须实现 Serializable 接口（标记接口，不需要实现任何方法）
public class User implements Serializable {
    private static final long serialVersionUID = 1L;  // 序列化版本号（强烈推荐）

    private String name;
    private int age;
    private transient String password;  // transient：该字段不参与序列化

    public User(String name, int age, String password) {
        this.name = name;
        this.age = age;
        this.password = password;
    }
}

// 2. 序列化：对象 → 字节序列
try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("user.obj"))) {
    oos.writeObject(new User("张三", 25, "123456"));
}

// 3. 反序列化：字节序列 → 对象
try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("user.obj"))) {
    User user = (User) ois.readObject();
    System.out.println(user.getName());     // 张三
    System.out.println(user.getPassword()); // null（transient，不会序列化）
}
```

### 6.2 serialVersionUID 的重要

```java
// ❌ 没有 serialVersionUID 会有麻烦：类结构稍有变化，反序列化就失败
public class User implements Serializable {
    private String name;
    private int age;
    // 之后加了 private String email —— 反序列化会抛 InvalidClassException
}

// ✅ 显式定义 serialVersionUID，兼容修改
public class User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private int age;
    private String email;  // 新增字段 → 反序列化时该字段为 null，不会报错
}
```

| 要点 | 说明 |
|------|------|
| **Serializable** | 标记接口，不实现就不能序列化 |
| **serialVersionUID** | 版本控制号，**强烈建议显式定义** |
| **transient** | 标记的字段不序列化（密码、连接池等敏感/不可序列化的字段） |
| **static** | 静态字段不序列化（属于类，不属于对象） |
| **序列化深度** | 对象内部的引用类型也会递归序列化（被引用对象也必须 Serializable） |

---

## 七、try-with-resources（JDK 7+）

---

### 7.1 传统方式 vs try-with-resources

```java
// ❌ 传统方式：又臭又长，容易漏关
FileInputStream fis = null;
try {
    fis = new FileInputStream("test.txt");
    // 读数据...
} catch (IOException e) {
    e.printStackTrace();
} finally {
    if (fis != null) {
        try {
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// ✅ try-with-resources：简洁安全，自动关闭
try (FileInputStream fis = new FileInputStream("test.txt")) {
    // 读数据...
} catch (IOException e) {
    e.printStackTrace();
}
// fis 会自动调用 close()，即使发生了异常
```

### 7.2 管理多个资源

```java
// 多个资源用分号分隔，关闭顺序与声明顺序相反（后打开的先关）
try (
    FileInputStream fis = new FileInputStream("input.txt");
    FileOutputStream fos = new FileOutputStream("output.txt");
) {
    // 复制文件...
}
```

> **原理：** `try()` 括号中的对象必须实现 `AutoCloseable` 接口（`Closeable` 继承了它）。Java IO 的所有流都实现了该接口。

---

## 八、NIO 简介（New IO / Non-Blocking IO）

JDK 4 引入 NIO（New IO），JDK 7 增强为 NIO.2，提供了三大核心组件：

---

### 8.1 与传统 IO 的对比

| 特性 | 传统 IO（java.io） | NIO（java.nio） |
|------|-------------------|-----------------|
| **数据模型** | 流式（Stream），单向 | 块式（Buffer + Channel），双向 |
| **阻塞** | 阻塞 | 阻塞 + 非阻塞 |
| **处理方式** | 面向流 | 面向缓冲区和通道 |
| **目录操作** | File 类（功能有限） | Path + Files（丰富） |
| **性能** | 一般 | 高（批量操作、内存映射） |

---

### 8.2 三大核心组件

```
Channel（通道）    →  数据传输的管道（双向，连接数据源）
Buffer（缓冲区）   →  存放数据的容器（读和写都要经过 Buffer）
Selector（选择器） →  一个线程管理多个 Channel（非阻塞 IO 的核心）
```

```java
// 使用 NIO 复制文件
try (FileChannel source = new FileInputStream("source.txt").getChannel();
     FileChannel dest = new FileOutputStream("dest.txt").getChannel()) {
    ByteBuffer buffer = ByteBuffer.allocate(1024);
    while (source.read(buffer) != -1) {
        buffer.flip();          // 切换到读模式
        dest.write(buffer);
        buffer.clear();         // 清空缓冲区，准备下一次读取
    }
}
```

### 8.3 NIO.2 Path 和 Files

```java
// Path 替代 File（JDK 7+）
Path path = Paths.get("D:/data/test.txt");

// 更丰富的文件操作
Files.readAllBytes(path);            // 读所有字节
Files.readAllLines(path);            // 读所有行（List<String>）
Files.lines(path);                   // 返回 Stream<String>，逐行懒加载
Files.write(path, lines);            // 写入
Files.copy(src, dest);               // 复制
Files.move(src, dest);               // 移动/重命名
Files.delete(path);                  // 删除
Files.exists(path);                  // 判断存在
Files.isDirectory(path);             // 判断目录
Files.walk(path);                    // 递归遍历目录树（Stream<Path>）
Files.size(path);                    // 文件大小
Files.createDirectories(path);       // 创建多级目录
```

---

## 九、常用 IO 工具类速查

---

### 9.1 文件复制

```java
// 传统 IO 方式
public static void copyFile(File src, File dest) throws IOException {
    try (FileInputStream fis = new FileInputStream(src);
         FileOutputStream fos = new FileOutputStream(dest)) {
        byte[] buffer = new byte[8192];
        int len;
        while ((len = fis.read(buffer)) != -1) {
            fos.write(buffer, 0, len);
        }
    }
}

// NIO 方式（更简洁，大文件性能更好）
public static void copyFileNIO(Path src, Path dest) throws IOException {
    Files.copy(src, dest, StandardCopyOption.REPLACE_EXISTING);
}
```

### 9.2 读取文本文件全部内容

```java
// JDK 8+ Stream 方式
try (Stream<String> lines = Files.lines(Paths.get("test.txt"))) {
    lines.forEach(System.out::println);
}

// JDK 7+ 一次性读取
List<String> allLines = Files.readAllLines(Paths.get("test.txt"));

// JDK 11+ 更简洁
String content = Files.readString(Path.of("test.txt"));
```

### 9.3 Scanner — 方便读取输入

```java
// 从文件读取
try (Scanner sc = new Scanner(new File("test.txt"))) {
    while (sc.hasNextLine()) {
        String line = sc.nextLine();
        System.out.println(line);
    }
}

// 从控制台读取
Scanner sc = new Scanner(System.in);
String input = sc.nextLine();
```

---

## 十、IO 流选择指南

```
需要读/写二进制数据？
  ├─ 是 → 字节流
  │       ├─ 从文件 → FileInputStream / FileOutputStream
  │       ├─ 从字节数组 → ByteArrayInputStream / ByteArrayOutputStream
  │       ├─ 需要缓冲 → 套上 BufferedInputStream / BufferedOutputStream
  │       └─ 需要对象序列化 → ObjectInputStream / ObjectOutputStream
  │
  └─ 是文本 → 字符流
          ├─ 从文件（默认编码） → FileReader / FileWriter
          ├─ 从文件（指定编码） → InputStreamReader / OutputStreamWriter + 指定编码
          ├─ 从字符串 → StringReader / StringWriter
          ├─ 需要缓冲 + 按行读取 → 套上 BufferedReader / BufferedWriter
          └─ 需要号类型 → PrintWriter（自动 flush、println 等方法）
```

---

## 十一、常见问题

### 11.1 中文乱码

```java
// ❌ 乱码根源：编码不一致
// 文件是 UTF-8 编码，但 FileReader 使用系统默认编码（Windows 可能是 GBK）
try (BufferedReader br = new BufferedReader(new FileReader("test.txt"))) {  // 可能乱码
    // ...
}

// ✅ 解决：显式指定编码
try (BufferedReader br = new BufferedReader(
        new InputStreamReader(new FileInputStream("test.txt"), StandardCharsets.UTF_8))) {
    // ...
}
```

### 11.2 flush 什么时候需要？

```java
// BufferedOutputStream / BufferedWriter 有缓冲区，不满不会自动写磁盘
BufferedWriter bw = new BufferedWriter(new FileWriter("test.txt"));
bw.write("hello");        // 此时数据还在缓冲区，没写到磁盘
bw.flush();               // 强制刷到磁盘
// bw.close();             // close() 也会自动 flush()
bw.write("world");        // ❌ close 后不能再写
```

### 11.3 流没有关闭的后果

| 后果 | 说明 |
|------|------|
| **内存泄漏** | 流对象占用堆内存，GC 无法回收（底层持有操作系统句柄） |
| **文件锁定** | Windows 上文件可能被锁定，无法删除或修改 |
| **资源耗尽** | 进程可打开的文件句柄有上限，频繁打开不关闭会导致"Too many open files" |

> **永远使用 try-with-resources**，这是最简单可靠的关闭方式。

---

## 十二、关键要点速查

| 概念 | 一句话说明 |
|------|-----------|
| **字节流** | 以 `byte` 为单位，基类 `InputStream` / `OutputStream`，处理二进制数据 |
| **字符流** | 以 `char` 为单位，基类 `Reader` / `Writer`，自动编码转换，处理文本 |
| **节点流 vs 处理流** | 节点流直连数据源，处理流套在节点流上增强功能（装饰模式） |
| **缓冲流** | `BufferedXxx`，维护缓冲区减少磁盘访问，大幅提升 IO 性能 |
| **FileReader 编码** | 只能用系统默认编码，跨平台用 `InputStreamReader` + 指定编码 |
| **flush** | 强制将缓冲区数据刷新到目的地，close 会自动 flush |
| **try-with-resources** | `try(资源声明)` 自动关闭流，要求实现 `AutoCloseable` |
| **Serializable** | 标记接口，不实现就不能被 `ObjectOutputStream` 序列化 |
| **serialVersionUID** | 序列化版本号，显式定义避免反序列化版本冲突 |
| **transient** | 修饰的字段不参与序列化（密码、临时数据等） |
| **NIO** | 面向 Buffer + Channel，`Files.copy()` / `Files.readAllLines()` 比传统 IO 简洁 |
| **Scanner** | 方便的工具类，支持按行、按词读取文件和输入流 |
| **中文乱码** | 根本原因是编码不一致，始终显式指定字符编码 |
