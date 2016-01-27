package com.microsoft.azure.eventprocessorhost;

import java.util.concurrent.Future;
import com.microsoft.azure.eventhubs.EventData;

public class PartitionContext
{
    private ICheckpointManager checkpointManager;
    private String consumerGroupName;
    private String eventHubPath;
    private Lease lease;
    private String partitionId;

    private PartitionContext()
    {
        // FORBIDDEN!
    }

    PartitionContext(ICheckpointManager checkpointManager, String partitionId)
    {
        this.checkpointManager = checkpointManager;
        this.partitionId = partitionId;
    }

    public String getConsumerGroupName()
    {
        return this.consumerGroupName;
    }

    public void setConsumerGroupName(String consumerGroupName)
    {
        this.consumerGroupName = consumerGroupName;
    }

    public String getEventHubPath()
    {
        return this.eventHubPath;
    }

    public void setEventHubPath(String eventHubPath)
    {
        this.eventHubPath = eventHubPath;
    }

    public Lease getLease()
    {
        return this.lease;
    }

    public void setLease(Lease lease)
    {
        this.lease = lease;
    }

    public Future<Void> Checkpoint()
    {
        return this.checkpointManager.updateCheckpoint(this.partitionId, this.lease.getOffset());
    }

    public Future<Void> Checkpoint(EventData data)
    {
        return checkpointManager.updateCheckpoint(this.partitionId, data.getSystemProperties().getOffset());
    }
}
