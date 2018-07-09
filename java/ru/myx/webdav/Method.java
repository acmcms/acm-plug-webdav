/*
 * Copyright 1997-1998 by Marc Eaddy, Jonathan Shapiro, Shao Rong; ALL RIGHTS
 * RESERVED
 * 
 * Permission to use, copy, modify, and distribute this software and its
 * documentation for research and educational purpose and without fee is hereby
 * granted, provided that the above copyright notice appear in all copies and
 * that both that the copyright notice and warranty disclaimer appear in
 * supporting documentation, and that the names of the copyright holders or any
 * of their entities not be used in advertising or publicity pertaining to
 * distribution of the software without specific, written prior permission. Use
 * of this software in whole or in parts for direct commercial advantage
 * requires explicit prior permission.
 * 
 * The copyright holders disclaim all warranties with regard to this software,
 * including all implied warranties of merchantability and fitness. In no event
 * shall the copyright holders be liable for any special, indirect or
 * consequential damages or any damages whatsoever resulting from loss of use,
 * data or profits, whether in an action of contract, negligence or other
 * tortuous action, arising out of or in connection with the use or performance
 * of this software.
 */
package ru.myx.webdav;

import java.util.Iterator;

import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.xml.Xml;

/**
 * Each HTTP method implemented by the WebDav Server will have a Method subclass
 * associated with it. The Method subclass will create the CommandQueue needed
 * to carry- out the HTTP method.
 * 
 * @author Marc Eaddy
 * @version 2.0, 14 Dec 1997
 */
abstract class Method {
	protected static final boolean checkParseXmlParameters(final String owner, final ServeRequest query) {
		try {
			if (!query.isEmpty()) {
				/**
				 * TODO: check: not binary???
				 */
				final BaseObject mmdp = Xml.toBase( owner,
						query.toCharacter().getText().toString(),
						"DAV:",
						null,
						null );
				assert mmdp != null : "toMap should not return NULL";
				if (Base.hasKeys( mmdp )) {
					Report.info( owner, "got XML properties, body: \r\n" + query.toCharacter().getText() );
					for (final Iterator<String> iterator = Base.keys( mmdp ); iterator.hasNext();) {
						final String key = iterator.next();
						query.addParameter( key, mmdp.baseGet( key, BaseObject.UNDEFINED ) );
					}
				}
			}
			return true;
		} catch (final Throwable t) {
			Report.exception( owner, "Error on request", t );
			return false;
		}
	}
	
	// Stores the current request
	protected Uri				uri;
	
	protected ServeRequest	query;
	
	// Whether or not to execute the CommandQueue
	protected boolean			_bExecute	= true;
	
	/**
	 * Create a Method object. This function will only be invoked by a Method
	 * subclasses by calling super().
	 * 
	 * @param uri
	 *            [IN] Uri of the request
	 * @param query
	 */
	Method(final Uri uri, final ServeRequest query) {
		this.query = query;
		this.uri = uri;
	}
	
	protected abstract ReplyAnswer run();
}
