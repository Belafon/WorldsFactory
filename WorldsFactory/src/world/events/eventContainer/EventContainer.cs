using System;
using System.Collections.ObjectModel;
using Avalonia.Platform.Storage;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using Serilog;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.events.eventContainer;

public class EventContainer : ConceptWithID, NamedConcept
{
	public static readonly string ID_PREFIX = "@eventContainer:";
	public static string GetId(string name) => ID_PREFIX + name;
	public EventContainer(
		string name,
		ObservableCollection<string> tags,
		IEventContainerLoader eventContainerLoader,
		bool isAtFirstLevel = false)
	: base(ID_PREFIX + name, tags, eventContainerLoader.IdManager, eventContainerLoader.TagManager)
	{
		IsAtFirstLevel = isAtFirstLevel;
		this.name = name;
		this.loader = eventContainerLoader;
		loader.Save(this);
		SubContainers.CollectionChanged += (sender, args) => loader.Save(this);
		bindRenamingAndDeletingReferencesToSubContainers();
		bindRenamingAndDeletingReferencesToEvents();
	}

	private void bindRenamingAndDeletingReferencesToEvents()
	{
		foreach (var refEvent in Events)
		{
			BindRenamingAndDeletingReferenceToEvent(refEvent);
		}

		Events.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (var event_ in args.NewItems)
				{
					if (event_ is Reference<IEvent> refEvent)
						BindRenamingAndDeletingReferenceToEvent(refEvent);
				}
			}
			loader.Save(this);
		};
	}

	private void BindRenamingAndDeletingReferenceToEvent(Reference<IEvent> refEvent)
	{
		if (refEvent.GetConceptWithId() is null)
		{
			refEvent.OnConceptFound += (sender, args) =>
			{
				bindRenamingAndDeletingEvent(refEvent);
			};
		}
		else
		{
			bindRenamingAndDeletingEvent(refEvent);
		}
	}

	private void bindRenamingAndDeletingEvent(Reference<IEvent> refEvent)
	{
		if (refEvent.TryGetConcept() is IEvent ev)
		{
			ev.OnDelete += (sender, args) => Events.Remove(refEvent);
			ev.OnDelete += (sender, args) => loader.Save(this);
			ev.OnIdChanged += (sender, args) => loader.Save(this);
		}
	}

	private void bindRenamingAndDeletingReferencesToSubContainers()
	{
		foreach (var refContainer in SubContainers)
		{
			BindRenamingAndDeletingReferenceToSubContainer(refContainer);
		}

		SubContainers.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (var container in args.NewItems)
				{
					if (container is Reference<EventContainer> refContainer)
						BindRenamingAndDeletingReferenceToSubContainer(refContainer);
				}
			}
		};
	}

	private void BindRenamingAndDeletingReferenceToSubContainer(Reference<EventContainer> refContainer)
	{
		if (refContainer.GetConceptWithId() is null)
		{
			refContainer.OnConceptFound += (sender, args) =>
			{
				bindRenamingAndDeletingSubContainer(refContainer);
			};
		}
		else
		{
			bindRenamingAndDeletingSubContainer(refContainer);
		}
	}

	private void bindRenamingAndDeletingSubContainer(Reference<EventContainer> refContainer)
	{
		if (refContainer.TryGetConcept() is EventContainer subContainer)
		{
			subContainer.OnDelete += (sender, args) => SubContainers.Remove(refContainer);
			subContainer.OnDelete += (sender, args) => loader.Save(this);
			subContainer.OnIdChanged += (sender, args) => loader.Save(this);
		}
		else throw new Exception("EventContainer is null");
	}

	private string name = "";
	public string Name
	{
		get => name;
		set
		{
			var oldId = Id;
			var oldName = name;
			name = value;
			try
			{
				Id = ID_PREFIX + value;
			}
			catch (ConceptWithIDAlreadyExistsException e)
			{
				name = oldName;
				throw e;
			}
			loader.Rename(Id, this);
		}
	}

	public override string PostfixId => name;
	public ObservableCollection<Reference<IEvent>> Events { get; } = new();
	public ObservableCollection<Reference<EventContainer>> SubContainers { get; } = new();

	public bool IsAtFirstLevel { get; }
	public event EventHandler? OnDelete;
	[JsonIgnore]
	public IEventContainerLoader loader;

	public void Delete()
	{

		int next = 0;
		int lastCount = Events.Count;
		while (next < Events.Count)
		{
			if (Events[next].TryGetConcept() is IEvent e)
			{
				e.SequenceManager.EventContainer = null;
				if (Events.Count == lastCount)
					next++;
				else
					lastCount = Events.Count;
			}
			else
				next++;
		}

		next = 0;
		lastCount = SubContainers.Count;
		while (next < SubContainers.Count)
		{
			if (SubContainers[next].TryGetConcept() is EventContainer container)
			{
				SubContainers[next].Delete();
				container.Delete();
				if (SubContainers.Count == lastCount)
					next++;
				else
					lastCount = SubContainers.Count;
			}
			else
			{
				next++;
				SubContainers[next].Delete();
			}
		}

		

		loader.Delete();
		this.DeleteFromIdManagers();
		OnDelete?.Invoke(this, new EventArgs());
	}

	public Reference<EventContainer> GetReference()
	{
		return new Reference<EventContainer>(this, loader.IdManager);
	}

}

public class EventContainerLoader : IEventContainerLoader
{
	public IDManagerWithSuperior IdManager { get; }
	public ITagManager TagManager { get; }
	public IStorageFolder Folder { get; }
	public string Id { get; private set; }
	public EventContainerLoader(IDManagerWithSuperior manager, ITagManager tagManager, IStorageFolder storageFolder, string id)
	{
		IdManager = manager;
		TagManager = tagManager;
		Folder = storageFolder;
		Id = id;
	}
	private static string filePostfix = ".json";

