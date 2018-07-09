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

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import ru.myx.ae3.Engine;
import ru.myx.ae3.answer.Reply;
import ru.myx.ae3.base.BaseList;
import ru.myx.ae3.base.BaseObject;
import ru.myx.ae3.binary.TransferCopier;
import ru.myx.ae3.vfs.Entry;
import ru.myx.ae3.vfs.TreeReadType;

/**
 * This interface is part of our WebDAV server interface core. Namespace
 * Subsystem Providers must implement this interface.
 * 
 * @author Marc Eaddy
 * @version 2.0, 16 Nov 1997
 */
final class StorageManager {
	/** No error. */
	public static final int				NAMESPC_SUCCESS				= 200;
	
	/** The URI specified does not exist or is not readable. */
	public static final int				NAMESPC_URI_NOT_FOUND		= -1000404;
	
	/** An I/O exception was thrown unexpectedly. */
	public static final int				NAMESPC_UNKNOWN_ERROR		= -1001500;
	
	/**
	 * An error occurred when attempting to read the URI specified.
	 */
	public static final int				NAMESPC_URI_READ_ERROR		= -1002500;
	
	/**
	 * An error occurred when attempting to write the URI specified.
	 */
	public static final int				NAMESPC_URI_WRITE_ERROR		= -1003409;
	
	/**
	 * An error occurred when attempting to delete the URI specified
	 */
	public static final int				NAMESPC_URI_DELETE_ERROR	= -1004403;
	
	/** Read access it not allowed for the URI specified. */
	public static final int				NAMESPC_URI_ACCESS_DENIED	= -1007403;
	
	/**
	 * The operation requested is not supported for collections.
	 */
	public static final int				NAMESPC_COLL_NOT_SUPPORTED	= -1008501;
	
	/** The URI was null or contained illegal characters */
	public static final int				NAMESPC_URI_INVALID			= -1009400;
	
	/**
	 * An internal error occurred. See the system logs for more information.
	 */
	public static final int				NAMESPC_INTERNAL_ERROR		= -1010500;
	
	/**
	 * The destination already exists and the overwrite flag is false.
	 */
	public static final int				NAMESPC_NO_OVERWRITE		= -1011412;
	
	/** Could not open the URI for writing. */
	public static final int				NAMESPC_URI_NOT_WRITEABLE	= -1012403;
	
	/**
	 * Could not write the resource due to insufficient space on the server.
	 */
	public static final int				NAMESPC_NOT_ENOUGH_SPACE	= -1013419;
	
	/**
	 * Attempted to create a resource in a directory that does not exist.
	 */
	public static final int				NAMESPC_PARENT_NOT_FOUND	= -1014409;
	
	/**
	 * Cannot copy a resource unto itself.
	 */
	public static final int				NAMESPC_COPY_TO_SELF		= -1015409;
	
	/**
	 * The URI exists but is not a collection.
	 */
	public static final int				NAMESPC_URI_NOT_COLLECTION	= -1016409;
	
	/**
	 * The collection specified already exists.
	 */
	public static final int				NAMESPC_COLL_ALREADY_EXISTS	= -1017405;
	
	/**
	 * The destination resource was successfully created.
	 */
	public static final int				NAMESPC_RESOURCE_CREATED	= +1000201;
	
	/**
	 * The collection was created.
	 */
	public static final int				NAMESPC_COLLECTION_CREATED	= +1001201;
	
	private final Entry					folder;
	
	private final Map<String, DavLock>	locks						= new TreeMap<>();
	
	/**
	 * @param folder
	 */
	StorageManager(final Entry folder) {
		this.folder = folder;
	}
	
