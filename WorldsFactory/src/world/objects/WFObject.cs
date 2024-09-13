using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using System.ComponentModel;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure.types;
using WorldsFactory.project;
using ReactiveUI;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure;
using Serilog;

namespace WorldsFactory.world.objects;

/**
 * Represents an object
 *
 * It has an initialize method, whose id has insead of name of a class
 * a object prefix @object:, the name of the mehthod is constructed from the name of the object
 * with postfix _initialize
 */
public class WFObject : ConceptWithID, INotifyPropertyChanged, NamedConcept
{
	public const string ID_PREFIX = "@object:";
	private IObjectLoader loader;
	public WFType Type { get; init; }
	private string name = null!;
	public string Name
	{
		get => name;
		set
		{
			if (name == value)
				return;

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

			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Name"));
			Title = ToString();
			loader.Rename(oldId, this, InitMethod);
		}
	}


	public new event PropertyChangedEventHandler? PropertyChanged;
	public override string PostfixId => Name;
	

	public event EventHandler? OnDelete;
	public WFObject(string name, WFType type, IIDConceptManager manager, ITagManager tagManager, IObjectLoader loader, IMethod? initMethod)
		: base(ID_PREFIX + name, new ObservableCollection<string>(
			new string[] { "object", "Object" }
		), manager, tagManager)
	{
		this.loader = loader;

		Type = type;
		if(type is Reference<IClass> classRef){
			classRef.OnIdChanged += (sender, args) => {
				Title = ToString();
			};
			
			classRef.OnConceptFound += (sender, args) => {
				classRef.OnIdChanged += (sender, args) => {
					Title = ToString();
				};
			};
		}

		this.name = name;
		Title = ToString();

		if (initMethod is null)
		{
			InitMethod = loader.CreateInitMethod(this);
			loader.Save(this);
		}
		else
			this.initMethod = initMethod;
			
	}

	public void Delete()
	{
		loader.DeleteObject(this);
		DeleteFromIdManagers();
		OnDelete?.Invoke(this, new EventArgs());
	}

	public override string ToString()
	{
		return Name + " - " + Type.GetPostfixId;
	}
	
	private string title = "";
	public string Title
	{
		get => title;
		set
		{
			title = value;
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("Title"));
		}
	}

	private IMethod initMethod = null!;
	public IMethod InitMethod
	{
		get => initMethod;
		set
		{
			if (initMethod == value)
				return;

			initMethod = value;
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("InitMethod"));
		}
	}
	public Reference<WFObject> GetReference()
	{
		return new Reference<WFObject>(this, loader.IdManager);
	}
}



internal class WFObjectConverter : JsonConverter<WFObject>
{
	private IObjectLoader loader;
	private IIDConceptManager idManager;
	private ITagManager tagManager;
	private ILibraryLoader methodLoader;

	public WFObjectConverter(IIDConceptManager idManager, ITagManager tagManager, IObjectLoader loader, ILibraryLoader methodLoader)
	{
		this.idManager = idManager;
		this.tagManager = tagManager;
		this.loader = loader;
		this.methodLoader = methodLoader;
	}

	public override WFObject? ReadJson(JsonReader reader, Type objectType, WFObject? existingValue, bool hasExistingValue, JsonSerializer serializer){
		return ReadJsonAsync(reader, objectType, existingValue, hasExistingValue, serializer);
	}
	public WFObject? ReadJsonAsync(JsonReader reader, Type objectType, WFObject? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		if (reader.TokenType == JsonToken.Null)
		{
			throw new JsonSerializationException("Object cannot be null");
		}

		// Check if the current token is the start of an object
		if (reader.TokenType != JsonToken.StartObject)
		{
			throw new JsonSerializationException("Expected start of object.");
		}

		string? id = null;
		string? name = null;
		WFType? type = null;
		IMethod? initMethod = null;

		reader.Read();

		while (reader.TokenType != JsonToken.EndObject)
		{
			// If the current token is a property name
			if (reader.TokenType == JsonToken.PropertyName)
			{
				string propertyName = reader.Value!.ToString()!;

				// Read the next token
				reader.Read();

				if (propertyName == "Name")
				{
					name = reader.Value.ToString()!;
				}
				else if (propertyName == "id")
				{
					id = reader.Value.ToString()!;
				}
				else if (propertyName == "Type")
				{
					type = serializer.Deserialize<WFType>(reader)!;
				}
				else if (propertyName == "InitMethod")
				{
					if(id is null)
						throw new JsonSerializationException("Id must be read before InitMethod");

					var methodSerializer = new MethodConverter(idManager, tagManager, methodLoader, WFObject.ID_PREFIX);
					serializer.Converters.Add(methodSerializer);

					initMethod = serializer.Deserialize<Method>(reader)!;
				}
			}
			
			// Read the next token
			reader.Read();
		}

		if(id is null){
			Log.Error("Object cannot be loaded, because id is missing in the file");
			throw new JsonSerializationException("Object cannot be loaded, because id is missing in the file");
		}

		if (name is null || type is null)
		{
			Log.Error("Object with name {id} could not be loaded", id);
			throw new JsonSerializationException("Object with name " + id + " could not be loaded");
		}
		
		// Create a new WFObject with the read values
		WFObject wfObject;
		try
		{
			wfObject = new WFObject(name!, type!, idManager, tagManager, loader, initMethod);
		}
		catch (ConceptWithIDAlreadyExistsException)
		{
			Log.Warning("Object with name {name} already exists", name);
			throw new JsonSerializationException("Object with name " + name + " already exists");
		}

		return wfObject;
	}


	public override void WriteJson(JsonWriter writer, WFObject? value, JsonSerializer serializer)
	{
		string json = JsonConvert.SerializeObject(value, Formatting.Indented, new JsonSerializerSettings
		{
			Converters = {
				new WFObjectConverter(idManager, tagManager, loader, methodLoader),
				new WFTypeConverter(idManager),
				new ClassReferenceConverter(idManager),
				new ObjectReferenceConverter(idManager),
			}
		});
		writer.WriteRawValue(json);
	}
}