using Avalonia.Platform.Storage;

namespace WorldsFactory.world;

public class WorldCreator
{
	public static void Create(IStorageFolder worldFolder, IStorageProvider storageProvider)
	{
		var libFolder = worldFolder.CreateFolderAsync("Library").Result;
		var eventsFolder = worldFolder.CreateFolderAsync("Events").Result;
		var objectsFolder = worldFolder.CreateFolderAsync("Objects").Result;
		var visualFolder = worldFolder.CreateFolderAsync("Visualizations").Result;
		if(libFolder is null)
		{
			throw new CannotCreateFolderException("Cannot create Library folder.");
		}
		if(eventsFolder is null){
			throw new CannotCreateFolderException("Cannot create Events folder.");
		}
		if(objectsFolder is null){
			throw new CannotCreateFolderException("Cannot create Objects folder.");
		}
		if(visualFolder is null){
			throw new CannotCreateFolderException("Cannot create Visualizations folder.");
		}

		var classesFolder = libFolder.CreateFolderAsync("Classes").Result;
		if(classesFolder is null){
			throw new CannotCreateFolderException("Cannot create Classes folder.");
		}

		var methodsFolder = libFolder.CreateFolderAsync("Methods").Result;
		if(methodsFolder is null){
			throw new CannotCreateFolderException("Cannot create Methods folder.");
		}
	}
}
