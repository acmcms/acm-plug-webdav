package ru.myx.webdav;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

/**
 * The MoveMethod class encapsulates the Commands necessary to rename a
 * resource.
 * 
 * @see ru.myx.webdav.Method
 * @author Marc Eaddy
 * @version 1.0, 8 Nov 1997
 */
@SuppressWarnings("javadoc")
class MethodCopy extends Method {
	private final StorageManager	storageManager;
	
	private Depth					depth	= null;
	
	/**
	 * @param storageManager
	 * @param uri
	 * @param query
	 * @see Method
	 */
	MethodCopy(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super( uri, query );
		this.depth = new Depth( Base.getString( query.getAttributes(), "Depth", null ) );
		this.storageManager = storageManager;
	}
	
	@Override
	protected ReplyAnswer run() {
		final String destination = Base.getString( this.query.getAttributes(), "Destination", "" ).trim();
		if (destination.length() == 0) {
			return Reply.string( "DAV", this.query, "Cannot copy without destination!" ).setCode( Reply.CD_UNKNOWN );
		}
		final String source = this.query.getUrl();
		final String check = source.substring( 0, source.length() - this.query.getResourceIdentifier().length() );
		if (!destination.startsWith( check )) {
			return Reply.string( "DAV", this.query, "Cannot copy outside the storage!" )
					.setCode( Reply.CD_UNIMPLEMENTED );
		}
		final Entry file = this.storageManager.getResourceFile( this.uri, false );
		if (file == null || !file.isExist()) {
			return Reply.string( "DAV", this.query, "Cannot copy no file exists!" ).setCode( Reply.CD_CONFLICT );
		}
		final String rename = destination.substring( check.length() );
		final String overwrite = Base.getString( this.query.getAttributes(), "Overwrite", "F" ).trim().toUpperCase();
		final Uri targetUri = new Uri( rename );
		final int code = this.storageManager.copyResource( this.uri, targetUri, "T".equals( overwrite ), this.depth );
		return Reply.empty( "DAV", this.query ).setCode( Math.abs( code % 1000 ) );
	}
}
