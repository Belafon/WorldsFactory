using System.Collections.ObjectModel;
using WorldsFactory.world.library.classStructure.types;


namespace WorldsFactory.world.objects;
public class Objects : IObjects
{
	public ObservableCollection<WFObject> Collection { get; private set; } = new();
	private IObjectLoader loader;
	public Objects(IObjectLoader loader)
	{
		this.loader = loader;
		loadObjects();
	}

	private async void loadObjects()
	{
		var collection = await loader.Load();
		foreach (var obj in collection)
		{
			obj.OnDelete += (sender, args) => Collection.Remove(obj);
			Collection.Add(obj);
		}
	}

	public void CreateNewObject(string name, WFType type)
	{
		var newObject = loader.CreateNewObject(name, type);
		newObject.OnDelete += (sender, args) => Collection.Remove(newObject);
		Collection.Add(newObject);
	}
}