using System;

namespace WorldsFactory.world.ids;
public class TagManager : ITagManager
{
	public HashSet<Action<TaggedConcept, string>> OnTagAdded { get; private set; } = new HashSet<Action<TaggedConcept, string>>();
	public HashSet<Action<TaggedConcept, string>> OnTagRemoved { get; private set; } = new HashSet<Action<TaggedConcept, string>>();
	public HashSet<Action<TaggedConcept>> OnTaggedObjectCreated { get; private set; } = new HashSet<Action<TaggedConcept>>();
	public Dictionary<string, HashSet<TaggedConcept>> TaggedObjects { get; private set; } = new Dictionary<string, HashSet<TaggedConcept>>();

	void ITagManager.newTaggedObjectCreated(TaggedConcept obj)
	{
		foreach (var tag in obj.Tags)
		{
			if (TaggedObjects.ContainsKey(tag))
			{
				TaggedObjects[tag].Add(obj);
			}
			else
			{
				TaggedObjects.Add(tag, new HashSet<TaggedConcept>() { obj });
			}
		}
	}
	public void AddTag(TaggedConcept obj, string tag)
	{
		if(!TaggedObjects.ContainsKey(tag))
			TaggedObjects.Add(tag, new HashSet<TaggedConcept>());

		TaggedObjects[tag].Add(obj);
		foreach (var action in OnTagAdded)
		{
			action(obj, tag);
		}
	}

	public void RemoveTag(TaggedConcept obj, string tag)
	{
		TaggedObjects[tag].Remove(obj);
		foreach (var action in OnTagRemoved)
		{
			action(obj, tag);
		}
	}
	
	void ITagManager.DeleteObject(TaggedConcept obj)
	{
		foreach (var tag in obj.Tags)
		{
			TaggedObjects[tag].Remove(obj);
		}
	}
}
