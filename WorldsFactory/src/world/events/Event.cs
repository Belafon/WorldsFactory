using System.ComponentModel;
using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;
using WorldsFactory.project;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure;
using NP.Utilities;
using Serilog;
using Newtonsoft.Json;
using WorldsFactory.world.library.classStructure.types;
using Newtonsoft.Json.Linq;
using WorldsFactory.world.library;
using System.Security;
using WorldsFactory.world.events.eventContainer;

namespace WorldsFactory.world.events;
public class Event : ConceptWithID, IEvent, INotifyPropertyChanged
{
	public const string ConditionMethodsName = "Condition";
	public const string ActionMethodsName = "Action";
	public const string ID_PREFIX = "@event:";
	public const string EVENTS_CLASSES_TAG = "eventsClass";
	public const string EVENTS_CLASSES_TAG_UNDERSCORE = EVENTS_CLASSES_TAG + "_";
	private string name = "";
	public string Name
	{
		get => name;
		set
		{
			var oldName = name;
			var oldId = Id;
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

			loader.Rename(this, oldId);
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Name"));
		}
	}

	public override string PostfixId => Name;

	public IMethod Condition { get; init; }
	public IMethod Action { get; init; }
	public IEventSequenceManager SequenceManager { get; }
	[JsonIgnore]
	public IIDConceptManager IdEventsManager
	{
		get => loader.IdEventsManager;
	}

	public event EventHandler? OnDelete;
	private IEventLoader loader;
	[JsonIgnore]
	public IClass Class { get; private set; }
	public new event PropertyChangedEventHandler? PropertyChanged;

	public Event(
		IEventLoader loader,
		string name) : base(
		ID_PREFIX + name,
		new ObservableCollection<string>(new string[] { "event", "Event" }),
		loader.IdEventsManager,
		loader.TagManager
		)
	{
		SequenceManager = new EventSequenceManager(this);
		this.name = name;
		this.loader = loader;

		Class = loader.CreateNewClass(EVENTS_CLASSES_TAG_UNDERSCORE + name);

		// TODO make a builder for Method
		Condition = new Method(loader.TagManager, loader.IdManager, loader, Class, ConditionMethodsName, BasicType.Boolean, new List<Parameter>());
		Condition.Body!.Code = MethodsBody.GetMethodsHeader(ConditionMethodsName, Condition.Parameters.ToList()) + "\n\treturn False";

		Action = new Method(loader.TagManager, loader.IdManager, loader, Class, ActionMethodsName, BasicType.Void, new List<Parameter>());
		Action.Body!.Code = MethodsBody.GetMethodsHeader(ActionMethodsName, Action.Parameters.ToList()) + "\n\tpass";

		bindOnSave(loader);

		loader.Save(this);
	}

	private void bindOnSave(IEventLoader loader)
	{
		SequenceManager.Events.CollectionChanged += (sender, args) => loader.Save(this);
		SequenceManager.PropertyChanged += (sender, args) => loader.Save(this);
		
		bindOnContainerRenamed();
		SequenceManager.PropertyChanged += (sender, args) =>
		{
			if (args.PropertyName == "EventContainer")
			{
				bindOnContainerRenamed();
			}
		};
	}

	private void bindOnContainerRenamed()
	{

		if (SequenceManager.EventContainer != null)
		{
			SequenceManager.EventContainer!.OnIdChanged += (sender, args) =>
			{
				loader.Save(this);
			};
		}
	}

	internal Event(
		ObservableCollection<string> tags,
		IEventLoader loader,
		string name,
		Method condition,
		Method action,
		IClass clazz,
		EventSequenceManager sequenceManager) : base(
		ID_PREFIX + name,
		tags,
		loader.IdEventsManager,
		loader.TagManager
		)
	{
		SequenceManager = sequenceManager;
		Condition = condition;
		Action = action;
		this.name = name;
		this.loader = loader;

		Class = clazz;


		if (Condition == null)
		{
			Assert.Fail("Condition is null");
			throw new Exception("Condition is null");
		}

		if (Action == null)
		{
			Assert.Fail("Action is null");
			throw new Exception("Action is null");
		}

		bindOnSave(loader);
	}

	public Reference<IEvent> GetReference()
	{
		return new Reference<IEvent>(this, loader.IdManager);
	}

	public void Delete()
	{
		loader.Delete(this);
		DeleteFromIdManagers();
		Class.Delete(null);
		OnDelete?.Invoke(this, new EventArgs());
	}

	public override string ToString()
	{
		return Id;
	}


}

public class EventConverter : JsonConverter<IEvent>
{
	private IEventLoader loader;
	private IIDConceptManager idManager;
	private ITagManager tagManager;
	public EventConverter(IEventLoader loader, IIDConceptManager idManager, ITagManager tagManager)
	{
		this.loader = loader;
		this.idManager = idManager;
		this.tagManager = tagManager;
	}
	public override IEvent? ReadJson(JsonReader reader, Type objectType, IEvent? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
			return null;

		JObject jsonObject = JObject.Load(reader);
		var name = jsonObject["Name"]?.Value<string>();
		if (name is null)
			throw new JsonSerializationException("Expected Name property");

		serializer.Converters.Add(new MethodConverter(idManager, tagManager, loader, Event.EVENTS_CLASSES_TAG_UNDERSCORE + name));

		// load action and condition methods from files
		IClass clazz = loader.CreateNewClass(name);

		Method condition = jsonObject["Condition"]!.ToObject<Method>(serializer)!;
		Method action = jsonObject["Action"]!.ToObject<Method>(serializer)!;

		var tags = jsonObject["tags"]!.ToObject<ObservableCollection<string>>()!;

		// serialize SequenceManager
		var sequenceManager = jsonObject["SequenceManager"]!.ToObject<EventSequenceManager>(serializer)!;

		var newEvent = new Event(tags, loader, name, condition, action, clazz, sequenceManager);
		sequenceManager.Event = newEvent;
		return newEvent;
	}
	public override void WriteJson(JsonWriter writer, IEvent? value, JsonSerializer serializer)
	{
		throw new NotImplementedException();
	}


}
