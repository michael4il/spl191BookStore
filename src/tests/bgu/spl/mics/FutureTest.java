package bgu.spl.mics;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

public class FutureTest {

    Future<Integer> futr;
    /**
     * construct empty integer Future object
     * @throws Exception
     */
    @Before
    public void setUp() throws Exception {
        futr = new Future<>();
    }
    /**
     * We don't need tearDown function here.
     */

    /**
     * We assume for the test that Future is empty.
     * So for this check ONLY we will resolve the object first, otherwise we will wait forever.
     * It's true that we check method by another method but this is a basic query, and we check the methods with each other.
     */

    @Test
    public void get() {
        futr.resolve(1);
        assertEquals(1, (int)futr.get());
    }


    /**
     * @pre result == null. The future object is not yet resolved.
     * @post @post(this.get()) != @pre(this.get())
     */
    @Test
    public void resolve() {
        assertEquals(futr.get(1000,TimeUnit.MILLISECONDS), null);
        futr.resolve(2);
        assertEquals((int)futr.get(),2);
    }

    /**
     * Because we construct empty object isDone must returns false
     */
    @Test
    public void isDone() {
        assertEquals(false,futr.isDone());
    }

    /**
     * We assume for the test that Future is empty.
     * So we should reach the end of time, and therefore returns null.
     * For the test we will wait 1 second.
     */
    @Test
    public void get1() {
        assertEquals(null, futr.get(1000, TimeUnit.MILLISECONDS));
    }
}