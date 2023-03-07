package veinthrough.test.design;

import lombok.AllArgsConstructor;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 设计模式: command模式
 */
public class CommandTest {
    @Test
    public void commandTest() {
        Order buyOrder = new BuyOrder("000123", 10);
        Order sellOrder = new SellOrder("654321", 10);
        Broker broker = new Broker();
        broker.takeOrder(buyOrder);
        broker.takeOrder(sellOrder);
        broker.placeOrders();
    }
}

/**
 * broker: 相当于服务员
 */
class Broker {
    private List<Order> orderList = new ArrayList<>();

    void takeOrder(Order order) {
        orderList.add(order);
    }

    void placeOrders() {
        for (Order order : orderList) {
            order.execute();
        }
        orderList.clear();
    }
}

/**
 * receiver: order(command)的真正执行者
 */
@Slf4j
@AllArgsConstructor
abstract class Exchange {
    @NonNull
    protected Invoker.EXCHANGE type;

    private String sellMessage(String stockCode, int quantity) {
        return String.format("[%s]Stock[%s], %d Sold.", type, stockCode, quantity);
    }

    private String buyMessage(String stockCode, int quantity) {
        return String.format("[%s]Stock[%s], %d bought.", type, stockCode, quantity);
    }

    void buy(String stockCode, int quantity) {
        log.info(buyMessage(stockCode, quantity));
    }

    void sell(String stockCode, int quantity) {
        log.info(sellMessage(stockCode, quantity));
    }
}

/**
 * receiver: order(command)的真正执行者
 */
class SHExchange extends Exchange {
    SHExchange() {
        super(Invoker.EXCHANGE.SH);
    }
}

/**
 * receiver: order(command)的真正执行者
 */
class BJExchange extends Exchange {
    BJExchange() {
        super(Invoker.EXCHANGE.BJ);
    }
}

/**
 * invoker, 可以当作老板, 清楚地知道Order(Command)与Exchange(Receiver)的关系
 */
class Invoker {
    enum EXCHANGE {BJ, SH, SZ}

    private static final Map<String, Exchange> exchanges = new HashMap<String, Exchange>() {{
        put(EXCHANGE.SH.name(), new SHExchange());
        put(EXCHANGE.BJ.name(), new BJExchange());
    }};

    static Exchange getExchange(String stockCode) {
        return stockCode.charAt(0) == '0' ?
                exchanges.get(EXCHANGE.BJ.name()) :
                exchanges.get(EXCHANGE.SH.name());
    }
}

/**
 * command
 */
interface Order {
    void execute();
}

/**
 * command
 */
@AllArgsConstructor
class BuyOrder implements Order {
    @NonNull String stockCode;
    @NonNull int quantity;

    private void buy() {
        Invoker.getExchange(stockCode).buy(stockCode, quantity);
    }

    @Override
    public void execute() {
        buy();
    }
}

/**
 * command
 */
@AllArgsConstructor
class SellOrder implements Order {
    @NonNull String stockCode;
    @NonNull int quantity;

    private void sell() {
        Invoker.getExchange(stockCode).sell(stockCode, quantity);
    }

    @Override
    public void execute() {
        sell();
    }
}