	/**
	 * Copies the resource.
	 * 
	 * @param uriSrc
	 *            [IN] Source resource to copy
	 * @param uriDest
	 *            [IN] Destination resource to copy to
	 * @param bOverwrite
	 *            [IN] Overwrite if the destination resource already exists
	 * @param depth
	 *            [IN] 0, 1, or infinity
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS - Success - dest overwritten
	 *         <LI>NAMESPC_RESOURCE_CREATED - Success - dest created
	 *         <LI>NAMESPC_NO_OVERWRITE - Dest already exists
	 *         <LI>NAMESPC_UNKNOWN_ERROR - Unknown error
	 *         <LI>NAMEPSC_INTERNAL_ERROR - Internal error
	 *         <LI>NAMESPC_URI_INVALID - Src or Dest uri is null
	 *         <LI>NAMESPC_URI_NOT_FOUND - Src uri not found
	 *         <LI>NAMESPC_URI_READ_ERROR - Failed to read src
	 *         <LI>NAMESPC_URI_NOT_WRITEABLE - Failed to write dest
	 *         <LI>NAMESPC_URI_WRITE_ERROR - Failed to write dest
	 *         <LI>NAMESPC_NOT_ENOUGH_SPACE - Failed because disk is full
	 *         <LI>NAMESPC_PARENT_NOT_FOUND - Parent dir does not exist
	 *         <LI>NAMESPC_COLL_NOT_SUPPORTED - Can't copy a collection
	 *         <LI>NAMESPC_COPY_TO_SELF - Src and Dest uri are same
	 *         </UL>
	 */
	public int copyResource(final Uri uriSrc, final Uri uriDest, final boolean bOverwrite, final Depth depth) // Ignoring
	// for
	// now...
	{
		if (this.isEqual( uriSrc, uriDest )) {
			return StorageManager.NAMESPC_COPY_TO_SELF;
		}
		if (this.isDirectory( uriSrc )) {
			return this.recFolderCopy( uriSrc, uriDest, bOverwrite, depth );
		}
		final TransferCopier binary = this.getResource( uriSrc, false );
		if (binary == null) {
			return StorageManager.NAMESPC_URI_NOT_FOUND;
		}
		if (this.isDirectory( uriDest )) {
			if (bOverwrite) {
				this.deleteResource( uriDest );
				final int result = this.putResource( uriDest, binary, bOverwrite );
				if (result / 100 == 2) {
					return Reply.CD_EMPTY;
				}
				return result;
			}
			return StorageManager.NAMESPC_NO_OVERWRITE;
		}
		return this.putResource( uriDest, binary, bOverwrite );
	}
	
	/**
	 * Deletes the resource.
	 * 
	 * @param uri
	 *            [IN] URI of the resource to delete
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS
	 *         <LI>NAMESPC_URI_INVALID
	 *         <LI>NAMESPC_URI_DELETE_ERROR
	 *         <LI>NAMESPC_UNKNOWN_ERROR
	 *         <LI>NAMEPSC_INTERNAL_ERROR
	 *         </UL>
	 */
	public int deleteResource(final Uri uri) {
		if (uri == null) {
			return StorageManager.NAMESPC_URI_INVALID;
		}
		final Entry file = uri.getFile( this.folder, false );
		if (file == null || !file.isExist()) {
			return StorageManager.NAMESPC_SUCCESS;
		}
		if (file.isContainer()) {
			if (this.recFolderDelete( file )) {
				return StorageManager.NAMESPC_SUCCESS;
			}
			return StorageManager.NAMESPC_URI_DELETE_ERROR;
		}
		if (file.doUnlink().baseValue().booleanValue()) {
			return StorageManager.NAMESPC_SUCCESS;
		}
		return StorageManager.NAMESPC_URI_DELETE_ERROR;
	}
	
	public DavLock getLock(final Uri uri, final BaseObject lockData) {
		final String uriString = uri.toString();
		final DavLock existing = this.locks.get( uriString );
		if (existing != null) {
			return existing;
		}
		final DavLock created = new DavLock( "opaquelocktoken:" + Engine.createGuid(), lockData );
		this.locks.put( uriString, created );
		return created;
	}
	
