/*
 * Copyright (c) Microsoft. All rights reserved.
 * Licensed under the MIT license. See LICENSE file in the project root for full license information.
 */

package com.microsoft.azure.eventprocessorhost;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import com.microsoft.azure.eventhubs.EventData;
import com.microsoft.azure.eventhubs.PartitionReceiver;

public class PartitionContext
{
    private ICheckpointManager checkpointManager;
    private String consumerGroupName;
    private String eventHubPath;
    private Lease lease;
    private String partitionId;
    private String offset = PartitionReceiver.START_OF_STREAM;
    private long sequenceNumber = 0;;

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
    
    public void setOffsetAndSequenceNumber(EventData event)
    {
    	setOffsetAndSequenceNumber(event.getSystemProperties().getOffset(), event.getSystemProperties().getSequenceNumber());
    }
    
    public void setOffsetAndSequenceNumber(String offset, long sequenceNumber) throws IllegalArgumentException
    {
    	synchronized (this.offset)
    	{
    		if (sequenceNumber >= this.sequenceNumber)
    		{
    			this.offset = offset;
    			this.sequenceNumber = sequenceNumber;
    		}
    		else
    		{
    			throw new IllegalArgumentException("new offset less than old");
    		}
    	}
    }
    
    public String getPartitionId()
    {
    	return this.partitionId;
    }
    
    String getStartingOffset() throws InterruptedException, ExecutionException
    {
    	Checkpoint startingCheckpoint = this.checkpointManager.getCheckpoint(this.partitionId).get();
    	this.offset = startingCheckpoint.getOffset();
    	this.sequenceNumber = startingCheckpoint.getSequenceNumber();
    	return this.offset;
    }

    public Future<Void> checkpoint() throws InterruptedException, ExecutionException
    {
    	Checkpoint inStoreCheckpoint = this.checkpointManager.getCheckpoint(this.partitionId).get();
    	if (this.sequenceNumber >= inStoreCheckpoint.getSequenceNumber())
    	{
    		inStoreCheckpoint.setOffset(this.offset);
    		inStoreCheckpoint.setSequenceNumber(this.sequenceNumber);
    	}
    	else
    	{
			throw new IllegalArgumentException("new offset " + this.offset + "/" + this.sequenceNumber +
					" less than old " + inStoreCheckpoint.getOffset() + "/" + inStoreCheckpoint.getSequenceNumber());
    	}
        return this.checkpointManager.updateCheckpoint(inStoreCheckpoint);
    }

    public Future<Void> checkpoint(EventData event) throws InterruptedException, ExecutionException
    {
    	Checkpoint inStoreCheckpoint = this.checkpointManager.getCheckpoint(this.partitionId).get();
    	if (this.sequenceNumber >= inStoreCheckpoint.getSequenceNumber())
    	{
	    	inStoreCheckpoint.setOffset(event.getSystemProperties().getOffset());
	    	inStoreCheckpoint.setSequenceNumber(event.getSystemProperties().getSequenceNumber());
    	}
    	else
    	{
			throw new IllegalArgumentException("new offset " + this.offset + "/" + this.sequenceNumber +
					" less than old " + inStoreCheckpoint.getOffset() + "/" + inStoreCheckpoint.getSequenceNumber());
    	}
        return this.checkpointManager.updateCheckpoint(inStoreCheckpoint);
    }
}