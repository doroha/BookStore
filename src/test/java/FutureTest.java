package java;

import bgu.spl.mics.Future;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class FutureTest {
    Future<Integer> test_f= new Future<Integer>();

    public void isDone_Test1() throws Exception {
        assertFalse(test_f.isDone());
    }
    public void resolve_Test() throws Exception {
        test_f.resolve(100);
        assertTrue(test_f.isDone());
    }
    public void isDone_Test2() throws Exception {
        assertTrue(test_f.isDone());
    }
    public void get_Test() throws Exception {
        test_f.resolve(100);
        Integer x=new Integer(100);
        assertEquals(x,test_f.get());
    }
}