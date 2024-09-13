using System.Collections.ObjectModel;
using System.ComponentModel;
using Newtonsoft.Json;
using WorldsFactory.world.events.eventContainer;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.events;

internal class EventSequenceManager : IEventSequenceManager
{
	public ObservableCollection<Reference<IEvent>> Events { get; private set; } = null!;
	public IEvent Event { get; internal set; } = null!;

	public EventSequenceManager(IEvent _event)
	{
		Event = _event;
		Events = new ObservableCollection<Reference<IEvent>>();
		Events.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is IEvent event_)
						event_.OnDelete += OnEventDeleted!;

					if (Events.Count(e => e.Id == ((Reference<IEvent>)item).Id) > 1)
						throw new ArgumentException("The event is already in the sequence!");
				}
			}
			if (args.OldItems != null)
			{
				foreach (var item in args.OldItems)
				{
					if (item is IEvent event_)
						event_.OnDelete -= OnEventDeleted!;
				}
			}
		};
	}

	internal EventSequenceManager(HashSet<Reference<IEvent>> events)
	{
		this.Events = new ObservableCollection<Reference<IEvent>>(events);
		foreach (var eventReference in events)
		{
			if (eventReference.TryGetConcept() is IEvent event_)
			{
				event_.OnDelete += OnEventDeleted!;
			}
		}
	}

	public void TryToBindAllEventReferencesWithEvents()
	{
		foreach (var eventReference in Events)
		{
			if (eventReference.TryGetConcept() is IEvent event_)
			{
				event_.OnDelete += OnEventDeleted!;
			}
		}
	}

	private void OnEventDeleted(object sender, EventArgs e)
	{
		var deletedEvent = (IEvent)sender;
		Events.Remove(Events.First(e => e.Id == deletedEvent.Id));
		EventContainer = null;
	}

	private Reference<EventContainer>? eventContainer;
	internal Reference<EventContainer>? EventContainerReference
	{
		get => eventContainer;
		set
		{
			eventContainer = value;
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("EventContainerReference"));
		}
	}

	public EventContainer? EventContainer
	{
		get => (EventContainer)eventContainer?.TryGetConcept()!;
		set
		{
			if (Event is null)
				throw new InvalidOperationException("Event is null");

			var oldContainer = EventContainer;

			if (oldContainer == value)
				return;

			removeFromOldContainer(oldContainer);

			if (value is null)
				eventContainer = null;
			else
			{
				eventContainer = value?.GetReference();

				// add only if not already in the container
				if (!value!.Events.Any(ev => ev.Id == Event.Id))
				{
					value!.Events.Add(Event.GetReference());
				}
				
				
				if(eventContainer!.TryGetConcept() is EventContainer container)
					if(!container.Events.Any(ev => ev.Id == Event.Id))
						container.Events.Add(Event.GetReference());
				
				if(EventContainerReference is not null)
					if(EventContainerReference.Id != value.Id)
						EventContainerReference = value.GetReference();
			}
			PropertyChanged?.Invoke(this, new PropertyChangedEventArgs("EventContainer"));
			
			var containerObject = (EventContainer)eventContainer?.TryGetConcept()!;
			if(containerObject is not null){
				containerObject.OnIdChanged += (sender, args) =>
				{
					eventContainer?.Rename(containerObject.Id, containerObject.PostfixId);
				};
			}
		}
	}

	private void removeFromOldContainer(EventContainer? oldContainer)
	{
		if (oldContainer != null)
		{
			var oldRef = oldContainer.Events.FirstOrDefault(ev => ev.Id == Event.Id);
			if (oldRef != null)
			{
				oldContainer.Events.Remove(oldRef);
			}
		}
	}

	public event PropertyChangedEventHandler? PropertyChanged;
}

internal class EventSequenceManagerConverter : JsonConverter<EventSequenceManager>
{
	public override EventSequenceManager ReadJson(JsonReader reader, Type objectType, EventSequenceManager? existingValue, bool hasExistingValue, JsonSerializer serializer)
	{
		// load list of event references and create EventSequenceManager
		var events = new HashSet<Reference<IEvent>>();
		var eventContainerReference = default(Reference<EventContainer>);

		while (reader.Read())
		{
			if (reader.TokenType == JsonToken.PropertyName)
			{
				string propertyName = reader.Value!.ToString()!;
				if (propertyName == "Events")
				{
					reader.Read(); // Move to the start of the array
					while (reader.Read())
					{
						if (reader.TokenType == JsonToken.EndArray)
						{
							break;
						}
						if (reader.TokenType == JsonToken.StartObject)
						{
							var eventReference = serializer.Deserialize<Reference<IEvent>>(reader);
							if (eventReference is not null)
								events.Add(eventReference);
						}
					}
				}
				else if (propertyName == "EventContainerReference")
				{
					reader.Read(); // Move to the start of the object
					eventContainerReference = serializer.Deserialize<Reference<EventContainer>>(reader);
				}
			}
		}

		var newSequence = new EventSequenceManager(events)
		{
			EventContainerReference = eventContainerReference
		};

		return newSequence;
	}

	public override void WriteJson(JsonWriter writer, EventSequenceManager? value, JsonSerializer serializer)
	{
		writer.WriteStartObject();

		writer.WritePropertyName("Events");
		writer.WriteStartArray();
		foreach (var event_ in value!.Events)
		{
			serializer.Serialize(writer, event_);
		}
		writer.WriteEndArray();

		writer.WritePropertyName("EventContainerReference");
		serializer.Serialize(writer, value!.EventContainerReference);

		writer.WriteEndObject();
	}
}