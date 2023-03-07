package veinthrough.test.design;

import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import static veinthrough.api.util.MethodLog.methodLog;

/**
 * 设计模式: 访问者模式
 * 主体类(不变)上添加功能(变化)
 */
public class VisitorTest {
    @Test
    public void test() {
        Component text = new Text();
        Component audio = new Audio();
        Handler textChecker = new TextChecker();
        Handler audioConsumer = new AudioConsumer();
        text.handle(textChecker);
        text.handle(audioConsumer);
        audio.handle(textChecker);
        audio.handle(audioConsumer);
    }
}

/**
 * 主体接口
 */
interface Component {
    void handle(Handler handler);
}

/**
 * 主体类
 */
@ToString
class Text implements Component {
    @Override
    public void handle(Handler handler) {
        handler.handleText(this);
    }
}

/**
 * 主体类
 */
@ToString
class Picture implements Component {
    @Override
    public void handle(Handler handler) {
        handler.handlePicture(this);
    }
}

/**
 * 主体类
 */
@ToString
class Audio implements Component {
    @Override
    public void handle(Handler handler) {
        handler.handleAudio(this);
    }
}

/**
 * 功能接口
 */
interface Handler {
    void handleText(Text text);

    void handlePicture(Picture pic);

    void handleAudio(Audio audio);
}

/**
 * 功能类
 * 1. 添加功能不影响主体类
 * 2. 但是本功能对于某些主体可能未实现
 */
@Slf4j
abstract class Checker implements Handler {
    public void handleText(Text text) {
        checkText(text);
    }

    public void handlePicture(Picture pic) {
        checkPicture(pic);
    }

    public void handleAudio(Audio audio) {
        checkAudio(audio);
    }

    abstract void checkText(Text text);

    abstract void checkPicture(Picture pic);

    abstract void checkAudio(Audio audio);
}

/**
 * 功能类
 * 1. 添加功能不影响主体类
 * 2. 但是本功能对于某些主体可能未实现
 */
@Slf4j
class TextChecker extends Checker {

    @Override
    void checkText(Text text) {
        log.info(methodLog("" + text));
    }

    /**
     * 本功能对于某些主体可能未实现
     */
    @Override
    void checkPicture(Picture pic) {
        throw new UnsupportedOperationException("" + pic);
    }

    /**
     * 本功能对于某些主体可能未实现
     */
    @Override
    void checkAudio(Audio audio) {
        throw new UnsupportedOperationException("" + audio);
    }
}

/**
 * 功能类
 * 1. 添加功能不影响主体类
 * 2. 但是本功能对于某些主体可能未实现
 */
@Slf4j
abstract class Consumer implements Handler {
    public void handleText(Text text) {
        consumeText(text);
    }

    public void handlePicture(Picture pic) {
        consumePicture(pic);
    }

    public void handleAudio(Audio audio) {
        consumeAudio(audio);
    }

    abstract void consumeText(Text text);

    abstract void consumePicture(Picture pic);

    abstract void consumeAudio(Audio audio);
}

/**
 * 功能类
 * 1. 添加功能不影响主体类
 * 2. 但是本功能对于某些主体可能未实现
 */
@Slf4j
class AudioConsumer extends Consumer {

    /**
     * 本功能对于某些主体可能未实现
     */
    @Override
    void consumeText(Text text) {
        throw new UnsupportedOperationException("" + text);
    }

    /**
     * 本功能对于某些主体可能未实现
     */
    @Override
    void consumePicture(Picture pic) {
        throw new UnsupportedOperationException("" + pic);
    }

    @Override
    void consumeAudio(Audio audio) {
        log.info(methodLog("" + audio));
    }
}



