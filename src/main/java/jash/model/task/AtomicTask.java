package jash.model.task;

import java.time.LocalDate;

import jash.model.Header;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * 任务模型
 */
@Setter
@Getter
@EqualsAndHashCode(exclude = "lineNumber")
@ToString
@NoArgsConstructor
public class AtomicTask extends AbstractTask implements Task {
    /** task owner */
    protected String owner;
    /** half man days */
    protected int cost;
    /** progress */
    protected int progress;
    /** 所在行号 */
    protected int lineNumber;

    public AtomicTask(String name, String owner, int cost) {
        this(name, owner, cost, 0);
    }

    public AtomicTask(String name, String owner, int cost, int progress) {
        this(null, null, name, owner, cost, progress, 0, 0);
    }

    public AtomicTask(Header header, LocalDate projectStartDate, String name, String owner,
        int cost, int progress, int startOffset, int endOffset) {
        this.header = header;
        this.projectStartDate = projectStartDate;
        this.name = name;
        this.owner = owner;

        this.cost = cost;
        this.progress = progress;
        this.startOffset = startOffset;
        this.endOffset = endOffset;
    }

    public boolean isComposite() {
        return false;
    }
}