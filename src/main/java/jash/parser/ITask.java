package jash.parser;

public interface ITask {
    /**
     * 任务的ID
     * @return
     */
    int getId();

    /**
     * 父任务的ID
     * @return
     */
    int getParentId();

    /**
     * 这个任务所属的组
     * @return
     */
    Header getHeader();

    /**
     * 任务的名字
     * @return
     */
    String getName();

    /**
     * 任务的Owner
     * @return
     */
    String getOwner();

    /**
     * 任务需要的Cost
     * @return
     */
    int getCost();

    /**
     * 任务目前的进度
     * @return
     */
    int getProgress();
}
