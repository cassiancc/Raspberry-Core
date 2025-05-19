package cc.cassian.raspberry.misc.toms_storage;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class StorageTerminalHelper {
    public static final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
}
