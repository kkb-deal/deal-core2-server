package cn.deal.component.utils;

import java.util.concurrent.locks.ReadWriteLock;

public interface ReadWriteLockProvider {
	ReadWriteLock getReadWriteLock(String key);
}
