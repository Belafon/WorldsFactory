using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using Serilog;
using WorldsFactory.project;
using WorldsFactory.world.objects;
using WorldsFactory.world.events;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world;
public class WorldLoader : ILibraryLoader
{
	internal IStorageProvider storageProvider;
	internal ITagManager tagManager;
	internal IStorageFolder? projectFolder;
	internal IStorageFolder? worldFolder;
	public IStorageFolder? LibraryFolder { get; private set; }
	internal IStorageFolder? objectsFolder;
	public IStorageFolder? EventsFolder;
	public IStorageFolder? EventContainersFolder;
	
	internal IStorageFolder? visualizationsFolder;
	public IStorageFolder? ClassesFolder { get; private set; }
	public IStorageFolder? MethodsFolder { get; private set; }

	public WorldLoader(IStorageProvider storageProvider, ITagManager tagManager)
	{
		this.storageProvider = storageProvider;
		this.tagManager = tagManager;
	}
	public World? Load(ProjectReference projectReference)
	{
		try
		{
			Task.Run(
				async () => await verifyWorldStructure(projectReference)
			).Wait();
		}
		catch (AggregateException e)
		{
			Log.Error(e, "Error while verifying world structure.");
			if (e.InnerException is not null)
				throw e.InnerException;
			else throw e;
		}


		IDConceptManager idConceptManager = new IDConceptManager();

		ILibrary library = null!;
		try
		{
			library = Task.Run(
				async () => await loadLibrary(idConceptManager)
			).Result;
		}
		catch (AggregateException e)
		{
			Log.Error(e, "Error while loading library.");
			if (e.InnerException is not null)
				throw e.InnerException;
			else throw e;
		}

		var objectsLoader = new ObjectLoader(objectsFolder!, idConceptManager, tagManager);
		IObjects objects = new Objects(objectsLoader);

		var eventsLoader = new EventLoader(EventsFolder!, idConceptManager, tagManager);
		var EventContainersLoader = new EventContainersLoader(idConceptManager, tagManager, EventContainersFolder!);
		IEvents events = new Events(eventsLoader, EventContainersLoader);

		return new World(library, objects, events, idConceptManager, tagManager, this);
	}

	private async Task<ILibrary> loadLibrary(IDConceptManager iDConceptManager)
	{
		ILibrary library = new Library(iDConceptManager, tagManager, this);
		await foreach (IStorageItem item in ClassesFolder!.GetItemsAsync())
		{
			if (!(item is IStorageFile classFile))
				continue;

			Log.Information("Loading class {0}.", classFile.Name);

			string fileContent = await readFileContentAsync(classFile);
			
			var classConverter = new ClassConverter(tagManager, (IDClassesManager)library.IdManager, classFile, this);
			Class? deserializedClass = deserializeClass(library, classFile, fileContent, classConverter);

			if (deserializedClass == null)
			{
				Log.Error("Deserialized class {0} is null.", classFile.Name);
				continue;
			}
			library.Classes.Add(deserializedClass);
		}
		return library;
	}

	private static async Task<string> readFileContentAsync(IStorageFile classFile)
	{
		await using var stream = await classFile.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		return streamReader.ReadToEnd();
	}

	private static Class? deserializeClass(ILibrary library, IStorageFile classFile, string fileContent, ClassConverter classConverter)
	{
		Class? deserializedClass;
		var errors = new List<string>();


		deserializedClass = JsonConvert.DeserializeObject<Class>(fileContent, new JsonSerializerSettings
		{
			Converters = {
						classConverter,
						new WFTypeConverter(library.IdManager),
						new ClassReferenceConverter(library.IdManager)
					},
			Error = (sender, errorArgs) =>
			{
				errors.Add(errorArgs.ErrorContext.Error.Message);
				errorArgs.ErrorContext.Handled = true;
			},
		});

		if (errors.Any())
		{
			Log.Error("Errors while deserializing class {0}: {1}", classFile.Name, string.Join(", ", errors));
		}

		return deserializedClass;
	}

