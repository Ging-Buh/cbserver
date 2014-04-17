package cb_server.Import;

import CB_Core.Import.ImporterProgress;

public class ServerImporterProgress extends ImporterProgress {
	@Override
	public void ProgressInkrement(String Name, String Msg, Boolean Done) {
		super.ProgressInkrement(Name, Msg, Done);
		System.out.println("ProgressIncrement: " + Name + "(" + getProgress() + "%)" + " - " + Msg + " - Done: " + Done);
	}
	
	@Override
	public void ProgressChangeMsg(String Name, String Msg) {
		super.ProgressChangeMsg(Name, Msg);
		System.out.println("ProgressChangeMsg: " + Name + "(" + getProgress() + "%)" + "- " + Msg);
	}
}