	public async Task<EventContainer?> Load()
	{

		var fileName = Id + filePostfix;
		var file = await findFile(fileName);
		if (file is null)
		{
			throw new FileNotFoundException("File not found", fileName);
		}
		return await LoadContainerFromFile(file);
	}


	public async Task<EventContainer?> LoadContainerFromFile(IStorageFile file)
	{
		var stream = await file.OpenReadAsync();
		var streamReader = new StreamReader(stream);
		var fileContent = await streamReader.ReadToEndAsync();
		streamReader.Close();
		stream.Close();

		var container = JsonConvert.DeserializeObject<EventContainer>(fileContent, new JsonSerializerSettings
		{
			Converters = {
				new EventReferenceConverter(IdManager),
				new EventContainerReferenceConverter(IdManager),
				new EventContainerConverter(IdManager, TagManager, Folder)
			}
		});
		return container;
	}

	private async Task<IStorageFile?> findFile(string fileName)
	{
		await foreach (var item in Folder.GetItemsAsync())
		{
			if (item is IStorageFile file && file.Name == fileName)
			{
				return file;
			}
		}
		return null;
	}


	public async void Save(EventContainer container)
	{
		var fileName = container.Id + filePostfix;
		var file = await findFile(fileName);
		if (file is null)
		{
			file = await Folder.CreateFileAsync(fileName);
		}

		if (file is null)
		{
			throw new CannotCreateFileException("EventContainerLoader: file is null");
		}

		var data = JsonConvert.SerializeObject(container, Formatting.Indented, new JsonSerializerSettings
		{
			Converters = {
				new EventReferenceConverter(IdManager),
				new EventContainerReferenceConverter(IdManager),
				new EventContainerConverter(IdManager, TagManager, Folder)
			}
		});

		await using var stream = await file.OpenWriteAsync();
		using var streamWriter = new StreamWriter(stream);
		await streamWriter.WriteAsync(data);
		streamWriter.Close();
		stream.Close();
	}

	public void Delete()
	{
		var fileName = Id + filePostfix;
		var file = findFile(fileName).Result;
		if (file is not null)
			file.DeleteAsync();
	}

	public async void Rename(string newId, EventContainer container)
	{
		var oldId = Id;
		Id = newId;
		var fileName = oldId + filePostfix;
		var file = await findFile(fileName);
		if (file is not null)
			await file.DeleteAsync();

		Save(container);
	}
}


public class EventContainerConverter : JsonConverter<EventContainer>
{
	private IDManagerWithSuperior manager;
	private ITagManager tagManager;
	private IStorageFolder storageFolder;
	public EventContainerConverter(IDManagerWithSuperior manager, ITagManager tagManager, IStorageFolder storageFolder)
	{
		this.manager = manager;
		this.tagManager = tagManager;
		this.storageFolder = storageFolder;
	}
	public override EventContainer? ReadJson(JsonReader reader, Type objectType, EventContainer? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		try
		{
			return ReadJsonSafe(reader, objectType, existingValue, hasExistingValue, serializer);
		}
		catch (Exception e)
		{
			Log.Error(e, "Error while reading EventContainer");
			return null;

		}
	}
	public EventContainer? ReadJsonSafe(JsonReader reader, Type objectType, EventContainer? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
			return null;

		JObject jsonObject = JObject.Load(reader);
		var name = jsonObject["name"]?.Value<string>();
		var tags = jsonObject["tags"]?.ToObject<ObservableCollection<string>>(serializer);
		var id = jsonObject["id"]?.Value<string>();
		var isAtFirstLevelNullable = jsonObject["isAtFirstLevel"]?.Value<bool>();
		if (name is null || tags is null || id is null || isAtFirstLevelNullable is null)
		{
			throw new JsonException("Name, tags or id is null");
		}
		var isAtFirstLevel = isAtFirstLevelNullable.Value;
		var events = jsonObject["events"]?.ToObject<ObservableCollection<Reference<IEvent>>>(serializer);
		var subContainers = jsonObject["subContainers"]?.ToObject<ObservableCollection<Reference<EventContainer>>>(serializer);

		var loader = new EventContainerLoader(manager, tagManager, storageFolder, id);
		var container = new EventContainer(name, tags, loader, isAtFirstLevel);

		foreach (var ev in events!)
		{
			container.Events.Add(ev);
		}
		foreach (var subContainer in subContainers!)
		{
			container.SubContainers.Add(subContainer);
		}
		return container;
	}

	public override void WriteJson(JsonWriter writer, EventContainer? value, JsonSerializer serializer)
	{
		if (value is null)
		{
			writer.WriteNull();
			return;
		}
		writer.WriteStartObject();
		writer.WritePropertyName("name");
		writer.WriteValue(value.Name);
		writer.WritePropertyName("tags");
		serializer.Serialize(writer, value.Tags);
		writer.WritePropertyName("id");
		writer.WriteValue(value.Id);
		writer.WritePropertyName("isAtFirstLevel");
		serializer.Serialize(writer, value.IsAtFirstLevel);
		writer.WritePropertyName("events");
		serializer.Serialize(writer, value.Events);
		writer.WritePropertyName("subContainers");
		serializer.Serialize(writer, value.SubContainers);
		writer.WriteEndObject();
	}
}


public interface IEventContainerLoader
{
	IDManagerWithSuperior IdManager { get; }
	ITagManager TagManager { get; }
	Task<EventContainer?> Load();
	Task<EventContainer?> LoadContainerFromFile(IStorageFile file);
	void Save(EventContainer container);
	void Delete();
	void Rename(string oldId, EventContainer container);
}