	internal async Task verifyWorldStructure(ProjectReference projectReference)
	{
		if (!Path.Exists(projectReference.Path))
		{
			throw new InvalidWorldStructureException("Project directory does not exist.");
		}

		Uri projectFolderUri = new Uri(projectReference.Path);
		projectFolder = await storageProvider.TryGetFolderFromPathAsync(projectFolderUri.AbsolutePath);

		if (projectFolder is null)
		{
			throw new InvalidWorldStructureException("Project directory does not exist.");
		}

		Uri worldFolderUri = new Uri(Path.Combine(projectFolderUri.AbsolutePath, "World"));
		worldFolder = await storageProvider.TryGetFolderFromPathAsync(worldFolderUri);

		if (worldFolder is null)
		{
			throw new InvalidWorldStructureException("World directory does not exist.");
		}

		Uri libraryFolderUri = new Uri(Path.Combine(worldFolderUri.AbsolutePath, "Library"));
		LibraryFolder = await storageProvider.TryGetFolderFromPathAsync(libraryFolderUri.AbsolutePath);
		if (LibraryFolder is null)
		{
			throw new InvalidWorldStructureException("Library directory does not exist.");
		}
		validateLibraryStructure(LibraryFolder, libraryFolderUri, storageProvider);

		Uri eventsFolderUri = new Uri(Path.Combine(worldFolderUri.AbsolutePath, "Events"));
		EventsFolder = await storageProvider.TryGetFolderFromPathAsync(eventsFolderUri.AbsolutePath);
		if (EventsFolder is null)
		{
			throw new InvalidWorldStructureException("Events directory does not exist.");
		}
		
		Uri eventContainersFolderUri = new Uri(Path.Combine(eventsFolderUri.AbsolutePath, "EventContainers"));
		EventContainersFolder = await storageProvider.TryGetFolderFromPathAsync(eventContainersFolderUri.AbsolutePath);
		if (EventContainersFolder is null)
		{
			Log.Error("EventContainers directory does not exist.");
			EventContainersFolder = await createNewFolder(EventsFolder, "EventContainers");
		}

		
		Uri objectsFolderUri = new Uri(Path.Combine(worldFolderUri.AbsolutePath, "Objects"));
		objectsFolder = await storageProvider.TryGetFolderFromPathAsync(objectsFolderUri.AbsolutePath);
		if (objectsFolder is null)
		{
			throw new InvalidWorldStructureException("Objects directory does not exist.");
		}

		Uri visualizationsFolderUri = new Uri(Path.Combine(worldFolderUri.AbsolutePath, "Visualizations"));
		visualizationsFolder = await storageProvider.TryGetFolderFromPathAsync(visualizationsFolderUri.AbsolutePath);
		if (visualizationsFolder is null)
		{
			throw new InvalidWorldStructureException("Visualizations directory does not exist.");
		}

		Uri methodsFolderUri = new Uri(Path.Combine(libraryFolderUri.AbsolutePath, "Methods"));
		MethodsFolder = await storageProvider.TryGetFolderFromPathAsync(methodsFolderUri.AbsolutePath);
		if (MethodsFolder is null)
		{
			throw new InvalidWorldStructureException("Methods directory does not exist.");
		}
	}

	private async Task<IStorageFolder> createNewFolder(IStorageFolder parent, string name)
	{
		try
		{
			return await parent.CreateFolderAsync(name);
		}
		catch (Exception e)
		{
			Log.Error(e, "Error while creating EventContainers folder.");
			throw new CannotCreateFolderException("Error while creating EventContainers folder.");
		}
	}

	internal async void validateLibraryStructure(IStorageFolder libraryFolder, Uri libraryFolderUri, IStorageProvider storageProvider)
	{
		Uri classesFolderUri = new Uri(Path.Combine(libraryFolderUri.AbsolutePath, "Classes"));
		ClassesFolder = await storageProvider.TryGetFolderFromPathAsync(classesFolderUri);
		if (ClassesFolder is null)
		{
			throw new InvalidWorldStructureException("Classes directory does not exist.");
		}
	}
}

public class CannotCreateFolderException : Exception
{
	public CannotCreateFolderException(string message) : base(message)
	{
	}
}

public class CannotCreateFileException : Exception
{
	public CannotCreateFileException(string message) : base(message)
	{
	}
}

public class InvalidClassStructure : Exception
{
	public InvalidClassStructure(string message) : base(message)
	{
	}
}

public class InvalidWorldStructureException : Exception
{
	public InvalidWorldStructureException(string message) : base(message)
	{
	}
}