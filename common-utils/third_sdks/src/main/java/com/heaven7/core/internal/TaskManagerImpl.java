package com.heaven7.core.internal;

import android.os.AsyncTask;

import com.heaven7.core.ITaskManager;

import java.util.ArrayList;

/**
 * Created by heaven7 on 2016/1/27.
 */
public class TaskManagerImpl implements ITaskManager{

    private final ArrayList<AsyncTask> mTasks;

    public TaskManagerImpl(int capicity){
        this.mTasks = new ArrayList<>(capicity);
    }
    public TaskManagerImpl() {
       this(4);
    }

    /** called with the lifecycle of activity-onStop */
    public void reset(){
        //reset tag
        for(AsyncTask task : mTasks){
            task.cancel(true);
        }
        mTasks.clear();
    }

    public void addTask(AsyncTask task){
        mTasks.add(task);
    }

    public void removeTask(AsyncTask task){
        mTasks.remove(task);
    }
}
