package ru.myx.ae3impl.vfs;

import java.io.InputStream;

import ru.myx.ae3.Engine;
import java.util.function.Function;
import ru.myx.ae3.binary.Transfer;
import ru.myx.ae3.binary.TransferBuffer;

/**
 * @author myx
 *
 */
public final class FolderJavaResources implements Function<String, String> {
	
	private final Class<?> root;

	/**
	 * @param root
	 */
	public FolderJavaResources(final Class<?> root) {
		this.root = root;
	}

	@Override
	public final String apply(final String name) {
		
		final InputStream stream;
		try {
			stream = this.root.getResourceAsStream(name);
		} catch (final Throwable t) {
			return null;
		}
		if (stream == null) {
			return null;
		}
		final TransferBuffer buffer = Transfer.createBuffer(stream);
		return buffer.toString(Engine.CHARSET_UTF8);
	}
}
