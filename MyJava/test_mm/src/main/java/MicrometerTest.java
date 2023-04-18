import com.sun.net.httpserver.HttpServer;
import io.micrometer.core.instrument.Clock;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.composite.CompositeMeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;
import io.micrometer.opentsdb.OpenTSDBConfig;
import io.micrometer.opentsdb.OpenTSDBMeterRegistry;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Timer;

import java.util.TimerTask;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;


/**
 * Hello world!
 *
 */
public class MicrometerTest
{
    public static void main( String[] args )
    {
        //组合注册表
        CompositeMeterRegistry composite = new CompositeMeterRegistry();
        //内存注册表
        MeterRegistry registry = new SimpleMeterRegistry();
        composite.add(registry);
        //普罗米修斯注册表
        OpenTSDBMeterRegistry openTSDBRegistry = new OpenTSDBMeterRegistry(OpenTSDBConfig.DEFAULT, Clock.SYSTEM);
//        PrometheusMeterRegistry prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        composite.add(openTSDBRegistry);
        //计数器
        Counter compositeCounter = composite.counter("counter");
        //计数
        compositeCounter.increment();

        Timer timer = composite.timer("LoopExample");
        timer.record(() -> {
            try {
//                TimeUnit.MILLISECONDS.sleep(1500);
                LoopExample.test();
            } catch (InterruptedException ignored) {
            }
        });
//        timer.record(3000, TimeUnit.MILLISECONDS);
        java.lang.String str = "LoopExample";

        try {
            //暴漏8080端口来对外提供指标数据
            HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);
            server.createContext("/prometheus", httpExchange -> {
                //获取普罗米修斯指标数据文本内容
                String response = "Done";
                //指标数据发送给客户端
                httpExchange.sendResponseHeaders(200, response.getBytes().length);
                try (OutputStream os = httpExchange.getResponseBody()) {
                    os.write(response.getBytes());
                }
            });


            new Thread(server::start).start();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

