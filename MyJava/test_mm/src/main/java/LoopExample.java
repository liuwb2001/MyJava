import java.util.Date;

public class LoopExample {
    public static void test() throws InterruptedException {
        Date start = new Date(); // 记录程序开始时间
        int min = 30;
        long duration = 5 * 1000; // 30 分钟，以毫秒为单位
//        long duration = 30 * 60 * 1000;
        int n = 0;
        while (new Date().getTime() - start.getTime() <= duration) {
            // 在程序运行 30 分钟之内执行以下代码
            System.out.println(n);
            n+=1;
            Thread.sleep(1000); // 等待 1 秒钟
        }
    }
}
