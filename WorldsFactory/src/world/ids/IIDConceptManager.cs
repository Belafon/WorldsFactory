using System.Collections.ObjectModel;
namespace WorldsFactory.world.ids
{
	public interface IIDConceptManager
	{
		internal bool CanBeIdAdded(string id);
		internal void AddConcept(ConceptWithID concept);
		internal void DeleteConcept(ConceptWithID concept);
		internal ConceptWithID? GetConceptById(string id);
        public bool DoesConceptExist(string id);

        public ObservableCollection<ConceptWithID> AllItems { get; }
	}
}