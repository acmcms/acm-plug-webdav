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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.TimeZone;

import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.answer.ReplyAnswer;
import ru.myx.ae3.base.Base;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.help.Convert;
import ru.myx.ae3.report.Report;
import ru.myx.ae3.serve.ServeRequest;
import ru.myx.ae3.vfs.Entry;

/**
 * The PropFindMethod class encapsulates the Commands necessary to get the names
 * and values of properties defined on a resource.
 * 
 * @see Method
 * @author Jonathan Shapiro, Marc Eaddy
 * @version 2.0, 14 Dec 1997
 */
@SuppressWarnings("javadoc")
class MethodPropPatch extends Method {
	private final StorageManager	storageManager;
	
	private long					lastModified	= -1L;
	
	/**
	 * @param storageManager
	 * @param uri
	 * @param query
	 * @see Method
	 */
	MethodPropPatch(final StorageManager storageManager, final Uri uri, final ServeRequest query) {
		super( uri, query );
		this.storageManager = storageManager;
		Method.checkParseXmlParameters( "WEBDAV:PROPPATCH", query );
		final BaseObject parameters = query.getParameters();
		final Object checkSet = Base.getJava( parameters, "set", null );
		if (checkSet != null && checkSet instanceof Map<?, ?>) {
			final Object checkProp = Convert.MapEntry.toObject( (Map<?, ?>) checkSet, "prop", null );
			if (checkProp != null) {
				if (checkProp instanceof Map<?, ?>) {
					final String propGetLastModified = Convert.MapEntry.toString( (Map<?, ?>) checkProp,
							"getlastmodified",
							"" ).trim();
					if (propGetLastModified.length() > 0) {
						try {
							this.lastModified = ((long) Math.ceil( new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss'Z'" )
									.parse( propGetLastModified ).getTime() / 1000.0 ) + 1L) * 1000L;
							this.lastModified += TimeZone.getDefault().getOffset( this.lastModified );
						} catch (final ParseException e) {
							Report.exception( "DAV/PROP_PATH", "While parsing date\r\nparams=" + parameters + "\r\n", e );
						}
					}
				}
			}
		}
	}
	
	@Override
	protected ReplyAnswer run() {
		if (this.lastModified == -1L) {
			return Reply.string( "DAV", this.query, "Done." ).setCode( Reply.CD_UNIMPLEMENTED );
		}
		final Entry file = this.storageManager.getResourceFile( this.uri, false );
		if (file == null || !file.isExist()) {
			return Reply.string( "DAV", this.query, "No such resource!" ).setCode( Reply.CD_UNKNOWN )
					.setContentType( "text/plain" );
		}
		if (this.lastModified != -1L) {
			file.doSetLastModified( this.lastModified );
		}
		return Reply.string( "DAV", this.query, "Done." ).setCode( Reply.CD_OK );
	}
}
