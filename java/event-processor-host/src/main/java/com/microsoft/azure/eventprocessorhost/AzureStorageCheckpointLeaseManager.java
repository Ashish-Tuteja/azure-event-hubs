package com.microsoft.azure.eventprocessorhost;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.*;


public class AzureStorageCheckpointLeaseManager implements ICheckpointManager, ILeaseManager
{
    private EventProcessorHost host;
    private String namespaceName;
    private String eventHubPath;
    private String consumerGroup;
    private ExecutorService executorService = null;
    private String storageConnectionString;

    public AzureStorageCheckpointLeaseManager(String storageConnectionString, String namespaceName,
                                              String eventHubPath, String consumerGroup)
    {
        this.storageConnectionString = storageConnectionString;
        this.namespaceName = namespaceName;
        this.eventHubPath = eventHubPath;
        this.consumerGroup = consumerGroup;
    }

    // These values can't be set in the constructor because the object is constructed before the
    // caller has set them up.
    public void setLateSettings(EventProcessorHost host, ExecutorService executorService)
    {
        this.host = host;
        this.executorService = executorService;
    }

    public Future<Boolean> checkpointStoreExists()
    {
        return this.executorService.submit(new CheckpointStoreExistsCallable());
    }

    public Future<Boolean> createCheckpointStoreIfNotExists()
    {
        return this.executorService.submit(new CreateCheckpointStoreIfNotExistsCallable());
    }

    public Future<String> getCheckpoint(String partitionId)
    {
        return this.executorService.submit(new GetCheckpointCallable(partitionId));
    }

    public Iterable<Future<String>> getAllCheckpoints()
    {
        ArrayList<Future<String>> checkpoints = new ArrayList<Future<String>>();
        // TODO for each partition call getCheckpoint()
        return checkpoints;
    }

    public Future<Void> updateCheckpoint(String partitionId, String offset)
    {
        return this.executorService.submit(new UpdateCheckpointCallable(partitionId, offset));
    }

    public Future<Void> deleteCheckpoint(String partitionId)
    {
        return this.executorService.submit(new DeleteCheckpointCallable(partitionId));
    }


    public Future<Boolean> leaseStoreExists()
    {
        return this.executorService.submit(new LeaseStoreExistsCallable());
    }

    public Future<Boolean> createLeaseStoreIfNotExists()
    {
        return this.executorService.submit(new CreateLeaseStoreIfNotExistsCallable());
    }

    public Future<Lease> getLease(String partitionId)
    {
        return this.executorService.submit(new GetLeaseCallable(partitionId));
    }

    public Iterable<Future<Lease>> getAllLeases()
    {
        ArrayList<Future<Lease>> leases = new ArrayList<Future<Lease>>();
        // DUMMY START
        Iterable<String> partitionIds = this.host.getPartitionManager().getPartitionIds();
        for (String id : partitionIds)
        {
            leases.add(getLease(id));
        }
        // DUMMY END
        return leases;
    }

    public Future<Void> createLeaseIfNotExists(String partitionId)
    {
        return this.executorService.submit(new CreateLeaseIfNotExistsCallable(partitionId));
    }

    public Future<Void> deleteLease(String partitionId)
    {
        return this.executorService.submit(new DeleteLeaseCallable(partitionId));
    }

    public Future<Lease> acquireLease(String partitionId)
    {
        return this.executorService.submit(new AcquireLeaseCallable(partitionId));
    }

    public Future<Boolean> renewLease(Lease lease)
    {
        return this.executorService.submit(new RenewLeaseCallable(lease));
    }

    public Future<Boolean> releaseLease(Lease lease)
    {
        return this.executorService.submit(new ReleaseLeaseCallable(lease));
    }

    public Future<Boolean> updateLease(Lease lease)
    {
        return this.executorService.submit(new UpdateLeaseCallable(lease));
    }


    private class CheckpointStoreExistsCallable implements Callable<Boolean>
    {
        public Boolean call()
        {
            return false;
        }
    }

    private class CreateCheckpointStoreIfNotExistsCallable implements Callable<Boolean>
    {
        public Boolean call()
        {
            return false;
        }
    }

    private class GetCheckpointCallable implements Callable<String>
    {
        private String partitionId;

        public GetCheckpointCallable(String partitionId)
        {
            this.partitionId = partitionId;
        }

        public String call()
        {
            return "";
        }
    }

    private class UpdateCheckpointCallable implements Callable<Void>
    {
        private String partitionId;
        private String offset;

        public UpdateCheckpointCallable(String partitionId, String offset)
        {
            this.partitionId = partitionId;
            this.offset = offset;
        }

        public Void call()
        {
            return null;
        }
    }

    private class DeleteCheckpointCallable implements Callable<Void>
    {
        private String partitionId;

        public DeleteCheckpointCallable(String partitionId)
        {
            this.partitionId = partitionId;
        }

        public Void call()
        {
            return null;
        }
    }


    private class LeaseStoreExistsCallable implements Callable<Boolean>
    {
        public Boolean call()
        {
            // DUMMY STARTS
            Boolean retval = (InMemoryLeaseStore.getSingleton().inMemoryLeases != null);
            return retval;
            // DUMMY ENDS
        }
    }

