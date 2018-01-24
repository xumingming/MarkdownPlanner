package jash.parser;

import jash.parser.CompositeTask;
import jash.parser.Header;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * {@link CompositeTask}的单测
 */
public class CompositeTaskTest {
    @Test
    public void isCompleted() throws Exception {
        CompositeTask task = new CompositeTask(Header.create(), "task1");
        task.addOwnerCost("james", 10, 5);
        task.addOwnerCost("bond", 5, 3);
        assertFalse(task.isCompleted());

        task = new CompositeTask(Header.create(), "task1");
        task.addOwnerCost("james", 10, 5);
        task.addOwnerCost("bond", 5, 3);
        assertFalse(task.isCompleted());
    }

    @Test
    public void getCost() throws Exception {
        CompositeTask task = new CompositeTask(Header.create(), "task1");
        task.addOwnerCost("james", 10, 5);
        task.addOwnerCost("bond", 5, 3);
        assertEquals(15, task.getCost());
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setCost() throws Exception {
        CompositeTask task = new CompositeTask(Header.create(), "task1");
        task.setCost(12);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void setProgress() throws Exception {
        CompositeTask task = new CompositeTask(Header.create(), "task1");
        task.setProgress(12);
    }

    @Test
    public void getOwner() throws Exception {
        CompositeTask task = new CompositeTask(Header.create(), "task1");
        task.addOwnerCost("james", 10, 5);
        task.addOwnerCost("bond", 5, 3);
        assertEquals(task.getOwner(), "james/bond");
    }
}
