package veinthrough.test.collection;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import veinthrough.test._enum.SIZE;

import static veinthrough.api.util.MethodLog.methodLog;
import static veinthrough.test._enum.SIZE.*;

/**
 * @author veinthrough
 *
 * 1. create list: {@link ListTest#createTest()}
 * 2. create set: {@link #setTest()}
 * 3. create map:
 * (1) {@link MapTest#createTest()}
 * (2) {@link MapTest#createFromKeysTest()}, {@link MapTest#createFromValuesTest()}
 * (3) {@link CollectionToMapTest}
 */
@Slf4j
public class CreateTest {
    private static final SIZE[] sizesArray = new SIZE[]{
            TOO_SMALL, SMALL, MEDIUM, LARGE, EXTRA_LARGE, TOO_LARGE};

    @Test
    public void setTest() {
        log.info(methodLog(
                "new from array", "" + Sets.newHashSet(sizesArray),
                "copy of array", "" + ImmutableSet.copyOf(sizesArray),
                "of elements", "" + ImmutableSet.of(
                        TOO_SMALL, SMALL, MEDIUM, LARGE, EXTRA_LARGE, TOO_LARGE)));
    }
}
