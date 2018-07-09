package ru.myx.webdav;

import ru.myx.ae3.base.BaseObject;

class DavLock {
	private final String		lockToken;
	
	private final BaseObject	lockData;
	
	DavLock(final String lockToken, final BaseObject lockData) {
		this.lockToken = lockToken;
		this.lockData = lockData;
	}
	
	final BaseObject getLockData() {
		return this.lockData;
	}
	
	final String getLockToken() {
		return this.lockToken;
	}
}
