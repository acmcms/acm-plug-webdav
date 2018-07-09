package ru.myx.webdav;

import ru.myx.ae3.answer.AbstractReplyException;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.help.Format;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

/**
 * The Executive registers all the available subsystems with the
 * SubsystemRegistry and provides an interface that allows the WebDavServlet to
 * execute methods using DoMethod().
 * <P>
 * 
 * @see Command
 * @author Marc Eaddy
 * @version 2.0, 16 Nov 1997
 */
@SuppressWarnings("javadoc")
public class Executive {
	private final StorageManager	storageManager;
	
	/**
	 * @param folder
	 */
	public Executive(final Entry folder) {
		this.storageManager = new StorageManager( folder );
	}
	
	private final Method create(final String verb, final ServeRequest query) {
		final String identifier = query.getResourceIdentifier();
		final Uri uri = new Uri( identifier );
		if ("GET".equalsIgnoreCase( verb ) || "HEAD".equalsIgnoreCase( verb )) {
			return new MethodGet( this.storageManager, uri, query );
		}
		if ("PUT".equalsIgnoreCase( verb )) {
			return new MethodPut( this.storageManager, uri, query );
		}
		if ("POST".equalsIgnoreCase( verb )) {
			return new MethodPost( uri, query );
		}
		if ("MKCOL".equalsIgnoreCase( verb )) {
			return new MethodMkcol( this.storageManager, uri, query );
		}
		if ("MOVE".equalsIgnoreCase( verb )) {
			return new MethodMove( this.storageManager, uri, query );
		}
		if ("COPY".equalsIgnoreCase( verb )) {
			return new MethodCopy( this.storageManager, uri, query );
		}
		if ("DELETE".equalsIgnoreCase( verb )) {
			return new MethodDelete( this.storageManager, uri, query );
		}
		if ("PROPFIND".equalsIgnoreCase( verb )) {
			return new MethodPropFind( this.storageManager, uri, query );
		}
		if ("PROPPATCH".equalsIgnoreCase( verb )) {
			return new MethodPropPatch( this.storageManager, uri, query );
		}
		if ("LOCK".equalsIgnoreCase( verb )) {
			return new MethodLock( this.storageManager, uri, query );
		}
		if ("UNLOCK".equalsIgnoreCase( verb )) {
			return new MethodUnlock( this.storageManager, uri, query );
		}
		return new MethodInvalid( query );
	}
	
	/**
	 * @param query
	 * @return reply
	 */
	public final ReplyAnswer doMethod(final ServeRequest query) {
		final String verb = query.getVerb();
		if ("OPTIONS".equals( verb )) {
			final ReplyAnswer answer = Reply.empty( "DAV", query ).setCode( Reply.CD_OK );
			answer.setAttribute( "Allow",
					"OPTIONS, POST, GET, HEAD, PUT, MKCOL, COPY, MOVE, DELETE, PROPFIND, PROPPATCH, LOCK, UNLOCK" );
			answer.setAttribute( "MS-Author-Via", "DAV" );
			answer.setAttribute( "DAV", "1,2" );
			return answer;
		}
		try {
			return this.create( verb, query ).run();
		} catch (final AbstractReplyException e) {
			return e.getReply();
		} catch (final Throwable t) {
			Report.exception( "WEBDAV-EXECUTIVE", "WebDAV exception", t );
			return Reply.string( "WEBDAV-EXECUTIVE", query, Format.Throwable.toText( t ) )
					.setCode( Reply.CD_EXCEPTION );
		}
	}
}
