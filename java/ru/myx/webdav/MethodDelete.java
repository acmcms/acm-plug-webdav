package ru.myx.webdav;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.BaseList;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.TreeReadType;

/**
 * The DeleteMethod class encapsulates the Commands necessary to validate and
 * delete a resource file pointed to by a Uri.
 * 
 * @see Method
 * @author Shao Ai Rong, Marc Eaddy
 * @version 2.0, 14 Dec 1997
 */
@SuppressWarnings("javadoc")
class MethodDelete extends Method {
	private static final void deleteRecursive(
			final Entry file) {
	
		final BaseList<Entry> children = file.toContainer().getContentCollection( TreeReadType.ANY ).baseValue();
		if (children != null) {
			final int length = children.length();
			for (int i = 0; i < length; ++i) {
				final Entry child = children.get( i );
				MethodDelete.deleteRecursive( child );
			}
		}
		file.doUnlink();
	}
	
	private final StorageManager	storageManager;
	
	
	/**
	 * @param storageManager
	 * @param uri
	 * @param query
	 * @see Method
	 */
	MethodDelete(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
	
		super( uri, query );
		this.storageManager = storageManager;
	}
	
	
	@Override
	protected ReplyAnswer run() {
	
		final Entry file = this.storageManager.getResourceFile( this.uri, false );
		if (file == null || !file.isExist()) {
			return Reply.string( "DAV", this.query, "Resource was not found!" ).setCode( Reply.CD_UNKNOWN );
		}
		if (!file.canWrite()) {
			return Reply.string( "DAV", this.query, "No write access!" ).setCode( Reply.CD_DENIED );
		}
		if (file.isContainer()) {
			MethodDelete.deleteRecursive( file );
			return Reply.empty( "DAV", this.query ).setCode( Reply.CD_OK );
		}
		if (file.doUnlink().baseValue().booleanValue()) {
			return Reply.empty( "DAV", this.query ).setCode( Reply.CD_OK );
		}
		return Reply.string( "DAV", this.query, "No success code while trying to unlink!" ).setCode( Reply.CD_DENIED );
	}
}
