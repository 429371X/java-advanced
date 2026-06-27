package ArrayList;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Consumer;

public class Lambda {
    static void main() {
        //利用lambda表达式遍历

        Collection<String> coll = new ArrayList<>();
        coll.add("aaa");
        coll.add("bbb");
        coll.add("ccc");

        //利用匿名内部类来遍历
        //forEach在list底层就是一个for循环(在set底层是iterator迭代器)，此处是便于集合使用lambda表达式而引出
        coll.forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println("使用匿名内部类输出： " + s);
            }
        });

        System.out.println("------------------------------------------");
        //使用lambda表达式
        coll.forEach(s -> System.out.println("使用lambda表达式输出： " + s));

    }
}
