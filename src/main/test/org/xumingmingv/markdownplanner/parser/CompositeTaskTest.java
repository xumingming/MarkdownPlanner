package org.xumingmingv.markdownplanner.parser;

import org.xumingmingv.markdownplanner.model.Header;
import org.xumingmingv.markdownplanner.model.task.CompositeTask;
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

    @Test
    public void getOwner() throws Exception {
        CompositeTask task = new CompositeTask(Header.create(), "task1");
        task.addOwnerCost("james", 10, 5);
        task.addOwnerCost("bond", 5, 3);
        assertEquals(task.getOwner(), "james/bond");
    }
}
