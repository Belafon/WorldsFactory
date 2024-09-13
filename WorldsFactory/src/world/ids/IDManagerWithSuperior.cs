using System.Collections.ObjectModel;
using WorldsFactory.world.ids;

namespace WorldsFactory.world.ids;

public class IDManagerWithSuperior : IIDConceptManager
{
	public HashSet<Action<ConceptWithID>> OnConceptCreated { get; private set; } = new();
	public HashSet<Action<ConceptWithID>> OnConceptDeleted { get; private set; } = new();
	public Dictionary<string, ConceptWithID> AllIdConcepts { get; private set; } = new();
	public ObservableCollection<ConceptWithID> AllItems { get; private set; } = new(); // TODO remove and use AllIds with AllIdConcepts
	public ObservableCollection<string> AllIds { get; private set; } = new();

	private IIDConceptManager idConceptManagerSuperior;
	
	public IDManagerWithSuperior(IIDConceptManager superior)
	{
		this.idConceptManagerSuperior = superior;
		AllItems.CollectionChanged += (sender, args) =>
		{
			if (args.NewItems != null)
			{
				foreach (var item in args.NewItems)
				{
					if (item is not ConceptWithID concept
						|| !AllIdConcepts.ContainsKey(concept.Id))
						throw new UnmodifiableCollectionChangedException("Attempt to add concept to AllClasses. Use AddObject instead.");
				}
			}
			if (args.OldItems != null)
			{
				foreach (var item in args.OldItems)
				{
					if (item is ConceptWithID concept
						&& AllIdConcepts.ContainsKey(concept.Id))
						throw new UnmodifiableCollectionChangedException ("Attempt to remove concept from AllClasses. Use DeleteObject instead.");
				}
			}
		};
	}



	void IIDConceptManager.AddConcept(ConceptWithID obj)
	{
		if(AllIdConcepts.ContainsKey(obj.Id))
			throw new ConceptWithIDAlreadyExistsException("Object with this id already exists");
		AllIdConcepts.Add(obj.Id, obj);
		idConceptManagerSuperior.AddConcept(obj);
		foreach (var action in OnConceptCreated)
		{
			action(obj);
		}
		AllItems.Add(obj);
		AllIds.Add(obj.Id);
	}

	void IIDConceptManager.DeleteConcept(ConceptWithID obj)
	{
		if(!AllIdConcepts.ContainsKey(obj.Id))
			throw new Exception("Object with this id doesn't exist");
		AllIdConcepts.Remove(obj.Id);
		idConceptManagerSuperior.DeleteConcept(obj);
		foreach (var action in OnConceptDeleted)
		{
			action(obj);
		}
		AllItems.Remove(obj);
		AllIds.Remove(obj.Id);
	}

	ConceptWithID? IIDConceptManager.GetConceptById(string id)
	{
		if(!AllIdConcepts.ContainsKey(id))
			return idConceptManagerSuperior.GetConceptById(id);
		return AllIdConcepts[id];
	}

	internal bool CanBeIdAdded(string id)
	{
		return !AllIdConcepts.ContainsKey(id);
	}

	bool IIDConceptManager.CanBeIdAdded(string id)
	{
		return !AllIdConcepts.ContainsKey(id);
	}

    public bool DoesConceptExist(string id)
    {
        if(AllIdConcepts.ContainsKey(id))
			return true;
		return idConceptManagerSuperior.DoesConceptExist(id);
    }
}