	/**
	 * Retrieves the resource.
	 * 
	 * @param uri
	 *            [IN] URI of the resource
	 * @param create
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS
	 *         <LI>NAMESPC_UNKNOWN_ERROR
	 *         <LI>NAMEPSC_INTERNAL_ERROR
	 *         <LI>NAMESPC_URI_INVALID
	 *         <LI>NAMESPC_URI_NOT_FOUND
	 *         <LI>NAMESPC_URI_READ_ERROR
	 *         </UL>
	 */
	public TransferCopier getResource(final Uri uri, final boolean create) {
		final Entry file = uri.getFile( this.folder, create );
		return file == null || !file.isExist() || !file.isBinary()
				? null
				: file.toBinary().getBinaryContent().baseValue();
	}
	
	/**
	 * @param uri
	 * @param create
	 * @return entry
	 */
	public Entry getResourceFile(final Uri uri, final boolean create) {
		return uri.getFile( this.folder, create );
	}
	
	private boolean isDirectory(final Uri uri) {
		if (uri == null) {
			return false;
		}
		final Entry file = uri.getFile( this.folder, false );
		return file != null && file.isExist() && file.isContainer();
	}
	
	private boolean isEqual(final Uri uriA, final Uri uriB) {
		if (uriA == null || uriB == null) {
			return false;
		}
		final Entry fileA = uriA.getFile( this.folder, false );
		final Entry fileB = uriB.getFile( this.folder, false );
		return fileA == null && fileB == null || fileA != null && fileB != null && fileA.equals( fileB );
	}
	
	/**
	 * Determine if the URI is valid for the underlying system. This function
	 * should be called before any operations are attempted for this URI.
	 * 
	 * @param uri
	 *            [IN] URI of the resource.
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS
	 *         <LI>NAMESPC_UNKNOWN_ERROR
	 *         <LI>NAMEPSC_INTERNAL_ERROR
	 *         <LI>NAMESPC_URI_INVALID
	 *         <LI>NAMESPC_URI_NOT_FOUND
	 *         </UL>
	 */
	public int isValid(final Uri uri) {
		if (uri == null) {
			return StorageManager.NAMESPC_URI_INVALID;
		}
		final Entry entry = uri.getFile( this.folder, false );
		return entry != null && entry.isExist()
				? StorageManager.NAMESPC_SUCCESS
				: StorageManager.NAMESPC_URI_NOT_FOUND;
	}
	
	/**
	 * Lists the members of a collection.
	 * 
	 * @param uri
	 *            [IN] URI of the collection
	 * @param result
	 *            [OUT] Unordered list of the members of the collection
	 * @param depth
	 * @see CollectionMember
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS
	 *         <LI>NAMESPC_URI_INVALID
	 *         <LI>NAMESPC_URI_NOT_COLLECTION
	 *         </UL>
	 */
	@SuppressWarnings("javadoc")
	public int listCollection(final Uri uri, final List<CollectionMember> result, final Depth depth) {
		final Entry file = uri.getFile( this.folder, false );
		if (file != null && file.isExist()) {
			if (depth == null || depth.getValue() != Depth.NOROOT) {
				result.add( new CollectionMember( file, uri ) );
			}
			if (depth == null || depth.getValue() != Depth.ZERO && depth.getValue() != Depth.INVALID) {
				final BaseList<Entry> members = file.toContainer().getContentCollection( null ).baseValue();
				if (members != null) {
					final String string = uri.toString();
					final String prefix = string.endsWith( "/" )
							? string
							: string + '/';
					for (int i = members.length() - 1; i >= 0; --i) {
						final Entry entry = members.get( i );
						final Uri uriNew = new Uri( prefix + entry.getKey() );
						result.add( new CollectionMember( entry, uriNew ) );
					}
				}
			}
		}
		return StorageManager.NAMESPC_SUCCESS;
	}
	
