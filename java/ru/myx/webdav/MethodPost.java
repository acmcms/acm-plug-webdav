package ru.myx.webdav;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.serve.ServeRequest;

/**
 * The GetMethod class encapsulates the Commands necessary to validate and
 * retrieve a resource file pointed to by a Uri.
 * 
 * @see Method
 * @author Marc Eaddy
 * @version 1.0, 8 Nov 1997
 */
@SuppressWarnings("javadoc")
class MethodPost extends Method {
	/**
	 * @param uri
	 * @param query
	 * @see Method
	 */
	MethodPost(final Uri uri, final ServeRequest query) {
		super( uri, query );
	}
	
	@Override
	protected ReplyAnswer run() {
		return Reply.string( "DAV", this.query, "resource not found: " + this.query.getResourceIdentifier() )
				.setCode( Reply.CD_UNKNOWN );
	}
}