    private class CreateLeaseStoreIfNotExistsCallable implements Callable<Boolean>
    {
        public Boolean call()
        {
            // DUMMY STARTS
            if (InMemoryLeaseStore.getSingleton().inMemoryLeases == null)
            {
            	AzureStorageCheckpointLeaseManager.this.host.logWithHost("createLeaseStoreIfNotExists() creating in memory hashmap");
                InMemoryLeaseStore.getSingleton().inMemoryLeases = new HashMap<String, Lease>();
            }
            return true;
            // DUMMY ENDS
        }
    }

    private class GetLeaseCallable implements Callable<Lease>
    {
        private String partitionId;

        public GetLeaseCallable(String partitionId)
        {
            this.partitionId = partitionId;
        }

        public Lease call()
        {
            // DUMMY STARTS
            Lease returnLease = null;
            if (InMemoryLeaseStore.getSingleton().inMemoryLeases.containsKey(this.partitionId))
            {
                returnLease = InMemoryLeaseStore.getSingleton().inMemoryLeases.get(this.partitionId);
            }
            else
            {
            	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "getLease() no existing lease");
            }
            return returnLease;
            // DUMMY ENDS
        }
    }

    private class CreateLeaseIfNotExistsCallable implements Callable<Void>
    {
        private String partitionId;

        public CreateLeaseIfNotExistsCallable(String partitionId)
        {
            this.partitionId = partitionId;
        }

        public Void call()
        {
            // DUMMY STARTS
            if (InMemoryLeaseStore.getSingleton().inMemoryLeases.containsKey(this.partitionId))
            {
            	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "createLeaseIfNotExists() found existing lease");
            }
            else
            {
            	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "createLeaseIfNotExists() creating new lease");
                Lease lease = new Lease(AzureStorageCheckpointLeaseManager.this.eventHubPath,
                        AzureStorageCheckpointLeaseManager.this.consumerGroup, this.partitionId);
                InMemoryLeaseStore.getSingleton().inMemoryLeases.put(this.partitionId, lease);
            }
            return null;
            // DUMMY ENDS
        }
    }

    private class DeleteLeaseCallable implements Callable<Void>
    {
        private String partitionId;

        public DeleteLeaseCallable(String partitionId)
        {
            this.partitionId = partitionId;
        }

        public Void call()
        {
            return null;
        }
    }

    private class AcquireLeaseCallable implements Callable<Lease>
    {
        private String partitionId;

        public AcquireLeaseCallable(String partitionId)
        {
            this.partitionId = partitionId;
        }

        public Lease call()
        {
            // DUMMY STARTS
            Lease leaseToReturn = null;
            if (InMemoryLeaseStore.getSingleton().inMemoryLeases.containsKey(this.partitionId))
            {
                leaseToReturn = InMemoryLeaseStore.getSingleton().inMemoryLeases.get(this.partitionId);
                if (leaseToReturn.isExpired())
                {
                	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "acquireLease() acquired lease");
                    leaseToReturn.setOwner(AzureStorageCheckpointLeaseManager.this.host.getHostName());
                }
                else if (leaseToReturn.getOwner().compareTo(AzureStorageCheckpointLeaseManager.this.host.getHostName()) == 0)
                {
                	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "acquireLease() already hold lease");
                }
                else
                {
                	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "acquireLease() stole lease from " + leaseToReturn.getOwner());
                	leaseToReturn.setOwner(AzureStorageCheckpointLeaseManager.this.host.getHostName());
                }
            }
            else
            {
            	AzureStorageCheckpointLeaseManager.this.host.logWithHostAndPartition(this.partitionId, "acquireLease() can't find lease");
            }
            return leaseToReturn;
            // DUMMY ENDS
        }
    }

    private class RenewLeaseCallable implements Callable<Boolean>
    {
        private Lease lease;

        public RenewLeaseCallable(Lease lease)
        {
            this.lease = lease;
        }

        public Boolean call()
        {
            return false;
        }
    }

    private class ReleaseLeaseCallable implements Callable<Boolean>
    {
        private Lease lease;

        public ReleaseLeaseCallable(Lease lease)
        {
            this.lease = lease;
        }

        public Boolean call()
        {
            return false;
        }
    }

    private class UpdateLeaseCallable implements Callable<Boolean>
    {
        private Lease lease;

        public UpdateLeaseCallable(Lease lease)
        {
            this.lease = lease;
        }

        public Boolean call()
        {
            return false;
        }
    }



    // DUMMY STARTS
    private static class InMemoryLeaseStore
    {
        private static InMemoryLeaseStore singleton = null;

        public static InMemoryLeaseStore getSingleton()
        {
            if (InMemoryLeaseStore.singleton == null)
            {
                InMemoryLeaseStore.singleton = new InMemoryLeaseStore();
            }
            return InMemoryLeaseStore.singleton;
        }

        public HashMap<String, Lease> inMemoryLeases = null;
    }
    // DUMMY ENDS
}
