package com.microsoft.azure.eventprocessorhost;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public final class EventProcessorOptions
{
    private Boolean invokeProcessorAfterReceiveTimeout = false; // TODO it has to default to something but what?
    private int maxBatchSize = 10;
    private int prefetchCount = 300;
    private int receiveTimeOutMilliseconds = 60000; // default to one minute
    private ExecutorService executorService;

    public static EventProcessorOptions getDefaultOptions()
    {
        return new EventProcessorOptions();
    }

    public EventProcessorOptions()
    {
        this.executorService = Executors.newCachedThreadPool();
    }

    public ExecutorService getExecutorService() { return this.executorService; }

    public void setExecutorService(ExecutorService executorService) { this.executorService = executorService; }

    public Boolean getInvokeProcessorAfterReceiveTimeout()
    {
        return this.invokeProcessorAfterReceiveTimeout;
    }

    public void setInvokeProcessorAfterReceiveTimeout(Boolean invokeProcessorAfterReceiveTimeout)
    {
        this.invokeProcessorAfterReceiveTimeout = invokeProcessorAfterReceiveTimeout;
    }

    public int getMaxBatchSize()
    {
        return this.maxBatchSize;
    }

    public void setMaxBatchSize(int maxBatchSize)
    {
        this.maxBatchSize = maxBatchSize;
    }

    public int getPrefetchCount()
    {
        return this.prefetchCount;
    }

    public void setPrefetchCount(int prefetchCount)
    {
        this.prefetchCount = prefetchCount;
    }

    public int getReceiveTimeOut()
    {
        return this.receiveTimeOutMilliseconds;
    }

    public void setReceiveTimeOut(int receiveTimeOutMilliseconds)
    {
        this.receiveTimeOutMilliseconds = receiveTimeOutMilliseconds;
    }
}
