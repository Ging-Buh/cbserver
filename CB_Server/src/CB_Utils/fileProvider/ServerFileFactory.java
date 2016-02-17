package CB_Utils.fileProvider;

/**
 * Created by Longri on 17.02.2016.
 */
public class ServerFileFactory extends FileFactory {
	@Override
	protected File createPlatformFile(String path) {
		return new ServerFile(path);
	}

	@Override
	protected File createPlatformFile(File parent) {
		return new ServerFile(parent);
	}

	@Override
	protected File createPlatformFile(File parent, String child) {
		return new ServerFile(parent, child);
	}

	@Override
	protected File createPlatformFile(String parent, String child) {
		return new ServerFile(parent, child);
	}
}
