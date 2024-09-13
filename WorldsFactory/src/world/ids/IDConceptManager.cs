using System.Collections.ObjectModel;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading.Tasks;

namespace WorldsFactory.world.ids;
public class IDConceptManager : IIDConceptManager
{
	public HashSet<Action<ConceptWithID>> OnConceptCreated { get; private set; } = new HashSet<Action<ConceptWithID>>();
	public HashSet<Action<ConceptWithID>> OnConceptDeleted { get; private set; } = new HashSet<Action<ConceptWithID>>();
	public Dictionary<string, ConceptWithID> AllIdConcepts { get; private set; } = new Dictionary<string, ConceptWithID>();
	public ObservableCollection<ConceptWithID> AllItems { get; private set; } = new ObservableCollection<ConceptWithID>();

    public IDConceptManager()
	{
		AllItems.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is not ConceptWithID concept
						|| !AllIdConcepts.ContainsKey(concept.Id))
						throw new UnmodifiableCollectionChangedException("Attempt to add concept to AllConcepts. Use AddObject instead.");
				}
			}
			if (args.OldItems != null)
			{
				foreach (var item in args.OldItems)
				{
					if (item is ConceptWithID concept
						&& AllIdConcepts.ContainsKey(concept.Id))
						throw new UnmodifiableCollectionChangedException ("Attempt to remove concept from AllConcepts. Use DeleteObject instead.");
				}
			}
		};
	}
	void IIDConceptManager.AddConcept(ConceptWithID obj)
	{
		if (AllIdConcepts.ContainsKey(obj.Id))
			throw new ConceptWithIDAlreadyExistsException("Object with this id already exists");
		AllIdConcepts.Add(obj.Id, obj);
		foreach (var action in OnConceptCreated)
		{
			action(obj);
		}
		AllItems.Add(obj);
	}

	void IIDConceptManager.DeleteConcept(ConceptWithID obj)
	{
		if (!AllIdConcepts.ContainsKey(obj.Id))
			throw new Exception("Object with this id does not exist");
		AllIdConcepts.Remove(obj.Id);
		foreach (var action in OnConceptDeleted)
		{
			action(obj);
		}
		AllItems.Remove(obj);
	}

	ConceptWithID? IIDConceptManager.GetConceptById(string id)
	{
		if(!AllIdConcepts.ContainsKey(id))
			return null;
		return AllIdConcepts[id];
	}

    bool IIDConceptManager.CanBeIdAdded(string id)
    {
		return !AllIdConcepts.ContainsKey(id);
    }

    public bool DoesConceptExist(string id)
    {
		return AllIdConcepts.ContainsKey(id);
    }
}

public class ConceptNotFoundException : Exception
{
	public ConceptNotFoundException(string id) : base("Concept with id " + id + " not found")
	{
	}
}

public class ConceptWithIDAlreadyExistsException : Exception
{
	public ConceptWithIDAlreadyExistsException(string id) : base("Concept with id " + id + " already exists")
	{
	}
}

public class UnmodifiableCollectionChangedException : Exception
{
	public UnmodifiableCollectionChangedException(string message) : base(message)
	{
	}
}
