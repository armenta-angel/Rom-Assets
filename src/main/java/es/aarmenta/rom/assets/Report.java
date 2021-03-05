package es.aarmenta.rom.assets;

public class Report {
	int totalRoms;
	int perfectMatches;
	int perfectIgnoreCaseMatches;
	int assetRenameMatches;

	public Report(int totalRoms) {
		super();

		this.totalRoms = totalRoms;
		perfectMatches = 0;
		perfectIgnoreCaseMatches = 0;
		assetRenameMatches = 0;
	}

	public void addPerfectMatch() {
		perfectMatches++;
	}
	
	public void addPerfectIgnoreCaseMatch() {
		perfectIgnoreCaseMatches++;
	}
	
	public void addRenameMatch() {
		assetRenameMatches++;
	}
	
	@Override
	public String toString() {
		return "Report [totalRoms=" + totalRoms + ", perfectMatches=" + perfectMatches + ", perfectIgnoreCaseMatches="
				+ perfectIgnoreCaseMatches + ", assetRenameMatches=" + assetRenameMatches + ", noMatch=" + calcNoMatch()
				+ "]";
	}

	private int calcNoMatch() {
		return totalRoms - perfectMatches - perfectIgnoreCaseMatches - assetRenameMatches;
	}
}
