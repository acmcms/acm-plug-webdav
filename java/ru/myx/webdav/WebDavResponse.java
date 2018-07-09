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

import org.w3c.dom.Element;

import ru.myx.ae3.answer.Reply;
import ru.myx.util.HashMapPrimitiveInt;

/** Wraps the HttpServletResponse class to abstract the specific protocol used. To support other
 * protocols we would only need to modify this class and the WebDavRetCode classes.
 *
 * @see WebDavRetCode
 * @author Marc Eaddy
 * @version 1.0, 16 Nov 1997 */
@SuppressWarnings("javadoc")
class WebDavResponse {
	
	/** This Map contains the mapping of HTTP and WebDAV status codes to descriptive text. This is a
	 * static variable. */
	private static final HashMapPrimitiveInt<String> mapStatusCodes = new HashMapPrimitiveInt<>();

	/** Status code (413) indicating the server is refusing to process a request because the request
	 * entity is larger than the server is willing or able to process. */
	public static final int SC_REQUEST_TOO_LONG = 413;

	/** Status code (418) indicating the entity body submitted with the PATCH method was not
	 * understood by the resource. */
	public static final int SC_UNPRCESSABLE_ENTITY = 418; // This
	
	// one
	// colides
	// with
	// HTTP
	// 1.1
	// "418 Reauthentication Required"
	/** Status code (419) indicating that the resource does not have sufficient space to record the
	 * state of the resource after the execution of this method. */
	public static final int SC_INSUFFICIENT_SPACE_ON_RESOURCE = 419; // This
	
	// one
	// colides
	// with
	// HTTP
	// 1.1
	// "419 Proxy Reauthentication Required"
	/** Status code (420) indicating the method was not executed on a particular resource within its
	 * scope because some part of the method's execution failed causing the entire method to be
	 * aborted. */
	public static final int SC_METHOD_FAILURE = 420;

	/** Status code (423) indicating the destination resource of a method is locked, and either the
	 * request did not contain a valid Lock-Info header, or the Lock-Info header identifies a lock
	 * held by another principal. */
	public static final int SC_LOCKED = 423;

	static {
		final HashMapPrimitiveInt<String> codes = WebDavResponse.mapStatusCodes;

		// HTTP 1.0 Server status codes -- see RFC 1945
		codes.put(Reply.CD_OK, "OK");
		codes.put(Reply.CD_CREATED, "Created");
		codes.put(Reply.CD_ACCEPTED, "Accepted");
		codes.put(Reply.CD_EMPTY, "No Content");
		codes.put(Reply.CD_MOVED, "Moved Permanently");
		codes.put(Reply.CD_LOOKAT, "Moved Temporarily");
		codes.put(Reply.CD_UNMODIFIED, "Not Modified");
		codes.put(Reply.CD_BADQUERY, "Bad Request");
		codes.put(Reply.CD_UNAUTHORIZED, "User Authentication Required");
		codes.put(Reply.CD_DENIED, "Forbidden");
		codes.put(Reply.CD_UNKNOWN, "Not Found");
		codes.put(Reply.CD_EXCEPTION, "Internal Server Error");
		codes.put(Reply.CD_UNIMPLEMENTED, "Not Implemented");
		codes.put(Reply.CD_BADGATEWAY, "Bad Gateway");
		codes.put(Reply.CD_BUSY, "Service Unavailable");

		// HTTP 1.1 Server status codes -- see RFC 2048
		codes.put(Reply.CD_CONTINUE, "Continue");
		codes.put(Reply.CD_BADMETHOD, "Method Not Allowed");
		codes.put(Reply.CD_CONFLICT, "Conflict");
		codes.put(Reply.CD_FAILED_PRECONDITION, "Precondition Failed");
		codes.put(WebDavResponse.SC_REQUEST_TOO_LONG, "Request Too Long");
		codes.put(Reply.CD_UNSUPPORTED_FORMAT, "Unsupported Media Type");
		// WebDav Server-specific status codes
		codes.put(Reply.CD_MULTISTATUS, "Multi-Status");
		codes.put(WebDavResponse.SC_UNPRCESSABLE_ENTITY, "Unprocessable Entity");
		codes.put(WebDavResponse.SC_INSUFFICIENT_SPACE_ON_RESOURCE, "Insufficient Space On Resource");
		codes.put(WebDavResponse.SC_METHOD_FAILURE, "Method Failure");
		codes.put(WebDavResponse.SC_LOCKED, "Locked");
	}

	static Element createHttpStatusElement(final int nHttpStatusCode) {
		
		final String strStatusText = "HTTP/1.1 " + nHttpStatusCode + " " + WebDavResponse.getStatusText(nHttpStatusCode);
		final Element element = WebdavXML.createElement("status");
		element.appendChild(WebdavXML.createTextNode(strStatusText));
		return element;
	}

	/** Returns the HTTP status text for the HTTP or WebDav status code specified by looking it up
	 * in the static mapping. This is a static function.
	 *
	 * @param nHttpStatusCode
	 *            [IN] HTTP or WebDAV status code
	 * @return A string with a short descriptive phrase for the HTTP status code (e.g., "OK"). */
	static String getStatusText(final int nHttpStatusCode) {
		
		return nHttpStatusCode != Reply.CD_EXCEPTION && !WebDavResponse.mapStatusCodes.containsKey(nHttpStatusCode)
			? WebDavResponse.getStatusText(Reply.CD_EXCEPTION)
			: WebDavResponse.mapStatusCodes.get(nHttpStatusCode);
	}
}
