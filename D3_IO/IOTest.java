package D3_IO;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;

/**
 * Java 常见 IO 流示范 —— 每种流一个测试方法，覆盖日常开发主要场景。
 */
public class IOTest {

    // ======================== 辅助目录 ========================
    private static final String DIR = "src/D3_IO/temp";

    public static void main(String[] args) throws Exception {
        // 初始化临时目录
        Files.createDirectories(Path.of(DIR));

        testFileInputStream();
        testFileOutputStream();
        testFileReader();
        testFileWriter();
        testBufferedStreams();
        testInputStreamReader();
        testByteArrayStreams();
        testObjectStreams();
        testFileOperations();
        testScanner();

        System.out.println("\n✅ 全部示例执行完成！");
    }

    // ======================== 1. FileInputStream ========================
    static void testFileInputStream() throws IOException {
        System.out.println("=== 1. FileInputStream（字节读取） ===");
        Path file = Path.of(DIR, "fis.txt");
        Files.writeString(file, "Hello 世界");

        // 读取方式1：逐字节（演示用，实际不推荐）
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            System.out.print("  逐字节: ");
            int b;
            while ((b = fis.read()) != -1) {
                System.out.print((char) b);
            }
            System.out.println();
        }

        // 读取方式2：数组批量读取（推荐）
        try (FileInputStream fis = new FileInputStream(file.toFile())) {
            byte[] buf = new byte[4];
            int len;
            System.out.print("  批量读取: ");
            while ((len = fis.read(buf)) != -1) {
                System.out.print("[" + len + "字节] ");
            }
            System.out.println();
        }
    }

    // ======================== 2. FileOutputStream ========================
    static void testFileOutputStream() throws IOException {
        System.out.println("=== 2. FileOutputStream（字节写入） ===");
        Path file = Path.of(DIR, "fos.txt");

        // 覆盖写入
        try (FileOutputStream fos = new FileOutputStream(file.toFile())) {
            fos.write("Hello".getBytes());
        }

        // 追加写入
        try (FileOutputStream fos = new FileOutputStream(file.toFile(), true)) {
            fos.write(", World!".getBytes());
        }

        System.out.println("  内容: " + Files.readString(file));
    }

    // ======================== 3. FileReader ========================
    static void testFileReader() throws IOException {
        System.out.println("=== 3. FileReader（字符读取） ===");
        Path file = Path.of(DIR, "fr.txt");
        Files.writeString(file, "第一行\n第二行\n第三行");

        try (FileReader fr = new FileReader(file.toFile())) {
            char[] buf = new char[10];
            int len;
            System.out.print("  内容: ");
            while ((len = fr.read(buf)) != -1) {
                System.out.print(new String(buf, 0, len));
            }
            // 输出后手动换行
            if (!"\n".equals(System.lineSeparator())) System.out.println();
        }
    }

    // ======================== 4. FileWriter ========================
    static void testFileWriter() throws IOException {
        System.out.println("=== 4. FileWriter（字符写入） ===");
        Path file = Path.of(DIR, "fw.txt");

        try (FileWriter fw = new FileWriter(file.toFile())) {
            fw.write("第一行\n");
            fw.write("第二行\n");
        }

        // 追加模式
        try (FileWriter fw = new FileWriter(file.toFile(), true)) {
            fw.write("第三行（追加）\n");
        }

        Files.readAllLines(file).forEach(l -> System.out.println("  " + l));
    }

    // ======================== 5. 缓冲流 ========================
    static void testBufferedStreams() throws IOException {
        System.out.println("=== 5. 缓冲流（BufferedXxx） ===");
        Path src = Path.of(DIR, "buf_src.txt");
        Path dest = Path.of(DIR, "buf_dest.txt");

        // 写入
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(src.toFile()))) {
            for (int i = 1; i <= 5; i++) {
                bw.write("第" + i + "行");
                bw.newLine();
            }
        }

        // 读取 + 写入（使用缓冲流复制）
        try (BufferedReader br = new BufferedReader(new FileReader(src.toFile()));
             BufferedWriter bw = new BufferedWriter(new FileWriter(dest.toFile()))) {
            String line;
            while ((line = br.readLine()) != null) {
                bw.write("COPY: " + line);
                bw.newLine();
            }
        }

        System.out.println("  源文件行数: " + Files.readAllLines(src).size());
        System.out.println("  目标文件行数: " + Files.readAllLines(dest).size());
    }

    // ======================== 6. InputStreamReader（指定编码） ========================
    static void testInputStreamReader() throws IOException {
        System.out.println("=== 6. InputStreamReader（指定编码） ===");
        Path file = Path.of(DIR, "encoding.txt");
        Files.writeString(file, "中文内容 → UTF-8 编码");

        // 显式指定 UTF-8 读取（跨平台安全）
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(new FileInputStream(file.toFile()), StandardCharsets.UTF_8))) {
            String line = br.readLine();
            System.out.println("  UTF-8 读取: " + line);
        }
    }

    // ======================== 7. ByteArray 流 ========================
    static void testByteArrayStreams() throws IOException {
        System.out.println("=== 7. ByteArrayInputStream / ByteArrayOutputStream ===");

        // 输出流：往内存写
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        baos.write("Hello ".getBytes());
        baos.write("ByteArray".getBytes());
        baos.close();  // 实际上不 close 也没事，这是内存操作

        byte[] data = baos.toByteArray();
        System.out.println("  写入字节数: " + data.length);

        // 输入流：从内存读
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data)) {
            byte[] buf = new byte[5];
            int len = bais.read(buf);
            System.out.println("  读出的前5字节: " + new String(buf, 0, len));
        }
    }

    // ======================== 8. 对象序列化 ========================
    static void testObjectStreams() {
        System.out.println("=== 8. ObjectInputStream / ObjectOutputStream ===");
        Path file = Path.of(DIR, "user.obj");

        // 序列化
        User user = new User("张三", 25, "123456");
        try (ObjectOutputStream oos =
                     new ObjectOutputStream(new FileOutputStream(file.toFile()))
            ) {
            oos.writeObject(user);
        }catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("  序列化完成: " + file);

        // 反序列化
        try (ObjectInputStream ois =
                     new ObjectInputStream(new FileInputStream(file.toFile()))) {
            User restored = (User) ois.readObject();
            System.out.println("  反序列化: name=" + restored.getName()
                    + ", age=" + restored.getAge()
                    + ", password=" + restored.getPassword()  // transient → null
                    + "（password 是 transient，应为 null）");
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ======================== 9. File 类操作 ========================
    static void testFileOperations() throws IOException {
        System.out.println("=== 9. File 类操作 ===");
        Path subDir = Path.of(DIR, "subdir");
        Path testFile = subDir.resolve("test.txt");

        // 创建目录
        Files.createDirectories(subDir);
        System.out.println("  目录存在: " + Files.exists(subDir));

        // 创建文件
        Files.writeString(testFile, "内容");
        System.out.println("  文件大小: " + Files.size(testFile) + " 字节");
        System.out.println("  是文件: " + Files.isRegularFile(testFile));

        // 列出目录
        System.out.print("  目录内容: ");
        Files.list(subDir).forEach(p -> System.out.print(p.getFileName() + " "));
        System.out.println();

        // 按后缀过滤
        File[] txtFiles = subDir.toFile().listFiles((dir, name) -> name.endsWith(".txt"));
        System.out.println("  .txt 文件数: " + (txtFiles != null ? txtFiles.length : 0));
    }

    // ======================== 10. Scanner ========================
    static void testScanner() throws IOException {
        System.out.println("=== 10. Scanner ===");
        Path file = Path.of(DIR, "scanner.txt");
        Files.writeString(file, "苹果 香蕉 橘子\n西瓜 葡萄");

        try (Scanner sc = new Scanner(file.toFile())) {
            System.out.print("  单词: ");
            while (sc.hasNext()) {
                System.out.print(sc.next() + " ");
            }
            System.out.println();
        }
    }

    // ======================== 序列化演示类 ========================
    static class User implements Serializable {// 序列化类必须实现 Serializable 接口
        private static final long serialVersionUID = 1L;

        private String name;
        private int age;
        private transient String password;  // 用transient修饰后该变量将不参与序列化

        public User(String name, int age, String password) {
            this.name = name;
            this.age = age;
            this.password = password;
        }

        public String getName() { return name; }
        public int getAge() { return age; }
        public String getPassword() { return password; }
    }
}
