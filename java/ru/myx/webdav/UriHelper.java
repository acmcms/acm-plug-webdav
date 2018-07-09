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

/**
 * The UriHelper defines some static helper functions for creating some XML
 * elements and HTML using a uri.
 * 
 * @see Uri
 * @author Marc Eaddy
 * @version 1.0, 8 Nov 1997
 */
@SuppressWarnings("javadoc")
final class UriHelper {
	static final Element createHrefElement(final String str) {
		final Element element = WebdavXML.createElement( "href" );
		element.appendChild( WebdavXML.createTextNode( str ) );
		return element;
	}
	
	static final Element createHrefElement(final Uri uri, final boolean folder) {
		return UriHelper.createHrefElement( UriHelper.fixUri( uri.toString(), folder ) );
	}
	
	static final String createHrefHTML(final String strLink, final String strText) {
		return "<A HREF=\"" + strLink + "\">" + strText + "</A>";
	}
	
	static final String fixUri(final String uri, final boolean folder) {
		return folder
				? !uri.endsWith( "/" )
						? uri + '/'
						: uri
				: uri.endsWith( "/" ) && uri.length() > 0
						? uri.substring( 0, uri.length() - 2 )
						: uri;
	}
}
