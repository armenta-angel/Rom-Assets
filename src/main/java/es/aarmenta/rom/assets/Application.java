package es.aarmenta.rom.assets;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application implements CommandLineRunner {

	private static final Logger log = LoggerFactory.getLogger(Application.class);

	@Autowired
	private ApplicationConfig appConfig;

	// --------------------------------------------------------- Public Methods

	public static void main(String args[]) {
		SpringApplication.run(Application.class, args);
	}

	@Override
	public void run(String... strings) throws Exception {
		if (checkDirectory(appConfig.romsDir, "Roms dir") && checkDirectory(appConfig.assetsDir, "Assets dir")
				&& checkDirectory(appConfig.destRomsDir, "Dest roms dir")
				&& checkDirectory(appConfig.destAssetsDir, "Dest assets dir")) {
			processAssets();
		}
	}

	// -------------------------------------------------------- Private Methods

	private boolean checkDirectory(String directory, String whichDir) {
		boolean checkIsOk = true;

		if (directory.length() > 0) {
			log.info(whichDir + ": " + directory);
			File dirFile = new File(directory);
			if (!dirFile.exists()) {
				log.warn(whichDir + " does not exists");
				checkIsOk = false;
			} else if (!dirFile.isDirectory()) {
				log.warn(whichDir + " is not a directory");
				checkIsOk = false;
			}
		} else {
			log.warn(whichDir + " has not been configured");
			checkIsOk = false;
		}
		return checkIsOk;
	}

	private void processAssets() {
		File romsDir = new File(appConfig.romsDir);
		List<File> romFiles = listFileNames(romsDir);
		if (romFiles.size() <= 0) {
			log.warn("There are no rom files in the source directory");
			return;
		}

		File assetsDir = new File(appConfig.assetsDir);
		List<File> assetFiles = listFileNames(assetsDir);
		if (assetFiles.size() <= 0) {
			log.warn("There are no asset files in the source directory");
			return;
		}

		Report aReport = findMatches(romFiles, assetFiles);
		log.info(aReport.toString());
	}

	private Report findMatches(List<File> roms, List<File> assets) {
		Report aReport = new Report(roms.size());

		for (File romFile : roms) {
			String romName = romFile.getName().substring(0, romFile.getName().lastIndexOf('.'));
			boolean found = false;
			for (int i = 0; !found && i < assets.size(); i++) {
				File assetFile = assets.get(i);
				String assetName = assetFile.getName().substring(0, assetFile.getName().lastIndexOf('.'));

				if (romName.equals(assetName)) {
					log.debug("Perfect match: " + romFile.getName() + " with " + assetFile.getName());
					moveRomAndAsset(romFile, assetFile, false);
					found = true;
					assets.remove(i);
					aReport.addPerfectMatch();
				} else if (romName.equalsIgnoreCase(assetName)) {
					log.debug("Perfect match (ignore case): " + romFile.getName() + " with " + assetFile.getName());
					moveRomAndAsset(romFile, assetFile, true);
					found = true;
					assets.remove(i);
					aReport.addPerfectIgnoreCaseMatch();
				} else if (romAssetMatch(romName, assetName, true)) {
					log.debug("Rom match: " + romFile.getName() + " with " + assetFile.getName());
					moveRomAndAsset(romFile, assetFile, true);
					found = true;
					assets.remove(i);
					aReport.addRenameMatch();
				}
			}
		}

		return aReport;
	}

	private List<File> listFileNames(File dir) {
		List<File> theList = new ArrayList<File>();

		File list[] = dir.listFiles(new IsFileFilter());
		if (list == null || list.length == 0) {
			log.warn("There are no rom files in the source directory");
		} else {
			for (File file : list) {
				log.debug(file.getName());
				theList.add(file);
			}
		}

		return theList;
	}

	private boolean romAssetMatch(String romFilename, String assetFilename, boolean ignoreCase) {
		int indexRom = romFilename.indexOf('(');
		int indexAsset = assetFilename.indexOf('(');

		if (indexRom > 0) {
			romFilename = romFilename.substring(0, indexRom).trim();
		}
		if (indexAsset > 0) {
			assetFilename = assetFilename.substring(0, indexAsset).trim();
		}

		if (ignoreCase) {
			return romFilename.equalsIgnoreCase(assetFilename);
		} else {
			return (romFilename.compareTo(assetFilename) == 0);
		}

	}

	private void moveRomAndAsset(File rom, File asset, boolean renameAsset) {
		if (renameAsset) {
			String romName = rom.getName().substring(0, rom.getName().lastIndexOf('.'));
			String assetExt = asset.getName().substring(asset.getName().lastIndexOf('.'));
			String newAssetName = romName + assetExt;

			asset.renameTo(new File(appConfig.destAssetsDir + "/" + newAssetName));
		} else {
			asset.renameTo(new File(appConfig.destAssetsDir + "/" + asset.getName()));
		}

		rom.renameTo(new File(appConfig.destRomsDir + "/" + rom.getName()));
	}
}
