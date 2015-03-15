
package com.example.slidecutlistview;

/**
 * 撤销监听器
 */
public interface CancelListener {

    /**
     * 没做撤销动作时所做的操作
     */
    public void normalAction();

    /**
     * 执行撤销动作的方法
     */
    public void executeCancelAction();
}
