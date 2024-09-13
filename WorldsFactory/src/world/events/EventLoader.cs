using System.Collections.ObjectModel;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure;
using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure.types;
using NP.Utilities;
using WorldsFactory.world.events.eventContainer;

namespace WorldsFactory.world.events;

public class EventLoader : IEventLoader
{
	private const string JSON_FILE_POSTFIX = ".json";

	public IStorageFolder MethodsFolder { get; private set; }
	public IStorageFolder LibraryFolder { get; private set; }
	public IStorageFolder? ClassesFolder { get; private set; }
	public IIDConceptManager IdManager { get; private set; }
	public IIDConceptManager IdEventsManager { get; private set; }
	public ITagManager TagManager { get; private set; }

	public EventLoader(IStorageFolder eventsFolder, IIDConceptManager idManager, ITagManager tagManager)
	{
		LibraryFolder = eventsFolder;
		IdManager = idManager;
		IdEventsManager = new IDClassesManager(idManager);
		TagManager = tagManager;

		findFolders(eventsFolder);

		if (MethodsFolder is null)
		{
			Assert.Fail("EventLoader: MethodsFolder is null");
			throw new Exception("MethodsFolder is null");
		}
	}
	public async Task LoadAll(ObservableCollection<IEvent> events)
	{
		var eventsList = await loadEvents(LibraryFolder);
		events.AddAll(eventsList);
	}
	private async Task<List<IEvent>> loadEvents(IStorageFolder folder)
	{
		var events = new List<IEvent>();
		await foreach (var item in folder.GetItemsAsync())
		{
			if (item is IStorageFile file)
			{
				var newEvent = await loadEvent(file);
				if (newEvent is not null)
					events.Add(newEvent);
			}
			else if (item is IStorageFolder subfolder)
			{
				events.AddRange(await loadEvents(subfolder));
			}
		}
		return events;
	}

	private async Task<IEvent?> loadEvent(IStorageFile file)
	{
		await using var stream = await file.OpenReadAsync();
		using var streamReader = new StreamReader(stream);
		var fileContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();

		var eventsPostfix = file.Name.Substring(Event.ID_PREFIX.Length, file.Name.Length - Event.ID_PREFIX.Length);

		return JsonConvert.DeserializeObject<Event>(fileContent, new JsonSerializerSettings
		{
			Converters = {
				new EventConverter(this, IdEventsManager, TagManager),
				new EventReferenceConverter(IdEventsManager),
				new EventContainerReferenceConverter(IdEventsManager),
				new EventSequenceManagerConverter(),
				new ClassReferenceConverter(IdEventsManager),
				new WFTypeConverter(IdEventsManager)
			}
		});

	}


	private async void findFolders(IStorageFolder eventsFolder)
	{
		MethodsFolder = await findFolder(eventsFolder, "Methods");
		LibraryFolder = await findFolder(eventsFolder, "Library");
	}

	private async Task<IStorageFolder> findFolder(IStorageFolder eventsFolder, string foldersName)
	{
		await foreach (var item in eventsFolder.GetItemsAsync())
		{
			if (item is IStorageFolder folder
				&& item.Name == foldersName)
				return folder;
		}

		var newFolder = await eventsFolder.CreateFolderAsync(foldersName);
		if (newFolder is null)
			throw new CannotCreateFolderException("EventLoader: newFolder is null");

		return newFolder;
	}

	private async Task<IStorageFile?> findEventsFile(string objectsId)
	{
		await foreach (var item in LibraryFolder.GetItemsAsync())
		{
			if (item is IStorageFile file
				&& file.Name == objectsId)
				return file; // TODO error
		}
	

		return null;
	}

	public async void Delete(Event event_)
	{
		await findEventsFile(event_.Id + JSON_FILE_POSTFIX).ContinueWith(async task => {
			if (task.Result is not null)
				await task.Result.DeleteAsync();
		});

		event_.Condition.Delete(IdManager, TagManager);
		event_.Action.Delete(IdManager, TagManager);
	}

	public async void Save(Event event_)
	{
		var data = JsonConvert.SerializeObject(event_, Formatting.Indented, new JsonSerializerSettings
		{
			Converters = {
				new ClassReferenceConverter(IdManager),
				new EventReferenceConverter(IdManager),
				new EventContainerReferenceConverter(IdManager),
				new EventSequenceManagerConverter()
			}
		});
		var file = await findEventsFile(event_.Id);
		if (file is null)
			file = await LibraryFolder.CreateFileAsync(event_.Id + ".json");

		if (file is null)
			throw new CannotCreateFolderException("EventLoader: Could not create file " + event_.Id);

		await using var stream = await file.OpenWriteAsync();
		using var streamWriter = new StreamWriter(stream);
		await streamWriter.WriteAsync(data);
		streamWriter.Close();
		stream.Close();
	}
	public async void Rename(Event event_, string oldId)
	{
		var file = await findEventsFile(oldId + JSON_FILE_POSTFIX);
		if (file is not null)
			await file.DeleteAsync();
			
		event_.Condition.Rename(Event.ConditionMethodsName, Event.EVENTS_CLASSES_TAG + "_" + event_.Name); 
		event_.Action.Rename(Event.ActionMethodsName, Event.EVENTS_CLASSES_TAG + "_" + event_.Name);

		Save(event_);
	}
	public IClass CreateNewClass(string name)
	{
		// create new class that is IClassLoader
		var classLoader = new EmptyClassLoader(IdManager, TagManager);
		return new Class(name, classLoader);

	}

	private class EmptyClassLoader : IClassLoader
	{
		public IIDConceptManager IdClassesManager { get; init; }
		public IIDConceptManager IdManager { get; init; }
		public ITagManager TagManager { get; init; }
		public EmptyClassLoader(IIDConceptManager idManager, ITagManager tagManager)
		{
			IdClassesManager = idManager;
			IdManager = new IDClassManager(idManager);
			TagManager = tagManager;
		}

		public void Delete(ILibrary library)
		{
		}

		public void SaveToFile(Class clazz)
		{
		}

		public Task Rename(string newName)
		{
			return Task.CompletedTask;
		}
	}
}
