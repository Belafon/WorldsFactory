using System;
using System.Collections.Generic;
using System.ComponentModel;
using System.Linq;
using System.Threading.Tasks;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using ReactiveUI;
using Serilog;
using WorldsFactory.screen;
using WorldsFactory.world.events;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.ids;
using WorldsFactory.world.objects;
using Xunit.Sdk;

namespace WorldsFactory.world.library.classStructure.types;

/// <summary>
/// Reference to a concept with id
/// </summary>
public interface IReference
{
	public static HashSet<IReference> AllReferencesWithoutConcpetFound = new HashSet<IReference>();
	public IConceptWithID? GetConceptWithId();
	[JsonProperty("id")]
	public string Id { get; }
	public string GetPrefixId { get; }
	public string GetPostfixId { get; }
	public IConceptWithID? TryGetConcept();
	public void Rename(string newId, string newName);
	public event PropertyChangedEventHandler? OnConceptFound;
	public event PropertyChangedEventHandler? OnIdChanged;
}

/// <summary>
/// Reference to a concept with id.
/// When the concept is not required, delete method should be called.
/// It can lead to exception due to Object Disposed Exception, when the
/// name of the concept is changed.
/// </summary>
/// <typeparam name="T"></typeparam>
public class Reference<T> : ViewModelBase, WFType, IReference where T : IConceptWithID
{
	/// <summary>
	/// Triggered, also when parent's id changed
	/// Is used by Avalonia to update the UI
	/// </summary>
	private string id;
	public string Id => id;
	public void Rename(string newId, string newName)
	{
		Name = newName;
		this.RaiseAndSetIfChanged(ref id, newId);
		OnIdChanged?.Invoke(this, new PropertyChangedEventArgs("Id"));
	}

	[JsonIgnore]
	private IIDConceptManager idManager { get; init; }
	[JsonIgnore]
	public T? ConceptWithID { get; private set; }
	public event PropertyChangedEventHandler? OnConceptFound;
	public event PropertyChangedEventHandler? OnIdChanged;
	public string GetPrefixId
	{
		get => id.Split(":")[0] + ":";
	}
	public string GetPostfixId
	{
		get => id.Split(":")[1];
	}

	private string name = "";
	[JsonIgnore]
	public string Name
	{
		get
		{
			if (ConceptWithID is null)
				TryGetConcept();

			if (name == "" && ConceptWithID is not null)
				Name = ConceptWithID.PostfixId;
			else if (name == "" && ConceptWithID is null)
				return id;

			return name;
		}
		set
		{
			name = value;
		}
	}

	public Reference(T conceptWithID, IIDConceptManager idManager)
	{
		this.idManager = idManager;
		ConceptWithID = conceptWithID;
		id = conceptWithID.Id;
		bindRenamingId(conceptWithID);

	}

	private PropertyChangedEventHandler? onRenameHandler;
	private void bindRenamingId(T conceptWithID)
	{
		if (onRenameHandler is not null)
			conceptWithID.OnIdChanged -= onRenameHandler;

		onRenameHandler = new PropertyChangedEventHandler((sender, args) =>
		{
			if (sender is NamedConcept conceptWithName)
				Rename(conceptWithID.Id, conceptWithName.Name);
			else Rename(conceptWithID.Id, "");
		});
		conceptWithID.OnIdChanged += onRenameHandler;
	}

	public Reference(string id, IIDConceptManager idManager)
	{
		this.id = id;
		this.idManager = idManager;
		var concept = TryGetConcept();
		if (concept is not null && concept is T conceptWithID)
		{
			ConceptWithID = conceptWithID;
			Name = conceptWithID.PostfixId;
			bindRenamingId(conceptWithID);
		}
		else
			IReference.AllReferencesWithoutConcpetFound.Add(this);
	}

