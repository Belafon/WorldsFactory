using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.Collections.ObjectModel;
using WorldsFactory.world.ids;
using WorldsFactory.world.library.classStructure.types;
using System.Text.RegularExpressions;
using Serilog;
using WorldsFactory.world.library;



namespace WorldsFactory.world.library.classStructure;
public class Property : ConceptWithID, NamedConcept
{
	public readonly static Regex PropertyNameStructurePattern = new Regex(@"^([a-zA-Z_][a-zA-Z0-9_]*)((\[\d*\])*)$");

	[JsonIgnore]
	public List<Action> OnDelete { get; init; } = new List<Action>();

	private string name;
	[JsonProperty("name")]
	public string Name
	{
		get => name;
		private set
		{
			RaiseAndSetIfChanged(ref name, value);
			FullName = value + StringArrayBrackets;
		}
	}
	
	public void Rename(string newName, string classIdPrefix)
	{
		var newId = GetPrefix(classIdPrefix) + newName;
		if(newId != Id)
		{
			var oldName = Name;
			Name = newName;
			try
			{
			   Id = newId;
			} catch (ConceptWithIDAlreadyExistsException e){
				Name = oldName;
				throw e;
			}
		}
	}
	
	[JsonIgnore]
	public List<int> ArrayBrackets { get; private set; } = new List<int>();

	private string stringArrayBrackets = "";
	[JsonProperty("arrayBrackets")]
	public string StringArrayBrackets
	{
		get => stringArrayBrackets;
		set
		{
			if (stringArrayBrackets == value)
				return;

			FullName = Name + value;
			value = value.Replace("[]", "[0]");
			string[] splitArray = value.Split(new char[] { '[', ']' }, StringSplitOptions.RemoveEmptyEntries);
			this.ArrayBrackets = splitArray
				.Where(x => int.TryParse(x, out _))
				.Select(int.Parse)
				.ToList();
			RaiseAndSetIfChanged(ref stringArrayBrackets, value);
		}
	}

	private string fullName = "";
	[JsonIgnore]
	public string FullName
	{
		get => fullName;
		set => RaiseAndSetIfChanged(ref fullName, value);
	}

	private Property(
		string name,
		WFType type,
		ObservableCollection<string> tags,
		IIDConceptManager idManager,
		ITagManager tagManager,
		string classesPostfixId,
		string stringBrackets)
		: base(GetPrefix(classesPostfixId) + name, tags, idManager, tagManager)
	{
		Type = type;
		
		this.name = "";
		Name = name;
		this.StringArrayBrackets = stringBrackets;
		AddTag("Property");
		AddTag("property");
	}
	
	public void AddTag(string tag)
	{
		if(!Tags.Contains(tag))
			Tags.Add(tag);
	}

	private WFType type = null!;

	[JsonProperty("type")]
	public WFType Type
	{
		get => type;
		set {
			RaiseAndSetIfChanged(ref type, value);
			
		}
	}

	public static string GetPrefix(string classesPostfixId)
	{
		return "@property:" + classesPostfixId + "_";
	}

	public override string ToString()
	{
		return Id;
	}

	public void Delete(IIDConceptManager idManager, ITagManager tagManager)
	{
		for (int i = 0; i < OnDelete.Count; i++)
		{
			OnDelete[i]();
		}
		idManager.DeleteConcept(this);
		tagManager.DeleteObject(this);
	}

	public class Builder
	{
		private string? name;
		private WFType? type;
		private ObservableCollection<string> tags = new ObservableCollection<string>();
		private IIDConceptManager? manager;
		private ITagManager? tagManager;
		private string? classesPostfixId;
		private List<int> arrayBrackets = new List<int>();
		private string stringArrayBrackets = "";

		public Builder SetClassesPostfixId(string classesPostfixId)
		{
			this.classesPostfixId = classesPostfixId;
			return this;
		}
		public Builder AddTag(string tag)
		{
			tags.Add(tag);
			return this;
		}

		public Builder SetTags(IEnumerable<string> tags)
		{
			foreach (string tag in tags)
			{
				this.tags.Add(tag);
			}
			return this;
		}

		public Builder SetName(string name)
		{
			this.name = name;
			return this;
		}

		public Builder SetType(WFType type)
		{
			this.type = type;
			return this;
		}

		public Builder SetIdManager(IIDConceptManager manager)
		{
			this.manager = manager;
			return this;
		}

		public Builder SetTagManager(ITagManager tagManager)
		{
			this.tagManager = tagManager;
			return this;
		}

		public Builder SetArrayBrackets(string arrayBrackets)
		{
			stringArrayBrackets = arrayBrackets;
			return this;
		}

		public Property Build()
		{
			if (name == null || type == null || tags == null || manager == null || tagManager == null || classesPostfixId == null)
			{
				throw new NullReferenceException("One of the fields is null");
			}
			return new Property(name, type, tags, manager, tagManager, classesPostfixId, stringArrayBrackets);
		}

	}
}

public class PropertyConverter : JsonConverter<Property>
{
	private IIDConceptManager idManager;
	private ITagManager tagManager;
	private string classesPostfixId;

	public PropertyConverter(IIDConceptManager idManager, ITagManager tagManager, string postfixId)
	{
		this.idManager = idManager;
		this.tagManager = tagManager;
		this.classesPostfixId = postfixId;
	}

	public override Property? ReadJson(JsonReader reader, Type objectType, Property? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		try
		{
			return ReadJsonCatched(reader, objectType, existingValue, hasExistingValue, serializer);
		}
		catch (Exception)
		{
			Log.Error("Error while reading property, cannot deserialize it");
			return null;
		}
	}

	public Property? ReadJsonCatched(JsonReader reader, Type objectType, Property? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{	
		if (reader.TokenType == JsonToken.Null)
		{
			return null;
		}


		var jsonObject = JObject.Load(reader);

		string id = jsonObject["id"]!.Value<string>()!;
		string name = jsonObject["name"]!.Value<string>()!;
		WFType type = jsonObject["type"]!.ToObject<WFType>(serializer)!;
		ObservableCollection<string> tags = jsonObject["tags"]!.ToObject<ObservableCollection<string>>(serializer)!;
		string stringArrayBrackets = jsonObject["arrayBrackets"]!.ToObject<string>(serializer)!;

		Property property = new Property.Builder()
			.SetClassesPostfixId(classesPostfixId)
			.SetName(name)
			.SetType(type)
			.SetTags(tags)
			.SetIdManager(idManager)
			.SetTagManager(tagManager)
			.SetArrayBrackets(stringArrayBrackets)
			.Build();

		return property;
	}

	public override void WriteJson(JsonWriter writer, Property? value, JsonSerializer serializer)
	{
		throw new NotImplementedException("Writing is not supported in this example.");
	}


}