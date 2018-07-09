package ru.myx.webdav;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

/**
 * The MkcolMethod class encapsulates the Commands necessary to create a
 * collection.
 * 
 * @see ru.myx.webdav.Method
 * @author Marc Eaddy
 * @version 1.0, 8 Nov 1997
 */
@SuppressWarnings("javadoc")
class MethodMkcol extends Method {
	private final StorageManager	storageManager;
	
	/**
	 * @param storageManager
	 * @param uri
	 * @param query
	 * @see Method
	 */
	MethodMkcol(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super( uri, query );
		this.storageManager = storageManager;
	}
	
	@Override
	protected ReplyAnswer run() {
		if (!this.query.isEmpty()) {
			return Reply.string( "DAV", this.query, "No body allowed for this command!" )
					.setCode( Reply.CD_UNSUPPORTED_FORMAT );
		}
		final Entry file = this.storageManager.getResourceFile( this.uri, true );
		if (file == null) {
			return Reply.string( "DAV", this.query, "Cannot create here - parent folder seems to be read-only!" )
					.setCode( Reply.CD_CONFLICT );
		}
		if (file.isExist()) {
			return Reply.string( "DAV", this.query, "Already exists!" ).setCode( Reply.CD_BADMETHOD );
		}
		if (!file.canWrite()) {
			return Reply.string( "DAV", this.query, "No write access!" ).setCode( Reply.CD_DENIED );
		}
		if (file.doSetContainer().baseValue().booleanValue()) {
			return Reply.empty( "DAV", this.query ).setCode( Reply.CD_CREATED );
		}
		return Reply.string( "DAV", this.query, "Creation failed, check parent folder!" ).setCode( Reply.CD_CONFLICT );
	}
}
