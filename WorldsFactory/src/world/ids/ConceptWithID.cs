using System.Collections.ObjectModel;
using NP.Utilities;
using Newtonsoft.Json;
using System.ComponentModel;
using WorldsFactory.world.library.classStructure.types;

namespace WorldsFactory.world.ids;

public abstract class ConceptWithID : TaggedConcept, IConceptWithID
{
	[JsonIgnore]
	public virtual string PostfixId => throw new NotImplementedException();
	private IIDConceptManager manager;
	[JsonProperty("id")]
	private string id;
	[JsonIgnore]
	public string Id
	{
		get => id;
		set
		{
			if(!manager.CanBeIdAdded(value))
			{
				throw new ConceptWithIDAlreadyExistsException(value);
			}
			
			if (id is not null && !id.IsNullOrEmpty())
			{
				DeleteFromIdManagers();
			}

			id = value;

			if (value is not null && !value.IsNullOrEmpty())
			{
				manager.AddConcept(this);
			}

			foreach (var concept in IReference.AllReferencesWithoutConcpetFound)
			{
				concept.TryGetConcept();
			}

			OnIdChanged?.Invoke(this, new PropertyChangedEventArgs("Id"));
		}
	}

	public event PropertyChangedEventHandler? OnIdChanged;

	public ConceptWithID(string id, ObservableCollection<string> tags, IIDConceptManager manager, ITagManager tagManager)
		: base(tags, tagManager)
	{
		this.id = "";
		this.manager = manager;
		Id = id;
	}
	protected void DeleteFromIdManagers()
	{
		manager.DeleteConcept(this);
	}

	public override string ToString()
	{
		return Id;
	}
}
