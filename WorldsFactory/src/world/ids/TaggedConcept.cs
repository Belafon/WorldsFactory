using System;
using System.Collections.ObjectModel;
using WorldsFactory.screen;
using System.Collections.Specialized;

namespace WorldsFactory.world.ids;
public class TaggedConcept : ViewModelBase
{
	public TaggedConcept(ObservableCollection<string> tags, ITagManager manager)
	{
		Tags = tags;
		manager.newTaggedObjectCreated(this);
		Tags.CollectionChanged += (sender, e) =>
		{
			if (e.Action == NotifyCollectionChangedAction.Add)
			{
				foreach (string tag in e.NewItems!)
				{
					manager.AddTag(this, tag);
				}
			}
			else if (e.Action == NotifyCollectionChangedAction.Remove)
			{
				foreach (string tag in e.OldItems!)
				{
					manager.RemoveTag(this, tag);
				}
			}
		};
	}
	[Newtonsoft.Json.JsonProperty("tags")]
	public ObservableCollection<string> Tags { get; init; }

}