	/**
	 * Creates a new collection.
	 * 
	 * @param uri
	 *            [IN] URI of the collection
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_URI_INVALID
	 *         <LI>NAMESPC_COLL_ALREADY_EXISTS
	 *         <LI>NAMESPC_UNKNOWN_ERROR
	 *         <LI>NAMESPC_COLLECTION_CREATED
	 *         </UL>
	 */
	public int makeCollection(final Uri uri) {
		final Entry file = uri.getFile( this.folder, true );
		if (file.isExist()) {
			return StorageManager.NAMESPC_COLL_ALREADY_EXISTS;
		}
		if (!file.doSetContainer().baseValue().booleanValue()) {
			return StorageManager.NAMESPC_UNKNOWN_ERROR;
		}
		return StorageManager.NAMESPC_COLLECTION_CREATED;
	}
	
	/**
	 * Moves the resource.
	 * 
	 * @param uriSrc
	 *            [IN] Source resource to move
	 * @param uriDest
	 *            [IN] Destination resource to move to
	 * @param bOverwrite
	 *            [IN] Overwrite if the destination resource already exists
	 * @param depth
	 *            [IN] 0, 1, or infinity
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS - Success - dest overwritten
	 *         <LI>NAMESPC_RESOURCE_CREATED - Success - dest created
	 *         <LI>NAMESPC_NO_OVERWRITE - Dest already exists
	 *         <LI>NAMESPC_UNKNOWN_ERROR - Unknown error
	 *         <LI>NAMEPSC_INTERNAL_ERROR - Internal error
	 *         <LI>NAMESPC_URI_INVALID - Src or Dest uri is null
	 *         <LI>NAMESPC_URI_NOT_FOUND - Src uri not found
	 *         <LI>NAMESPC_URI_READ_ERROR - Failed to read src
	 *         <LI>NAMESPC_URI_NOT_WRITEABLE - Failed to write dest
	 *         <LI>NAMESPC_URI_WRITE_ERROR - Failed to write dest
	 *         <LI>NAMESPC_NOT_ENOUGH_SPACE - Failed because disk is full
	 *         <LI>NAMESPC_PARENT_NOT_FOUND - Parent dir does not exist
	 *         <LI>NAMESPC_COLL_NOT_SUPPORTED - Can't copy a collectino
	 *         <LI>NAMESPC_COPY_TO_SELF - Src and Dest uri are same
	 *         <LI>NAMESPC_URI_DELETE_ERROR - Could not delete src uri
	 *         </UL>
	 */
	public int moveResource(final Uri uriSrc, final Uri uriDest, final boolean bOverwrite, final Depth depth) {
		final int nCopyReturn = this.copyResource( uriSrc, uriDest, bOverwrite, depth );
		if (nCopyReturn < StorageManager.NAMESPC_SUCCESS) {
			return nCopyReturn;
		}
		final int nDeleteReturn = this.deleteResource( uriSrc );
		if (nDeleteReturn < StorageManager.NAMESPC_SUCCESS) {
			return nDeleteReturn;
		}
		// We aren't concerned with delete success status codes
		return nCopyReturn;
	}
	
