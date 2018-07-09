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

import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.TreeLinkType;

/**
 * Wraps a uri to insulate us from changing standards for URLs, URIs and URNs.
 * As specified in RFC1738, the path separator for a Uri is the '/' character.
 * However, when determining the mapping between a Uri for a resource and the
 * location of that resource on the file server, the getFullPath() function will
 * prepend the doc root to the Uri and convert the path separator as necessary.
 * The doc root is obtained by retrieving the "server.root" system property, and
 * appending "public_html" to it.
 * 
 * <P>
 * For example, if the server.root property is "D:\Projects\JavaWebServer" and
 * the Uri is "/testing/index.html", and the file server requires path
 * separators to be '\', calling getFullPath() will return
 * "D:\Projects\JavaWebServer\public_html\testing\index.html".
 * 
 * @author Marc Eaddy
 * @version 2.0, 3 Nov 1997
 */
class Uri {
	
	private final String strUri;
	
	private Entry file;
	
	Uri(final String strUri) {
		this.strUri = strUri == null
			? null
			: strUri.replace("\\", "/");
	}
	
	Entry getFile(final Entry rootFolder, final boolean create) {
		
		if (this.file != null) {
			return this.file;
		}
		if (this.strUri == null) {
			return null;
		}
		if (this.strUri.length() <= 1) {
			return this.file = rootFolder;
		}
		final String uReal = this.strUri.substring(1);
		final Entry file = rootFolder.relative(uReal, create
			? TreeLinkType.PUBLIC_TREE_REFERENCE
			: null);
		return file != null && file.isExist()
			? this.file = file
			: file;
	}
	
	@Override
	public String toString() {
		
		return this.strUri;
	}
}
