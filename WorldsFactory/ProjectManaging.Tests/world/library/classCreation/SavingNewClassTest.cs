using Newtonsoft.Json;
using Newtonsoft.Json.Linq;

using WorldsFactory.world.library.classStructure;
using WorldsFactory.world.ids;
using WorldsFactory.world.library;
using WorldsFactory.world.library.classStructure.types;
using Avalonia.Platform.Storage;

namespace tests.world.library.classCreation;

public class NewClassCreationTest
{
	ITagManager tagManager = new TagManager();
	IDConceptManager idConceptManager = new IDConceptManager();
	IDClassesManager classManager;
	
	private Class clazz;
	private Class clazz2;
	public NewClassCreationTest()
	{
		classManager = new IDClassesManager(idConceptManager);
		var loader = new ClassLoader(null!, classManager, tagManager);
		clazz = new Class("coolClass", loader)
		{
			Description = "description"
		};

		var loader2 = new ClassLoader(null!, classManager, tagManager);
		clazz2 = new Class("coolClass2", loader2)
		{
			Description = "description2"
		};
	}

	[Fact]
	public void SerializationBasicClassAndDeserialization()
	{
		Assert.NotNull(clazz);


		var classConverter = new ClassConverter(tagManager, classManager, null!, null!);
		string json = JsonConvert.SerializeObject(clazz, Formatting.Indented, classConverter);
		
		Assert.NotNull(json);
		
		var idConceptManager2 = new IDConceptManager();
		var classManager2 = new IDClassesManager(idConceptManager2);
		var classConverter2 = new ClassConverter(tagManager, classManager2, null!, null!);

		Class? deserializedClass = JsonConvert.DeserializeObject<Class>(json, classConverter2);
		Assert.NotNull(deserializedClass);
		Assert.Equal(clazz.Id, deserializedClass.Id);
		Assert.Equal(clazz.Tags.Count, deserializedClass.Tags.Count);
		for (int i = 0; i < clazz.Tags.Count; i++)
		{
			Assert.Equal(clazz.Tags.ElementAt(i), deserializedClass.Tags.ElementAt(i));
		}
		Assert.Equal(clazz.Description, deserializedClass.Description);
	}
}
