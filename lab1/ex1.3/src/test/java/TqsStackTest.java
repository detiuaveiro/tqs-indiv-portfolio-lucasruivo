
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;

import java.util.NoSuchElementException;

import tqs.TqsStack;

import static java.lang.invoke.MethodHandles.lookup;
import static org.junit.jupiter.api.Assertions.*;

class TqsStackTest {

    static final Logger log = org.slf4j.LoggerFactory.getLogger(lookup().lookupClass());

    @Test
    @DisplayName("Stack is empty on construction")
    void emptyOnConstruction() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    @DisplayName("Push elements and check size and emptiness")
    void pushElements() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(1);
        stack.push(2);
        stack.push(3);
        assertFalse(stack.isEmpty());
        assertEquals(3, stack.size());
    }

    @Test
    @DisplayName("Push then pop returns the same value")
    void pushThenPop() {
        TqsStack<String> stack = new TqsStack<>();
        stack.push("hello");
        assertEquals("hello", stack.pop());
    }

    @Test
    @DisplayName("Push then peek returns value but size remains the same")
    void pushThenPeek() {
        TqsStack<String> stack = new TqsStack<>();
        stack.push("world");
        int sizeBefore = stack.size();
        String peeked = stack.peek();
        int sizeAfter = stack.size();
        assertEquals("world", peeked);
        assertEquals(sizeBefore, sizeAfter);
    }

    @Test
    @DisplayName("Popping all elements empties the stack")
    void popAllElements() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(1);
        stack.push(2);
        stack.pop();
        stack.pop();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }

    @Test
    @DisplayName("Popping from empty stack throws NoSuchElementException")
    void popEmptyStackThrows() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertThrows(NoSuchElementException.class, stack::pop);
    }

    @Test
    @DisplayName("Peeking into empty stack throws NoSuchElementException")
    void peekEmptyStackThrows() {
        TqsStack<Integer> stack = new TqsStack<>();
        assertThrows(NoSuchElementException.class, stack::peek);
    }

    @Test
    @DisplayName("popTopN removes n-1 elements and returns the nth item")
    void popTopNTest() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(1); stack.push(2); stack.push(3); stack.push(4);
        int result = stack.popTopN(3);
        assertEquals(2, result);
        assertEquals(1, stack.size());
    }

    // ===== NOVOS TESTES PARA COBERTURA =====

    @Test
    @DisplayName("popTopN with n = 1 returns top element without removing others")
    void popTopN1() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(10); stack.push(20);
        int result = stack.popTopN(1);
        assertEquals(20, result);
        assertEquals(1, stack.size());
    }

    @Test
    @DisplayName("popTopN with n > size throws exception")
    void popTopNTooBig() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(1);
        assertThrows(IllegalArgumentException.class, () -> stack.popTopN(2));
    }

    @Test
    @DisplayName("Push null element if allowed")
    void pushNull() {
        TqsStack<String> stack = new TqsStack<>();
        stack.push(null);
        assertNull(stack.peek());
    }

    @Test
    @DisplayName("Multiple peek calls do not change size")
    void multiplePeek() {
        TqsStack<Integer> stack = new TqsStack<>();
        stack.push(5);
        int sizeBefore = stack.size();
        assertEquals(5, stack.peek());
        assertEquals(5, stack.peek());
        assertEquals(sizeBefore, stack.size());
    }

    @Test
    @DisplayName("Pop all elements after multiple pushes")
    void popAllAfterMultiplePushes() {
        TqsStack<Integer> stack = new TqsStack<>();
        for(int i = 0; i < 100; i++) stack.push(i);
        for(int i = 0; i < 100; i++) stack.pop();
        assertTrue(stack.isEmpty());
        assertEquals(0, stack.size());
    }
}