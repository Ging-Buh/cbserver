package cb_server.DB;

import de.cb.sqlite.AlternateDatabase;
import de.cb.sqlite.DatabaseFactory;
import de.cb.sqlite.SQLite;

public class CBServerDatabaseFactory extends DatabaseFactory {
	public CBServerDatabaseFactory() {
		super();
	}

	@Override
	protected SQLite createInstanz(String Path, AlternateDatabase alter) {
		try {
			return new CBServerDB(Path, alter);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		return null;
	}

}
