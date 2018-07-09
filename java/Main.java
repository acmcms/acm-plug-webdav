import ru.myx.ae3.produce.Produce;
import ru.myx.ae3impl.server.ServerFactoryDAV;
import ru.myx.ae3impl.skinner.SkinnerFactoryDAV;

/**
 * @author myx
 * 
 */
public final class Main {
	
	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		System.out.println( "BOOT: WebDAV services set up..." );
		Produce.registerFactory( new ServerFactoryDAV() );
		Produce.registerFactory( new SkinnerFactoryDAV() );
		System.out.println( "BOOT: WebDAV OK" );
	}
	
}