	/// <summary>
	/// Returns currently saved concept
	/// </summary>
	/// <returns>Currently saved concept</returns>
	public IConceptWithID? GetConceptWithId()
	{
		return ConceptWithID;
	}
	public override string ToString()
	{
		return Id;
	}

	/// <summary>
	/// Tries to find corresponding concept in idManager
	/// </summary>
	/// <returns>Concept with corresponding id</returns>
	public IConceptWithID? TryGetConcept()
	{
		if (ConceptWithID is null)
		{
			// try to load the concept
			var loadedConcept = idManager.GetConceptById(id);
			if (loadedConcept is not null
				&& loadedConcept is T loadedClass)
			{
				ConceptWithID = loadedClass;
				Name = loadedClass.PostfixId;

				bindRenamingId(loadedClass);
				IReference.AllReferencesWithoutConcpetFound.Remove(this);
				OnConceptFound?.Invoke(this, new PropertyChangedEventArgs("ConceptWithID"));
			}
		}
		return ConceptWithID;
	}

	/// <summary>
	/// Should be called when the concept is not required anymore
	/// It unbinds the renaming event handler
	/// </summary>
	public void Delete()
	{
		if (onRenameHandler is not null
			&& ConceptWithID is not null)
			ConceptWithID.OnIdChanged -= onRenameHandler;
	}

	public Reference<T> Clone()
	{
		return new Reference<T>(id, idManager);
	}
}

public class ClassReferenceConverter : JsonConverter<Reference<IClass>>
{
	private IIDConceptManager idManager;
	public ClassReferenceConverter(IIDConceptManager idManager)
	{
		this.idManager = idManager;
	}

	public override Reference<IClass>? ReadJson(JsonReader reader, Type objectType, Reference<IClass>? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
		{
			return null;
		}

		JObject jsonObject = JObject.Load(reader);
		var referenceType = jsonObject["referenceType"]!.Value<string?>();
		var id = jsonObject["id"]!.Value<string?>();

		if (referenceType == null || id == null)
		{
			throw new NullReferenceException("Reference type or id is null");
		}

		if (referenceType == "Class")
			return new Reference<IClass>(id, idManager);
		else
			throw new Exception("Unknown reference type");
	}

	public override void WriteJson(JsonWriter writer, Reference<IClass>? value, JsonSerializer serializer)
	{
		if (value is not IReference)
		{
			throw new Exception("Value is not a reference");
		}
		IReference reference = (IReference)value;
		ReferenceType? referenceType;
		if (reference.GetPrefixId == Class.ID_PREFIX)
			referenceType = ReferenceType.Class;
		else
			throw new Exception("Unknown type");

		var json = new JObject
		{
			{ "referenceType", referenceType!.ToString()},
			{ "id", reference.Id}
		};
		json.WriteTo(writer);
	}
}

public class EventReferenceConverter : JsonConverter<Reference<IEvent>>
{
	private IIDConceptManager idManager;
	public EventReferenceConverter(IIDConceptManager idManager)
	{
		this.idManager = idManager;
	}

	public override Reference<IEvent>? ReadJson(JsonReader reader, Type objectType, Reference<IEvent>? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
		{
			return null;
		}

		JObject jsonObject = JObject.Load(reader);
		var referenceType = jsonObject["referenceType"]!.Value<string?>();
		var id = jsonObject["id"]!.Value<string?>();

		if (referenceType == null || id == null)
		{
			throw new NullReferenceException("Reference type or id is null");
		}

		if (referenceType == "Event")
			return new Reference<IEvent>(id, idManager);
		else
			throw new Exception("Unknown reference type");
	}

	public override void WriteJson(JsonWriter writer, Reference<IEvent>? value, JsonSerializer serializer)
	{
		if (value is not IReference)
		{
			throw new Exception("Value is not a reference");
		}
		IReference reference = (IReference)value;
		ReferenceType? referenceType;
		if (reference.GetPrefixId == Event.ID_PREFIX)
			referenceType = ReferenceType.Event;
		else
			throw new Exception("Unknown type");

		var json = new JObject
		{
			{ "referenceType", referenceType!.ToString()},
			{ "id", reference.Id}
		};
		json.WriteTo(writer);
	}
}

