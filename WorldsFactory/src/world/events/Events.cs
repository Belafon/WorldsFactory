using System.Collections.ObjectModel;
using Avalonia.Platform.Storage;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.ids;

namespace WorldsFactory.world.events;

public class Events : IEvents
{
	public IEventLoader EventLoader { get; private set; }
	public IEventContainersLoader ContainersLoader { get; private set; }
	public ObservableCollection<IEvent> Collection { get; private set; }
	public ObservableCollection<EventContainer> FirstLevelEventContainers { get; } = new();
	public ObservableCollection<EventContainer> AllEventContainers { get; } = new();
	public Events(IEventLoader eventLoader, IEventContainersLoader containersLoader)
	{
		EventLoader = eventLoader;
		ContainersLoader = containersLoader;
		Collection = new ObservableCollection<IEvent>();
		
		AllEventContainers.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems is not null)
			{
				foreach (EventContainer container in args.NewItems)
				{
					container.OnDelete += (sender, args) => AllEventContainers.Remove(container);
				}
			}
		};
		
		eventLoader.LoadAll(Collection).ContinueWith((task) =>
		{
			setAndLoadAllEventContainers(containersLoader);
			foreach (var ev in Collection)
			{
				ev.OnDelete += (sender, args) => Collection.Remove(ev);
			}
			Collection.CollectionChanged += (sender, args) =>
			{
				if (args.NewItems is not null)
				{
					foreach (IEvent ev in args.NewItems)
					{
						ev.OnDelete += (sender, args) => Collection.Remove(ev);
					}
				}
			};

			foreach (var container in FirstLevelEventContainers)
			{
				container.OnDelete += (sender, args) => FirstLevelEventContainers.Remove(container);
			}
			FirstLevelEventContainers.CollectionChanged += (sender, args) =>
			{
				if (args.NewItems is not null)
				{
					foreach (EventContainer container in args.NewItems)
					{
						container.OnDelete += (sender, args) => FirstLevelEventContainers.Remove(container);
					}
				}
				else if (args.OldItems is not null)
				{
					foreach (EventContainer container in args.OldItems)
					{
						if(containersLoader.IdManager.DoesConceptExist(container.Id))
							container.Delete();
					}
				}
			};
		});
	}

	private async void setAndLoadAllEventContainers(IEventContainersLoader containersLoader)
	{
		var containers = await containersLoader.LoadAll();
		foreach (var container in containers)
		{
			AllEventContainers.Add(container);
			if (container.IsAtFirstLevel)
			{
				FirstLevelEventContainers.Add(container);
			} 
		}
	}

	public EventContainer CreateNewEventContainer(string name, EventContainer? parent)
	{
		bool isAtFirstLevel = parent is null;

		var container = ContainersLoader.CreateNewEventContainer(name, isAtFirstLevel);
		if (parent is not null)
		{
			parent.SubContainers.Add(container.GetReference());
		}
		AllEventContainers.Add(container);
		
		if(parent is null)
		{
			FirstLevelEventContainers.Add(container);
		}

		return container;
	}
}

public interface IEventContainersLoader
{
	EventContainer CreateNewEventContainer(string name, bool isAtFirstLevel);
	public Task<HashSet<EventContainer>> LoadAll();
	public IDManagerWithSuperior IdManager { get; }
	public ITagManager TagManager { get; }

}

public class EventContainersLoader : IEventContainersLoader
{
	public IDManagerWithSuperior IdManager { get; }
	public ITagManager TagManager { get; }
	public IStorageFolder Folder { get; }
	public EventContainersLoader(IIDConceptManager idManager, ITagManager tagManager, IStorageFolder folder)
	{
		IdManager = new IDManagerWithSuperior(idManager);
		TagManager = tagManager;
		Folder = folder;
	}
	
	public async Task<HashSet<EventContainer>> LoadAll()
	{
		var containers = new HashSet<EventContainer>();
		await foreach (var item in Folder.GetItemsAsync())
		{
			if (item is IStorageFile file)
			{
				var containerId = file.Name.Replace(".json", "");
				var loadedContainer = await new EventContainerLoader(IdManager, TagManager, Folder, containerId)
					.LoadContainerFromFile(file);
					
				if(loadedContainer is not null)
					containers.Add(loadedContainer);
				// if the container is null, just skip it
			}
		}
		return containers;
	}

	public EventContainer CreateNewEventContainer(string name, bool isAtFirstLevel)
	{
		var newLoader = new EventContainerLoader(IdManager, TagManager, Folder, EventContainer.GetId(name));
		return new EventContainer(name, new ObservableCollection<string>(), newLoader, isAtFirstLevel);
	}
}