	/**
	 * Overwrites the resource.
	 * 
	 * @param uri
	 *            [IN] Uri of the resource
	 * @param copier
	 * @param bOverwrite
	 *            [IN] Whether or not to overwrite the resource if it already
	 *            exists
	 * 
	 * @return One of the following status codes:
	 *         <P>
	 *         <UL>
	 *         <LI>NAMESPC_SUCCESS - Success - URI overwritten
	 *         <LI>NAMESPC_RESOURCE_CREATED - Success - URI created
	 *         <LI>NAMESPC_NO_OVERWRITE - Resource already exists
	 *         <LI>NAMESPC_UNKNOWN_ERROR - Unknown error
	 *         <LI>NAMEPSC_INTERNAL_ERROR - Internal error
	 *         <LI>NAMESPC_URI_INVALID - URI is null
	 *         <LI>NAMESPC_URI_NOT_WRITEABLE - Failed to write URI
	 *         <LI>NAMESPC_URI_WRITE_ERROR - Failed to write URI
	 *         <LI>NAMESPC_NOT_ENOUGH_SPACE - Failed because disk is full
	 *         <LI>NAMESPC_COLL_NOT_SUPPORTED - Can't put to a collection
	 *         <LI>NAMESPC_PARENT_NOT_FOUND - Parent dir does not exist
	 *         </UL>
	 */
	public int putResource(final Uri uri, final TransferCopier copier, final boolean bOverwrite) {
		final boolean bDestAlreadyExists = this.isValid( uri ) == StorageManager.NAMESPC_SUCCESS;
		// Open the file for writing
		if (uri == null) {
			return StorageManager.NAMESPC_URI_INVALID;
		}
		if (bDestAlreadyExists) {
			if (!bOverwrite) {
				return StorageManager.NAMESPC_NO_OVERWRITE;
			}
			if (this.isDirectory( uri )) {
				return StorageManager.NAMESPC_COLL_NOT_SUPPORTED;
			}
		}
		try {
			final Entry entry = uri.getFile( this.folder, true );
			entry.doSetBinary( copier );
		} catch (final Exception ex) {
			return StorageManager.NAMESPC_URI_WRITE_ERROR;
		}
		if (bDestAlreadyExists) {
			return Reply.CD_EMPTY;
		}
		return Reply.CD_CREATED;
	}
	
	private final int recFolderCopy(final Uri uriSrc, final Uri uriDest, final boolean bOverwrite, final Depth depth) {
		final Entry src = this.getResourceFile( uriSrc, false );
		if (src == null) {
			return StorageManager.NAMESPC_URI_NOT_FOUND;
		}
		final Entry dst = this.getResourceFile( uriDest, true );
		if (dst == null) {
			return StorageManager.NAMESPC_COLL_ALREADY_EXISTS;
		}
		if (dst.isExist() && dst.isBinary()) {
			if (!bOverwrite) {
				return StorageManager.NAMESPC_COLL_ALREADY_EXISTS;
			}
			dst.doUnlink();
		}
		if (!dst.isExist()) {
			dst.doSetContainer();
		}
		final BaseList<Entry> entries = src.toContainer().getContentCollection( null ).baseValue();
		if (entries != null) {
			for (final Entry entry : entries) {
				final Uri entryUriSrc = new Uri( uriSrc.toString() + '/' + entry.getKey() );
				final Uri entryUriDst = new Uri( uriDest.toString() + '/' + entry.getKey() );
				if (entry.isContainer()) {
					final int result = this.recFolderCopy( entryUriSrc, entryUriDst, bOverwrite, depth );
					if (result != StorageManager.NAMESPC_SUCCESS) {
						return result;
					}
				} else {
					final TransferCopier binary = entry.toBinary().getBinaryContent().baseValue();
					if (binary == null) {
						return StorageManager.NAMESPC_URI_NOT_FOUND;
					}
					final int result = this.putResource( entryUriDst, binary, bOverwrite );
					if (result / 100 != 2) {
						return result;
					}
				}
			}
		}
		return StorageManager.NAMESPC_SUCCESS;
	}
	
	private final boolean recFolderDelete(final Entry folder) {
		final BaseList<Entry> children = folder.toContainer().getContentCollection( TreeReadType.ANY ).baseValue();
		if (children != null) {
			for (final Entry entry : children) {
				if (entry.isContainer()) {
					if (!this.recFolderDelete( entry )) {
						return false;
					}
				} else {
					if (!entry.doUnlink().baseValue().booleanValue()) {
						return false;
					}
				}
			}
		}
		return folder.doUnlink().baseValue().booleanValue();
	}
}
