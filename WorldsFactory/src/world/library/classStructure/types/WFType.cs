using Newtonsoft.Json.Linq;
using Newtonsoft.Json;
using WorldsFactory.world.ids;
using WorldsFactory.world.events;
using WorldsFactory.world.objects;

namespace WorldsFactory.world.library.classStructure.types;

/// <summary>
/// similar to <see cref="Reference{T}"/> but it can also be 
/// a <see cref="BasicType"/>
/// </summary>
public interface WFType
{
	public string Id { get; }
	public string GetPostfixId { get; }
}


public class BasicType : WFType
{
	public const string ID_PREFIX = "@basicType:";
	[JsonProperty("id")]
	public string Id { get; init; }
	[JsonIgnore]
	public string postfixId;
	public BasicType(string postfixId)
	{
		Id = ID_PREFIX + postfixId;
		this.postfixId = postfixId;
	}

	public static WFType Integer = new BasicType("Integer");
	public static WFType String = new BasicType("String");
	public static WFType Boolean = new BasicType("Boolean");
	public static WFType Void = new BasicType("Void");
	
	
	public static Dictionary<BasicTypeName, WFType> AllTypes = new Dictionary<BasicTypeName, WFType>
	{
		{BasicTypeName.Integer, Integer},
		{BasicTypeName.String, String},
		{BasicTypeName.Boolean, Boolean},
		{BasicTypeName.Void, Void}
	};
	public string GetPostfixId => postfixId;
	
	public override string ToString()
	{
		return Id;
	}
}

public class WFTypeConverter : JsonConverter<WFType>
{
	private IIDConceptManager idManager;
	public WFTypeConverter(IIDConceptManager idManager)
	{
		this.idManager = idManager;
	}
	public override WFType? ReadJson(JsonReader reader, Type objectType, WFType? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
	 // if current objet is null, return null
		if (reader.TokenType == JsonToken.Null)
		{
			return null;
		}
		
		JObject jo = JObject.Load(reader);

		if (jo["id"] != null)
		{
			string id = jo["id"]!.Value<string>()!;

			foreach (var basicType in BasicType.AllTypes)
			{
				if (id == basicType.Value.Id)
				{
					return basicType.Value;
				}
			}

			// else it is a reference
			using (var referenceReader = jo.CreateReader())
			{
				var referenceConverter = new ClassReferenceConverter(idManager);
				var reference = referenceConverter.ReadJson(referenceReader, objectType, existingValue, serializer);
				if (reference is WFType type)
				{
					return type;
				}
				Assert.Fail("Reference is not WFType");
			}
		}

		// If "Id" doesn't match any known type, return a default value or throw an exception.
		// You can customize this behavior based on your requirements.

		throw new JsonSerializationException("Unsupported WFType");
	}

	public override void WriteJson(JsonWriter writer, WFType? value, JsonSerializer serializer)
	{
		if(value is Reference<IClass> reference)
		{
			var referenceConverter = new ClassReferenceConverter(idManager);
			referenceConverter.WriteJson(writer, reference, serializer);
		}
		else if (value is BasicType basicType)
		{
			writer.WriteValue(basicType.Id);
		}
		else if (value is Reference<IEvent> eventReference)
		{
			var referenceConverter = new EventReferenceConverter(idManager);
			referenceConverter.WriteJson(writer, eventReference, serializer);
		}
		else if (value is Reference<WFObject> objectReference)
		{
			var referenceConverter = new ObjectReferenceConverter(idManager);
			referenceConverter.WriteJson(writer, objectReference, serializer);
		}
		else
		{
			throw new Exception("Unknown type");
		}
	}

	public override bool CanWrite => false;

	public override bool CanRead => true;
}


public enum BasicTypeName
{
	Integer,
	String,
	Boolean,
	Void
}