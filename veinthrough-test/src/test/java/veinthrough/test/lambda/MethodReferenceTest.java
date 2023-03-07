package veinthrough.test.lambda;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test.async.WordCounter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * lambda:
 * 1. 函数名不是约束
 * 比如{@link Factory}中create函数从无参生成一个T类型的对象,
 * 那么任何T类型的无参构造函数都可用, 比如{@link Car#Car()};
 * 2. [类上]的[实例方法]引用, 可以不需要参数, 把this当参数
 * {@link WordCounter.FutureTaskCounter#call()}
 *  > 函数名不是约束
 *  > 把this当参数
 * {@link WordCounter.ThreadPoolCounter#call()}
 */
public class MethodReferenceTest {
    @Test
    public void methodReferenceTest() {
        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            Car car = Car.create(Car::new); // 构造方法引用Class::new/类的实例方法引用, 函数名不是约束条件, () -> Car就行
            cars.add(car);
        }
        cars.forEach(Car::showCar); // [类上]的[实例方法]引用, 可以不需要参数, 把this当参数
        cars.forEach(Car::showCarS); // [静态方法]引用, 必须有一个参数， 这里参数为car
        Stream.of("white", "black")
                .forEach(cars.get(0)::showCarColor); // [实例上]的[实例方法]引用, 没有考虑this, 这里参数为string
    }

    @FunctionalInterface
    interface Factory<T> {
        T create();
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @ToString
    @Slf4j
    static class Car {
        private String name = "car";

        static void showCarS(Car car) {
            log.info(methodLog("" + car));
        }

        void showCarColor(String color) {
            log.info(methodLog(color + " " + this));
        }

        void showCar() {
            log.info(methodLog("" + this));
        }

        static Car create(Factory<Car> factory) {
            return factory.create();
        }
    }
}