public class ObjectReferenceConverter : JsonConverter<Reference<WFObject>>
{
	private IIDConceptManager idManager;
	public ObjectReferenceConverter(IIDConceptManager idManager)
	{
		this.idManager = idManager;
	}

	public override Reference<WFObject>? ReadJson(JsonReader reader, Type objectType, Reference<WFObject>? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
		{
			return null;
		}

		JObject jsonObject = JObject.Load(reader);
		var referenceType = jsonObject["referenceType"]!.Value<string?>();
		var id = jsonObject["id"]!.Value<string?>();

		if (referenceType == null || id == null)
		{
			throw new NullReferenceException("Reference type or id is null");
		}

		if (referenceType == "Object")
			return new Reference<WFObject>(id, idManager);
		else
			throw new Exception("Unknown reference type");
	}

	public override void WriteJson(JsonWriter writer, Reference<WFObject>? value, JsonSerializer serializer)
	{
		if (value is not IReference)
		{
			throw new Exception("Value is not a reference");
		}
		IReference reference = (IReference)value;
		ReferenceType? referenceType;
		if (reference.GetPrefixId == WFObject.ID_PREFIX)
			referenceType = ReferenceType.Object;
		else
			throw new Exception("Unknown type");

		var json = new JObject
		{
			{ "referenceType", referenceType!.ToString()},
			{ "id", reference.Id}
		};
		json.WriteTo(writer);
	}

}

public class EventContainerReferenceConverter : JsonConverter<Reference<EventContainer>>
{
	private IIDConceptManager idManager;
	public EventContainerReferenceConverter(IIDConceptManager idManager)
	{
		this.idManager = idManager;
	}

	public override Reference<EventContainer>? ReadJson(JsonReader reader, Type objectType, Reference<EventContainer>? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		Reference<EventContainer>? refContainer;
		try
		{
			refContainer = ReadJsonSafe(reader, objectType, existingValue, hasExistingValue, serializer);
		}
		catch (Exception e)
		{
			Log.Error(e, "Error while reading EventContainer reference");
			return null;
		}
		return refContainer;
	}
	public Reference<EventContainer>? ReadJsonSafe(JsonReader reader, Type objectType, Reference<EventContainer>? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
		{
			return null;
		}

		JObject jsonObject = JObject.Load(reader);
		var referenceType = jsonObject["referenceType"]!.Value<string?>();
		var id = jsonObject["id"]!.Value<string?>();

		if (referenceType == null || id == null)
		{
			throw new NullReferenceException("Reference type or id is null");
		}

		if (referenceType == "EventContainer")
			return new Reference<EventContainer>(id, idManager);
		else
			throw new Exception("Unknown reference type");
	}

	public override void WriteJson(JsonWriter writer, Reference<EventContainer>? value, JsonSerializer serializer)
	{
		if (value is not IReference)
		{
			throw new Exception("Value is not a reference");
		}
		IReference reference = (IReference)value;
		ReferenceType? referenceType;
		if (reference.GetPrefixId == EventContainer.ID_PREFIX)
			referenceType = ReferenceType.EventContainer;
		else
			throw new Exception("Unknown type");

		var json = new JObject
		{
			{ "referenceType", referenceType!.ToString()},
			{ "id", reference.Id}
		};
		
		// Ensure writer is not in the middle of writing another object
		if (writer.WriteState != WriteState.Object)
		{
			json.WriteTo(writer);
		}
		else
		{
			// Handle the situation where writer is already writing an object
			// For example, you might want to finish the current object before starting a new one
			writer.WriteEndObject();
			json.WriteTo(writer);
		}
	}
}

public enum ReferenceType
{
	Class,
	Object,
	Method,
	Event,
	EventContainer
}